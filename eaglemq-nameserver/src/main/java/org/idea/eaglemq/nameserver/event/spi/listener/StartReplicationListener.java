package org.idea.eaglemq.nameserver.event.spi.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.enums.NameServerEventCode;
import org.idea.eaglemq.common.enums.NameServerResponseCode;
import org.idea.eaglemq.common.event.Listener;
import org.idea.eaglemq.nameserver.common.CommonCache;
import org.idea.eaglemq.nameserver.event.model.StartReplicationEvent;
import org.idea.eaglemq.nameserver.utils.NameserverUtils;

import java.net.InetSocketAddress;

/**
 * @Author idea
 * @Date: Created in 16:33 2024/5/18
 * @Description 开启同步复制监听器
 */
public class StartReplicationListener implements Listener<StartReplicationEvent> {

    @Override
    public void onReceive(StartReplicationEvent event) throws Exception {
        boolean isVerify = NameserverUtils.isVerify(event.getUser(), event.getPassword());
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        if (!isVerify) {
            TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode(),
                    NameServerResponseCode.ERROR_USER_OR_PASSWORD.getDesc().getBytes());
            channelHandlerContext.writeAndFlush(tcpMsg);
            channelHandlerContext.close();
            throw new IllegalAccessException("error account to connected!");
        }
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        event.setSlaveIp(inetSocketAddress.getHostString());
        event.setSlavePort(String.valueOf(inetSocketAddress.getPort()));
        String reqId = event.getSlaveIp() + ":" + event.getSlavePort();
        channelHandlerContext.attr(AttributeKey.valueOf("reqId")).set(reqId);
        CommonCache.getReplicationChannelManager().put(reqId, channelHandlerContext);
        TcpMsg tcpMsg = new TcpMsg(NameServerEventCode.MASTER_START_REPLICATION_ACK.getCode(),new byte[0]);
        channelHandlerContext.writeAndFlush(tcpMsg);
    }

}
