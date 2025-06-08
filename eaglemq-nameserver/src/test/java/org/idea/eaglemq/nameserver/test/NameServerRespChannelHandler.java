package org.idea.eaglemq.nameserver.test;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.idea.eaglemq.common.coder.TcpMsg;

/**
 * @author idea
 * @create 2024/1/21 15:52
 * @description nameserver通道
 */
@ChannelHandler.Sharable
public class NameServerRespChannelHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        System.out.println("resp:" + JSON.toJSONString(tcpMsg));
    }
}
