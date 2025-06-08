import org.idea.eaglemq.client.consumer.ConsumeMessage;
import org.idea.eaglemq.client.consumer.ConsumeResult;
import org.idea.eaglemq.client.consumer.DefaultMqConsumer;
import org.idea.eaglemq.client.consumer.MessageConsumeListener;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 11:04 2024/6/16
 * @Description
 */
public class TestConsumerSuite {

    private final Logger logger = LoggerFactory.getLogger(TestConsumerSuite.class);

    private DefaultMqConsumer consumer;


    @Test
    public void testConsumeUserEnterTopicMsg() throws InterruptedException {
        consumer = new DefaultMqConsumer();
        consumer.setNsIp("127.0.0.1");
        consumer.setNsPort(9093);
        consumer.setNsPwd("eagle_mq");
        consumer.setNsUser("eagle_mq");
        consumer.setTopic("user_enter");
        consumer.setConsumeGroup("test-consume-user-enter-group");
        consumer.setBrokerClusterGroup("eagle_mq_test_group");
        consumer.setBatchSize(1);
        consumer.setMessageConsumeListener(new MessageConsumeListener() {
            @Override
            public ConsumeResult consume(List<ConsumeMessage> consumeMessages) {
                for (ConsumeMessage consumeMessage : consumeMessages) {
                    logger.info("retryTimes：{},消费端获取的数据内容:{}" ,consumeMessage.getConsumeMsgCommitLogDTO().getRetryTimes(),
                            new String(consumeMessage.getConsumeMsgCommitLogDTO().getBody()));
                }
                return ConsumeResult.CONSUME_SUCCESS();
            }
        });
        consumer.start();
    }

    @Test
    public void testConsumeDelayMsgTopicMsg() throws InterruptedException {
        consumer = new DefaultMqConsumer();
        consumer.setNsIp("127.0.0.1");
        consumer.setNsPort(9093);
        consumer.setNsPwd("eagle_mq");
        consumer.setNsUser("eagle_mq");
        consumer.setTopic("delay_queue");
        consumer.setConsumeGroup("test-consume-user-enter-group");
        consumer.setBrokerClusterGroup("eagle_mq_test_group");
        consumer.setBatchSize(1);
        consumer.setMessageConsumeListener(new MessageConsumeListener() {
            @Override
            public ConsumeResult consume(List<ConsumeMessage> consumeMessages) {
                for (ConsumeMessage consumeMessage : consumeMessages) {
                    logger.info("retryTimes：{},延迟消息内容订阅:{}" ,consumeMessage.getConsumeMsgCommitLogDTO().getRetryTimes(),
                            new String(consumeMessage.getConsumeMsgCommitLogDTO().getBody()));
                }
                return ConsumeResult.CONSUME_SUCCESS();
            }
        });
        consumer.start();
    }

}
