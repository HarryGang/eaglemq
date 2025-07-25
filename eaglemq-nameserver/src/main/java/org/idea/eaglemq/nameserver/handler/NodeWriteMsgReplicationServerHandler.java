package org.idea.eaglemq.nameserver.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.enums.NameServerEventCode;
import org.idea.eaglemq.common.event.model.Event;
import org.idea.eaglemq.nameserver.common.CommonCache;
import org.idea.eaglemq.common.event.EventBus;
import org.idea.eaglemq.nameserver.event.model.*;

/**
 * @Author idea
 * @Date: Created in 19:01 2024/5/26
 * @Description 当前节点接收外界写入数据处理器
 */
@ChannelHandler.Sharable
public class NodeWriteMsgReplicationServerHandler extends SimpleChannelInboundHandler {

    private EventBus eventBus;

    public NodeWriteMsgReplicationServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        Event event = null;
        if (NameServerEventCode.NODE_REPLICATION_MSG.getCode() == code) {
            event = JSON.parseObject(body, NodeReplicationMsgEvent.class);
        }
        event.setChannelHandlerContext(channelHandlerContext);
        CommonCache.setPreNodeChannel(channelHandlerContext.channel());
        eventBus.publish(event);
    }
}
