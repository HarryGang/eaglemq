package org.idea.eaglemq.nameserver.event.spi.listener;

import com.alibaba.fastjson.JSON;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.enums.NameServerEventCode;
import org.idea.eaglemq.common.event.Listener;
import org.idea.eaglemq.nameserver.common.CommonCache;
import org.idea.eaglemq.nameserver.event.model.ReplicationMsgEvent;
import org.idea.eaglemq.nameserver.event.model.SlaveReplicationMsgAckEvent;
import org.idea.eaglemq.nameserver.store.ServiceInstance;

/**
 * @Author idea
 * @Date: Created in 23:17 2024/5/21
 * @Description 从节点专属的数据同步监听器
 */
public class SlaveReplicationMsgListener implements Listener<ReplicationMsgEvent> {


    @Override
    public void onReceive(ReplicationMsgEvent event) throws Exception {
        ServiceInstance serviceInstance = event.getServiceInstance();
        //从节点接收主节点同步数据逻辑
        CommonCache.getServiceInstanceManager().put(serviceInstance);
        SlaveReplicationMsgAckEvent slaveReplicationMsgAckEvent = new SlaveReplicationMsgAckEvent();
        slaveReplicationMsgAckEvent.setMsgId(event.getMsgId());
        event.getChannelHandlerContext().channel().writeAndFlush(new TcpMsg(NameServerEventCode.SLAVE_REPLICATION_ACK_MSG.ordinal(),
                JSON.toJSONBytes(slaveReplicationMsgAckEvent)));
    }
}
