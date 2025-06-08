package org.idea.eaglemq.nameserver.event.spi.listener;

import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.dto.SlaveAckDTO;
import org.idea.eaglemq.common.enums.NameServerResponseCode;
import org.idea.eaglemq.common.event.Listener;
import org.idea.eaglemq.nameserver.common.CommonCache;
import org.idea.eaglemq.nameserver.event.model.SlaveReplicationMsgAckEvent;

/**
 * @Author idea
 * @Date: Created in 13:48 2024/5/26
 * @Description 主节点接收从节点同步ack信号处理器
 */
public class SlaveReplicationMsgAckListener implements Listener<SlaveReplicationMsgAckEvent> {

    @Override
    public void onReceive(SlaveReplicationMsgAckEvent event) throws Exception {
        String slaveAckMsgId = event.getMsgId();
        SlaveAckDTO slaveAckDTO = CommonCache.getAckMap().get(slaveAckMsgId);
        if (slaveAckDTO == null) {
            return;
        }
        Integer currentAckTime = slaveAckDTO.getNeedAckTime().decrementAndGet();
        //如果是复制模式，代表所有从节点已经ack完毕了，
        //如果是半同步复制模式
        if (currentAckTime == 0) {
            CommonCache.getAckMap().remove(slaveAckMsgId);
            slaveAckDTO.getBrokerChannel().writeAndFlush(new TcpMsg(NameServerResponseCode.REGISTRY_SUCCESS.getCode(), NameServerResponseCode.REGISTRY_SUCCESS.getDesc().getBytes()));
        }
    }
}
