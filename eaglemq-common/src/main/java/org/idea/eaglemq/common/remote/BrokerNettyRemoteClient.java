package org.idea.eaglemq.common.remote;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.idea.eaglemq.common.cache.BrokerServerSyncFutureManager;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.coder.TcpMsgDecoder;
import org.idea.eaglemq.common.coder.TcpMsgEncoder;
import org.idea.eaglemq.common.constants.TcpConstants;
import org.idea.eaglemq.common.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 19:36 2024/6/10
 * @Description 对Broker进行远程连接的客户端
 */
public class BrokerNettyRemoteClient {

    private Logger logger = LoggerFactory.getLogger(BrokerNettyRemoteClient.class);

    private String ip;
    private Integer port;

    public BrokerNettyRemoteClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;

    public boolean isChannelActive() {
        return channel.isActive();
    }

    public void buildConnection(SimpleChannelInboundHandler simpleChannelInboundHandler) {
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ByteBuf delimiter = Unpooled.copiedBuffer(TcpConstants.DEFAULT_DECODE_CHAR.getBytes());
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024 * 8, delimiter));
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(simpleChannelInboundHandler);
            }
        });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(ip, port).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        throw new RuntimeException("connecting nameserver has error!");
                    }
                }
            });
            //初始化建立长链接
            channel = channelFuture.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        this.channel.close().addListener(channelFuture -> {
            if (channelFuture.isSuccess()) {
                logger.info("{} close channel success",this.getBrokerReqId() );
            } else {
                logger.info("{} close channel failure",this.getBrokerReqId() );
            }
        });
    }

    public TcpMsg sendSyncMsg(TcpMsg tcpMsg, String msgId) {
        channel.writeAndFlush(tcpMsg);
        SyncFuture syncFuture = new SyncFuture();
        syncFuture.setMsgId(msgId);
        BrokerServerSyncFutureManager.put(msgId, syncFuture);
        try {
            //可能在等待结果的时候，节点挂掉了
            return (TcpMsg) syncFuture.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getBrokerReqId() {
        return ip + ":" + port;
    }

    public void sendAsyncMsg(TcpMsg tcpMsg) {
        channel.writeAndFlush(tcpMsg);
    }
}
