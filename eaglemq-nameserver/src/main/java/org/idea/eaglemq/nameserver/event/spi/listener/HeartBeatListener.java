package org.idea.eaglemq.nameserver.event.spi.listener;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.dto.HeartBeatDTO;
import org.idea.eaglemq.common.dto.ServiceRegistryRespDTO;
import org.idea.eaglemq.common.enums.NameServerResponseCode;
import org.idea.eaglemq.common.event.Listener;
import org.idea.eaglemq.nameserver.common.CommonCache;
import org.idea.eaglemq.nameserver.enums.ReplicationMsgTypeEnum;
import org.idea.eaglemq.nameserver.event.model.HeartBeatEvent;
import org.idea.eaglemq.nameserver.event.model.ReplicationMsgEvent;
import org.idea.eaglemq.nameserver.store.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @Author idea
 * @Date: Created in 14:44 2024/5/4
 * @Description 心跳包处理
 */
public class HeartBeatListener implements Listener<HeartBeatEvent> {

    private static Logger logger = LoggerFactory.getLogger(HeartBeatListener.class);

    @Override
    public void onReceive(HeartBeatEvent event) throws IllegalAccessException {
        //把存在的实例保存下来
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        //之前做过认证
        Object reqId = channelHandlerContext.attr(AttributeKey.valueOf("reqId")).get();
        if (reqId == null) {
            ServiceRegistryRespDTO serviceRegistryRespDTO = new ServiceRegistryRespDTO();
            serviceRegistryRespDTO.setMsgId(event.getMsgId());
            TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode(),
                    JSON.toJSONBytes(serviceRegistryRespDTO));
            channelHandlerContext.writeAndFlush(tcpMsg);
            channelHandlerContext.close();
            throw new IllegalAccessException("error account to connected!");
        }
        logger.info("接收到心跳数据：{}",JSON.toJSONString(event));
        //心跳，客户端每隔3秒请求一次
        String reqIdStr = (String) reqId;
        String[] reqInfoStrArr = reqIdStr.split(":");
        long currentTimestamp = System.currentTimeMillis();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setIp(reqInfoStrArr[0]);
        serviceInstance.setPort(Integer.valueOf(reqInfoStrArr[1]));
        serviceInstance.setLastHeartBeatTime(currentTimestamp);
        HeartBeatDTO heartBeatDTO = new HeartBeatDTO();
        heartBeatDTO.setMsgId(event.getMsgId());
        channelHandlerContext.writeAndFlush(new TcpMsg(NameServerResponseCode.HEART_BEAT_SUCCESS.getCode(), JSON.toJSONBytes(heartBeatDTO)));
        CommonCache.getServiceInstanceManager().putIfExist(serviceInstance);
        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setServiceInstance(serviceInstance);
        replicationMsgEvent.setMsgId(UUID.randomUUID().toString());
        replicationMsgEvent.setChannelHandlerContext(event.getChannelHandlerContext());
        replicationMsgEvent.setType(ReplicationMsgTypeEnum.HEART_BEAT.getCode());
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);
    }
}
