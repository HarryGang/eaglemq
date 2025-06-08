package org.idea.eaglemq.common.remote;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.idea.eaglemq.common.cache.NameServerSyncFutureManager;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.dto.HeartBeatDTO;
import org.idea.eaglemq.common.dto.PullBrokerIpRespDTO;
import org.idea.eaglemq.common.dto.ServiceRegistryRespDTO;
import org.idea.eaglemq.common.enums.NameServerResponseCode;

/**
 * @Author idea
 * @Date: Created in 20:03 2024/6/10
 * @Description 处理对nameserver给客户端返回的数据内容
 */
@ChannelHandler.Sharable
public class NameServerRemoteRespHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        if (NameServerResponseCode.REGISTRY_SUCCESS.getCode() == code) {
            //msgId
            ServiceRegistryRespDTO serviceRegistryRespDTO = JSON.parseObject(tcpMsg.getBody(), ServiceRegistryRespDTO.class);
            SyncFuture syncFuture = NameServerSyncFutureManager.get(serviceRegistryRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode() == code) {
            ServiceRegistryRespDTO serviceRegistryRespDTO = JSON.parseObject(tcpMsg.getBody(), ServiceRegistryRespDTO.class);
            SyncFuture syncFuture = NameServerSyncFutureManager.get(serviceRegistryRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (NameServerResponseCode.HEART_BEAT_SUCCESS.getCode() == code) {
            HeartBeatDTO heartBeatDTO = JSON.parseObject(tcpMsg.getBody(),HeartBeatDTO.class);
            SyncFuture syncFuture = NameServerSyncFutureManager.get(heartBeatDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (NameServerResponseCode.PULL_BROKER_ADDRESS_SUCCESS.getCode() == code) {
            PullBrokerIpRespDTO pullBrokerIpRespDTO = JSON.parseObject(tcpMsg.getBody(), PullBrokerIpRespDTO.class);
            SyncFuture syncFuture = NameServerSyncFutureManager.get(pullBrokerIpRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        }
    }
}
