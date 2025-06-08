package org.idea.eaglemq.broker.event.spi.listener;

import com.alibaba.fastjson.JSON;
import io.netty.util.AttributeKey;
import org.idea.eaglemq.broker.cache.CommonCache;
import org.idea.eaglemq.broker.event.model.StartSyncEvent;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.dto.StartSyncRespDTO;
import org.idea.eaglemq.common.enums.BrokerResponseCode;
import org.idea.eaglemq.common.event.Listener;

import java.net.InetSocketAddress;

/**
 * @Author idea
 * @Date: Created at 2024/7/10
 * @Description 开启同步监听器
 */
public class StartSyncListener implements Listener<StartSyncEvent> {

    @Override
    public void onReceive(StartSyncEvent event) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) event.getChannelHandlerContext().channel().remoteAddress();
        String reqId = inetSocketAddress.getAddress() + ":" + inetSocketAddress.getPort();
        event.getChannelHandlerContext().attr(AttributeKey.valueOf("reqId")).set(reqId);
        //保存从节点的channel到本地缓存中
        CommonCache.getSlaveChannelMap().put(reqId,event.getChannelHandlerContext());
        StartSyncRespDTO startSyncRespDTO = new StartSyncRespDTO();
        startSyncRespDTO.setMsgId(event.getMsgId());
        startSyncRespDTO.setSuccess(true);
        TcpMsg responseMsg = new TcpMsg(BrokerResponseCode.START_SYNC_SUCCESS.getCode(), JSON.toJSONBytes(startSyncRespDTO));
        event.getChannelHandlerContext().writeAndFlush(responseMsg);
    }
}
