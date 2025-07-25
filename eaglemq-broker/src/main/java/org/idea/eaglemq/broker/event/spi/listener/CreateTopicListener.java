package org.idea.eaglemq.broker.event.spi.listener;

import com.alibaba.fastjson.JSON;
import com.sun.corba.se.pept.broker.Broker;
import io.netty.channel.ChannelHandlerContext;
import org.idea.eaglemq.broker.cache.CommonCache;
import org.idea.eaglemq.broker.event.model.CreateTopicEvent;
import org.idea.eaglemq.broker.model.CommitLogModel;
import org.idea.eaglemq.broker.model.EagleMqTopicModel;
import org.idea.eaglemq.broker.model.QueueModel;
import org.idea.eaglemq.broker.utils.LogFileNameUtil;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.constants.BrokerConstants;
import org.idea.eaglemq.common.dto.CreateTopicReqDTO;
import org.idea.eaglemq.common.enums.BrokerClusterModeEnum;
import org.idea.eaglemq.common.enums.BrokerEventCode;
import org.idea.eaglemq.common.enums.BrokerResponseCode;
import org.idea.eaglemq.common.event.Listener;
import org.idea.eaglemq.common.utils.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author idea
 * @create 2024/7/3 08:08
 * @description 创建topic监听器
 */
public class CreateTopicListener implements Listener<CreateTopicEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateTopicListener.class);

    @Override
    public void onReceive(CreateTopicEvent event) throws Exception {
        CreateTopicReqDTO createTopicReqDTO = event.getCreateTopicReqDTO();
        AssertUtils.isTrue(createTopicReqDTO.getQueueSize() > 0 && createTopicReqDTO.getQueueSize() < 100, "queueSize参数异常");
        EagleMqTopicModel eagleMqTopicModel = CommonCache.getEagleMqTopicModelMap().get(createTopicReqDTO.getTopic());
        AssertUtils.isTrue(eagleMqTopicModel == null, "topic已经存在");
        createTopicFile(createTopicReqDTO);
        addTopicInCommonCache(createTopicReqDTO);
        loadFileInMMap(createTopicReqDTO);
        LOGGER.info("topic:{} is created! queueSize is {}", createTopicReqDTO.getTopic(), createTopicReqDTO.getQueueSize());
        event.getChannelHandlerContext().write(new TcpMsg(BrokerResponseCode.CREATED_TOPIC_SUCCESS.getCode(),"success".getBytes()));
        if(BrokerClusterModeEnum.MASTER_SLAVE.getCode().equals(CommonCache.getGlobalProperties().getBrokerClusterMode())
        && "master".equals(CommonCache.getGlobalProperties().getBrokerClusterRole())) {
            //主节点，需要将创建topic请求同步给到从节点
            for (ChannelHandlerContext slaveChannel : CommonCache.getSlaveChannelMap().values()) {
                slaveChannel.writeAndFlush(new TcpMsg(BrokerEventCode.CREATE_TOPIC.getCode(), JSON.toJSONBytes(createTopicReqDTO)));
            }
        }
    }

    /**
     * 创建topic对应的文件
     *
     * @param createTopicReqDTO
     * @throws IOException
     */
    public static void createTopicFile(CreateTopicReqDTO createTopicReqDTO) throws IOException {
        String baseCommitLogDirPath = LogFileNameUtil.buildCommitLogBasePath(createTopicReqDTO.getTopic());
        File commitLogDir = new File(baseCommitLogDirPath);
        commitLogDir.mkdir();
        File commitLogFile = new File(baseCommitLogDirPath + BrokerConstants.SPLIT + LogFileNameUtil.buildFirstCommitLogName());
        commitLogFile.createNewFile();

        String baseConsumeQueueDirPath = LogFileNameUtil.buildConsumeQueueBasePath(createTopicReqDTO.getTopic());
        File consumeQueueDir = new File(baseConsumeQueueDirPath);
        consumeQueueDir.mkdir();
        for (int i = 0; i < createTopicReqDTO.getQueueSize(); i++) {
            new File(baseConsumeQueueDirPath + BrokerConstants.SPLIT + i).mkdir();
            new File(baseConsumeQueueDirPath + BrokerConstants.SPLIT + i + BrokerConstants.SPLIT + LogFileNameUtil.buildFirstConsumeQueueName())
                    .createNewFile();
        }
    }

    /**
     * 加载文件到mmap中
     *
     * @param createTopicReqDTO
     * @throws IOException
     */
    public static void loadFileInMMap(CreateTopicReqDTO createTopicReqDTO) throws IOException {
        CommonCache.getCommitLogAppendHandler().prepareMMapLoading(createTopicReqDTO.getTopic());
        CommonCache.getConsumeQueueAppendHandler().prepareConsumeQueue(createTopicReqDTO.getTopic());
    }

    /**
     * 添加topic到缓存中
     *
     * @param createTopicReqDTO
     */
    public static void addTopicInCommonCache(CreateTopicReqDTO createTopicReqDTO) {
        EagleMqTopicModel eagleMqTopicModel = new EagleMqTopicModel();
        eagleMqTopicModel.setTopic(createTopicReqDTO.getTopic());
        long currentTimeStamp = System.currentTimeMillis();
        eagleMqTopicModel.setCreateAt(currentTimeStamp);
        eagleMqTopicModel.setUpdateAt(currentTimeStamp);
        CommitLogModel commitLogModel = new CommitLogModel();
        commitLogModel.setFileName(LogFileNameUtil.buildFirstCommitLogName());
        commitLogModel.setOffsetLimit(BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE.longValue());
        commitLogModel.setOffset(new AtomicInteger(0));
        eagleMqTopicModel.setCommitLogModel(commitLogModel);
        List<QueueModel> queueList = new ArrayList<>();
        for (int i = 0; i < createTopicReqDTO.getQueueSize(); i++) {
            QueueModel queueModel = new QueueModel();
            queueModel.setId(i);
            queueModel.setFileName(LogFileNameUtil.buildFirstConsumeQueueName());
            queueModel.setOffsetLimit(BrokerConstants.COMSUMEQUEUE_DEFAULT_MMAP_SIZE);
            queueModel.setLastOffset(0);
            queueModel.setLatestOffset(new AtomicInteger(0));
            queueList.add(queueModel);
        }
        eagleMqTopicModel.setQueueList(queueList);
        CommonCache.getEagleMqTopicModelList().add(eagleMqTopicModel);
    }

}
