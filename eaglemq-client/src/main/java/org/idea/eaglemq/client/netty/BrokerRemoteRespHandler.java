package org.idea.eaglemq.client.netty;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.idea.eaglemq.client.async.event.model.BrokerConnectionClosedEvent;
import org.idea.eaglemq.client.async.event.model.TxMessageCallBackEvent;
import org.idea.eaglemq.common.cache.BrokerServerSyncFutureManager;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.dto.*;
import org.idea.eaglemq.common.enums.BrokerResponseCode;
import org.idea.eaglemq.common.event.EventBus;
import org.idea.eaglemq.common.remote.SyncFuture;

import java.net.InetSocketAddress;

/**
 * @Author idea
 * @Date: Created in 18:10 2024/6/15
 * @Description
 */
@ChannelHandler.Sharable
public class BrokerRemoteRespHandler extends SimpleChannelInboundHandler {

    private EventBus eventBus;

    public BrokerRemoteRespHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        if (BrokerResponseCode.SEND_MSG_RESP.getCode() == code) {
            SendMessageToBrokerResponseDTO sendMessageToBrokerResponseDTO = JSON.parseObject(body, SendMessageToBrokerResponseDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(sendMessageToBrokerResponseDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (BrokerResponseCode.CONSUME_MSG_RESP.getCode() == code) {
            ConsumeMsgBaseRespDTO consumeMsgBaseRespDTO = JSON.parseObject(body, ConsumeMsgBaseRespDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(consumeMsgBaseRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (BrokerResponseCode.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode() == code) {
            ConsumeMsgAckRespDTO consumeMsgAckRespDTO = JSON.parseObject(body, ConsumeMsgAckRespDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(consumeMsgAckRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (BrokerResponseCode.CONSUME_MSG_RETRY_RESP.getCode() == code) {
            ConsumeMsgRetryRespDTO consumeMsgRetryRespDTO = JSON.parseObject(body, ConsumeMsgRetryRespDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(consumeMsgRetryRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (BrokerResponseCode.HALF_MSG_SEND_SUCCESS.getCode() == code) {
            SendMessageToBrokerResponseDTO consumeMsgRetryRespDTO = JSON.parseObject(body, SendMessageToBrokerResponseDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(consumeMsgRetryRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (BrokerResponseCode.REMAIN_ACK_MSG_SEND_SUCCESS.getCode() == code) {
            SendMessageToBrokerResponseDTO consumeMsgRetryRespDTO = JSON.parseObject(body, SendMessageToBrokerResponseDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(consumeMsgRetryRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (BrokerResponseCode.TX_CALLBACK_MSG.getCode() == code) {
            System.out.println("收到事务回调消息");
            TxMessageCallbackReqDTO txMessageCallbackReqDTO = JSON.parseObject(body, TxMessageCallbackReqDTO.class);
            TxMessageCallBackEvent txMessageCallBackEvent = new TxMessageCallBackEvent();
            txMessageCallBackEvent.setMsgId(txMessageCallbackReqDTO.getMessageDTO().getMsgId());
            txMessageCallBackEvent.setChannelHandlerContext(channelHandlerContext);
            txMessageCallBackEvent.setTxMessageCallbackReqDTO(txMessageCallbackReqDTO);
            eventBus.publish(txMessageCallBackEvent);
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String reqId = inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();
        ctx.attr(AttributeKey.valueOf("reqId")).set(reqId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("通道关闭");
        //需要触发一个事件出来
        BrokerConnectionClosedEvent brokerConnectionClosedEvent = new BrokerConnectionClosedEvent();
        brokerConnectionClosedEvent.setBrokerReqId((String) ctx.attr(AttributeKey.valueOf("reqId")).get());
        eventBus.publish(brokerConnectionClosedEvent);
    }
}
