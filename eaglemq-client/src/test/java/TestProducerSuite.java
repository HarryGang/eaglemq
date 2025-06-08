import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.idea.eaglemq.client.producer.DefaultProducerImpl;
import org.idea.eaglemq.client.producer.SendResult;
import org.idea.eaglemq.common.dto.MessageDTO;
import org.idea.eaglemq.common.enums.MessageSendWay;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 20:14 2024/6/15
 * @Description
 */
public class TestProducerSuite {

    private DefaultProducerImpl producer;

    @Before
    public void setUp() {
        producer = new DefaultProducerImpl();
        producer.setNsIp("127.0.0.1");
        producer.setNsPort(9093);
        producer.setNsPwd("eagle_mq");
        producer.setNsUser("eagle_mq");
        producer.setBrokerClusterGroup("eagle_mq_test_group");
        producer.start();
    }

    @Test
    public void sendUserEnterMsg() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            try {
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setTopic("user_enter");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",i);
                jsonObject.put("level",1);
                jsonObject.put("enterTime",System.currentTimeMillis());
                messageDTO.setBody(jsonObject.toJSONString().getBytes());
                SendResult sendResult = producer.send(messageDTO);
                System.out.println(JSON.toJSONString(sendResult));
                TimeUnit.SECONDS.sleep(5);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设计延迟消息的时候需要考虑什么内容？
     * 1.时间范围的校验（几秒 - 几分钟，30分钟，1小时） （过大的延迟时间，会导致我们的数据一直没有执行，一直都存放在时间轮里消耗内容）
     * 2.如果延迟时间非常久（订单有效期（保险产品1年，10年），开定时任务）
     * 3.如果公司里需要有一个任务中心，支付后延迟一段时间，发送通知
     * 4.订单超时 使用延迟消息？（并不是完全赞同，上千万的订单数据需要延迟关闭）
     *
     * @throws InterruptedException
     */
    @Test
    public void sendDelayUserEnterMsg() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            try {
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setTopic("user_enter");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",i);
                jsonObject.put("level",1);
                jsonObject.put("enterTime",System.currentTimeMillis());
                messageDTO.setBody(jsonObject.toJSONString().getBytes());
                messageDTO.setDelay(5);
                SendResult sendResult = producer.send(messageDTO);
                System.out.println(JSON.toJSONString(sendResult));
                TimeUnit.SECONDS.sleep(5);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void sendPaySuccessAsyncMsg() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            TimeUnit.SECONDS.sleep(1);
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setTopic("pay_success");
            messageDTO.setBody(("mq content-" + i).getBytes());
            producer.sendAsync(messageDTO);
            System.out.println("async send ok");
        }
    }


}
