package org.idea.eaglemq.nameserver.replication;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.enums.NameServerEventCode;
import org.idea.eaglemq.nameserver.common.CommonCache;
import org.idea.eaglemq.nameserver.event.model.SlaveHeartBeatEvent;
import org.idea.eaglemq.nameserver.event.model.StartReplicationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 10:46 2024/5/19
 * @Description 从节点给主节点发送心跳数据 定时任务
 */
public class SlaveReplicationHeartBeatTask extends ReplicationTask {

    private final Logger logger = LoggerFactory.getLogger(SlaveReplicationHeartBeatTask.class);

    public SlaveReplicationHeartBeatTask(String taskName) {
        super(taskName);
    }

    @Override
    public void startTask() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StartReplicationEvent startReplicationEvent = new StartReplicationEvent();
        startReplicationEvent.setUser(CommonCache.getNameserverProperties().getNameserverUser());
        startReplicationEvent.setPassword(CommonCache.getNameserverProperties().getNameserverPwd());
        TcpMsg startReplicationMsg = new TcpMsg(NameServerEventCode.START_REPLICATION.getCode(), JSON.toJSONBytes(startReplicationEvent));
        CommonCache.getConnectNodeChannel().writeAndFlush(startReplicationMsg);
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
                //发送数据给到主节点
                Channel channel = CommonCache.getConnectNodeChannel();
                TcpMsg tcpMsg = new TcpMsg(NameServerEventCode.SLAVE_HEART_BEAT.getCode(), JSON.toJSONBytes(new SlaveHeartBeatEvent()));
                channel.writeAndFlush(tcpMsg);
                logger.info("从节点发送心跳数据给master");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
