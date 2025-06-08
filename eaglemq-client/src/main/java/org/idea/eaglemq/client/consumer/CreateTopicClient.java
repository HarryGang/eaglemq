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
 * @Date: Created at 2024/8/4
 * @Description
 */
public class CreateTopicClient {


    public void createTopic(String topic, String brokerAddress) {
        String[] brokerAddr = brokerAddress.split(":");
        String ip = brokerAddr[0];
        Integer port = Integer.valueOf(brokerAddr[1]);
        BrokerNettyRemoteClient brokerNettyRemoteClient = new BrokerNettyRemoteClient(ip, port);
        brokerNettyRemoteClient.buildConnection(new BrokerRemoteRespHandler(new EventBus("mq-client-eventbus")));
        CreateTopicReqDTO createTopicReqDTO = new CreateTopicReqDTO();
        createTopicReqDTO.setTopic(topic);
        createTopicReqDTO.setQueueSize(3);
        TcpMsg respMsg = brokerNettyRemoteClient.sendSyncMsg(new TcpMsg(BrokerEventCode.CREATE_TOPIC.getCode(), JSON.toJSONBytes(createTopicReqDTO)), UUID.randomUUID().toString());
        System.out.println("resp:" + JSON.toJSONString(respMsg));
        brokerNettyRemoteClient.close();
    }
}
