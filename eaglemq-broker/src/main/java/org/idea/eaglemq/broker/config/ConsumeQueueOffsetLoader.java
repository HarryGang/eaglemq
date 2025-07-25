package org.idea.eaglemq.broker.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.util.internal.StringUtil;
import org.idea.eaglemq.broker.cache.CommonCache;
import org.idea.eaglemq.broker.model.ConsumeQueueOffsetModel;
import org.idea.eaglemq.broker.utils.FileContentUtil;
import org.idea.eaglemq.common.constants.BrokerConstants;

import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 08:13 2024/4/17
 * @Description
 */
public class ConsumeQueueOffsetLoader {

    private String filePath;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getEagleMqHome();
        if(StringUtil.isNullOrEmpty(basePath)){
            throw new IllegalArgumentException("EAGLE_MQ_HOME is invalid!");
        }
        filePath = basePath + "/config/consumequeue-offset.json";
        String fileContent = FileContentUtil.readFromFile(filePath);
        ConsumeQueueOffsetModel consumeQueueOffsetModels = JSON.parseObject(fileContent, ConsumeQueueOffsetModel.class);
        CommonCache.setConsumeQueueOffsetModel(consumeQueueOffsetModels);
    }


    /**
     * 开启一个刷新内存到磁盘的任务
     */
    public void startRefreshConsumeQueueOffsetTask() {
        //异步线程
        //每3秒将内存中的配置刷新到磁盘里面
        CommonThreadPoolConfig.refreshConsumeQueueOffsetExecutor.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        TimeUnit.SECONDS.sleep(BrokerConstants.DEFAULT_REFRESH_CONSUME_QUEUE_OFFSET_TIME_STEP);
                        ConsumeQueueOffsetModel consumeQueueOffsetModel = CommonCache.getConsumeQueueOffsetModel();
                        FileContentUtil.overWriteToFile(filePath,JSON.toJSONString(consumeQueueOffsetModel, SerializerFeature.PrettyFormat));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }while (true);
            }
        });

    }

}
