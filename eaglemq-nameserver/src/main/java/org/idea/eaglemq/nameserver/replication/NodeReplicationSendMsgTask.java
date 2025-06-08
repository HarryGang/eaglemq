package org.idea.eaglemq.nameserver.replication;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.dto.NodeAckDTO;
import org.idea.eaglemq.common.enums.NameServerEventCode;
import org.idea.eaglemq.nameserver.common.CommonCache;
import org.idea.eaglemq.nameserver.event.model.NodeReplicationMsgEvent;
import org.idea.eaglemq.nameserver.event.model.ReplicationMsgEvent;

import java.util.UUID;

/**
 * @Author idea
 * @Date: Created in 10:08 2024/6/1
 * @Description 链式复制中，非尾部节点发送数据给下一个节点的任务
 */
public class NodeReplicationSendMsgTask  extends ReplicationTask{


    public NodeReplicationSendMsgTask(String taskName) {
        super(taskName);
    }

    @Override
    void startTask() {
        while (true) {
            try {
                //如果你是头节点，不是头节点也不是尾部节点
                ReplicationMsgEvent replicationMsgEvent = CommonCache.getReplicationMsgQueueManager().getReplicationMsgQueue().take();
                Channel nextNodeChannel = CommonCache.getConnectNodeChannel();
                NodeReplicationMsgEvent nodeReplicationMsgEvent = new NodeReplicationMsgEvent();
                nodeReplicationMsgEvent.setMsgId(replicationMsgEvent.getMsgId());
                nodeReplicationMsgEvent.setServiceInstance(replicationMsgEvent.getServiceInstance());
                nodeReplicationMsgEvent.setType(replicationMsgEvent.getType());
                NodeAckDTO nodeAckDTO = new NodeAckDTO();
                //broker的连接通道
                nodeAckDTO.setChannelHandlerContext(replicationMsgEvent.getChannelHandlerContext());
                CommonCache.getNodeAckMap().put(replicationMsgEvent.getMsgId(),nodeAckDTO);
                if(nextNodeChannel.isActive()) {
                    nextNodeChannel.writeAndFlush(new TcpMsg(NameServerEventCode.NODE_REPLICATION_MSG.getCode(), JSON.toJSONBytes(nodeReplicationMsgEvent)));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
