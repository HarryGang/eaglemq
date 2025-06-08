package org.idea.eaglemq.client.consumer;


import com.alibaba.fastjson.JSON;
import org.idea.eaglemq.client.netty.BrokerRemoteRespHandler;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.dto.CreateTopicReqDTO;
import org.idea.eaglemq.common.enums.BrokerEventCode;
import org.idea.eaglemq.common.event.EventBus;
import org.idea.eaglemq.common.remote.BrokerNettyRemoteClient;

import java.util.UUID;

/**
 * @Author idea
 * @Date: Created at 2024/7/7
 * @Description 创建topic的class
 */
public class CreateTopicCommand {


    public static void main(String[] args) {
        BrokerNettyRemoteClient brokerNettyRemoteClient = new BrokerNettyRemoteClient("127.0.0.1", 8990);
        brokerNettyRemoteClient.buildConnection(new BrokerRemoteRespHandler(new EventBus("mq-client-eventbus")));
        CreateTopicReqDTO createTopicReqDTO = new CreateTopicReqDTO();
        createTopicReqDTO.setTopic("delay_queue");
        createTopicReqDTO.setQueueSize(1);
        brokerNettyRemoteClient.sendAsyncMsg(new TcpMsg(BrokerEventCode.CREATE_TOPIC.getCode(), JSON.toJSONBytes(createTopicReqDTO)));
        System.out.println("已经创建topic：" + createTopicReqDTO.getTopic());
    }
}
