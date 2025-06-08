import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.coder.TcpMsgDecoder;
import org.idea.eaglemq.common.coder.TcpMsgEncoder;
import org.idea.eaglemq.common.constants.BrokerConstants;
import org.idea.eaglemq.common.constants.NameServerConstants;
import org.idea.eaglemq.common.enums.NameServerEventCode;
import org.idea.eaglemq.nameserver.test.NameServerRespChannelHandler;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 13:25 2024/5/4
 * @Description
 */
public class TestNameServerSuite {

    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String DEFAULT_NAMESERVER_IP = "127.0.0.1";

    @Before
    public void setUp() {
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(new NameServerRespChannelHandler());
            }
        });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(DEFAULT_NAMESERVER_IP, NameServerConstants.DEFAULT_NAMESERVER_PORT).sync();
            channel = channelFuture.channel();
            System.out.println("success connected to nameserver!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSendMsg() {
        for (int i = 0; i < 100; i++) {
            try {
                System.out.println("isActive:" + channel.isActive());
                TimeUnit.SECONDS.sleep(1);
                String msgBody = "this is client test string";
                TcpMsg tcpMsg = new TcpMsg(NameServerEventCode.HEART_BEAT.getCode(), msgBody.getBytes());
                channel.writeAndFlush(tcpMsg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
