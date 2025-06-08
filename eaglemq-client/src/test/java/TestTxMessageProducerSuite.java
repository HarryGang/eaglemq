import com.alibaba.fastjson.JSONObject;
import io.netty.handler.logging.LogLevel;
import org.idea.eaglemq.client.producer.DefaultProducerImpl;
import org.idea.eaglemq.client.producer.SendResult;
import org.idea.eaglemq.common.dto.MessageDTO;
import org.idea.eaglemq.common.enums.LocalTransactionState;
import org.idea.eaglemq.common.transaction.TransactionListener;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author idea
 * @Date: Created at 2024/8/20
 * @Description 测试事务消息-发送端
 */
public class TestTxMessageProducerSuite {


    private DefaultProducerImpl producer;

    @Before
    public void setUp() {
        producer = new DefaultProducerImpl();
        producer.setNsIp("127.0.0.1");
        producer.setNsPort(9093);
        producer.setNsPwd("eagle_mq");
        producer.setNsUser("eagle_mq");
        producer.setBrokerClusterGroup("eagle_mq_test_group");
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(MessageDTO messageDTO) {
                try {
                    System.out.println("开始执行本地事务");
                    Thread.sleep(1000);
                    int i = 1 / 0;
                    System.out.println("本地事务执行结束");
                    return LocalTransactionState.COMMIT;
                } catch (Exception e) {
                    System.out.println("事务执行异常");
                    return LocalTransactionState.UNKNOW;
                }
            }

            @Override
            public LocalTransactionState callBackHandler(MessageDTO messageDTO) {
                System.out.println("收到producer端回调");
                return LocalTransactionState.ROLLBACK;
            }
        });
        producer.start();
    }


    @Test
    public void sendTxMsg() throws InterruptedException {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTopic("user_enter");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", 1001);
        jsonObject.put("level", 1);
        jsonObject.put("enterTime", System.currentTimeMillis());
        messageDTO.setBody(jsonObject.toJSONString().getBytes());
        SendResult sendResult = producer.sendTxMessage(messageDTO);
        System.out.println("事务消息发送结果:" + sendResult);
        Thread.sleep(100000);
    }

}
