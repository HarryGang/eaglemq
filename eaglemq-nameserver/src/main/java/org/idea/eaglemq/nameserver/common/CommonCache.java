package org.idea.eaglemq.nameserver.common;

import io.netty.channel.Channel;
import org.idea.eaglemq.common.dto.NodeAckDTO;
import org.idea.eaglemq.common.dto.SlaveAckDTO;
import org.idea.eaglemq.nameserver.core.PropertiesLoader;
import org.idea.eaglemq.nameserver.replication.MasterReplicationMsgSendTask;
import org.idea.eaglemq.nameserver.replication.ReplicationTask;
import org.idea.eaglemq.nameserver.store.ReplicationChannelManager;
import org.idea.eaglemq.nameserver.store.ReplicationMsgQueueManager;
import org.idea.eaglemq.nameserver.store.ServiceInstanceManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Author idea
 * @Date: Created in 17:26 2024/5/4
 * @Description
 */
public class CommonCache {

    private static ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager();
    private static PropertiesLoader propertiesLoader = new PropertiesLoader();
    private static NameserverProperties nameserverProperties = new NameserverProperties();
    private static ReplicationChannelManager replicationChannelManager = new ReplicationChannelManager();
    private static ReplicationTask replicationTask;
    private static Channel connectNodeChannel = null;
    private static Channel preNodeChannel = null;
    private static ReplicationMsgQueueManager replicationMsgQueueManager = new ReplicationMsgQueueManager();

    private static Map<String, NodeAckDTO> nodeAckMap = new ConcurrentHashMap<>();
    private static Map<String, SlaveAckDTO> ackMap = new ConcurrentHashMap<>();

    public static Map<String, NodeAckDTO> getNodeAckMap() {
        return nodeAckMap;
    }

    public static void setNodeAckMap(Map<String, NodeAckDTO> nodeAckMap) {
        CommonCache.nodeAckMap = nodeAckMap;
    }

    public static Map<String, SlaveAckDTO> getAckMap() {
        return ackMap;
    }

    public static void setAckMap(Map<String, SlaveAckDTO> ackMap) {
        CommonCache.ackMap = ackMap;
    }

    public static Channel getPreNodeChannel() {
        return preNodeChannel;
    }

    public static void setPreNodeChannel(Channel preNodeChannel) {
        CommonCache.preNodeChannel = preNodeChannel;
    }

    public static ReplicationMsgQueueManager getReplicationMsgQueueManager() {
        return replicationMsgQueueManager;
    }

    public static void setReplicationMsgQueueManager(ReplicationMsgQueueManager replicationMsgQueueManager) {
        CommonCache.replicationMsgQueueManager = replicationMsgQueueManager;
    }

    public static Channel getConnectNodeChannel() {
        return connectNodeChannel;
    }

    public static void setConnectNodeChannel(Channel connectNodeChannel) {
        CommonCache.connectNodeChannel = connectNodeChannel;
    }

    public static ReplicationTask getReplicationTask() {
        return replicationTask;
    }

    public static void setReplicationTask(ReplicationTask replicationTask) {
        CommonCache.replicationTask = replicationTask;
    }

    public static ReplicationChannelManager getReplicationChannelManager() {
        return replicationChannelManager;
    }

    public static void setReplicationChannelManager(ReplicationChannelManager replicationChannelManager) {
        CommonCache.replicationChannelManager = replicationChannelManager;
    }

    public static NameserverProperties getNameserverProperties() {
        return nameserverProperties;
    }

    public static void setNameserverProperties(NameserverProperties nameserverProperties) {
        CommonCache.nameserverProperties = nameserverProperties;
    }

    public static ServiceInstanceManager getServiceInstanceManager() {
        return serviceInstanceManager;
    }

    public static void setServiceInstanceManager(ServiceInstanceManager serviceInstanceManager) {
        CommonCache.serviceInstanceManager = serviceInstanceManager;
    }

    public static PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }

    public static void setPropertiesLoader(PropertiesLoader propertiesLoader) {
        CommonCache.propertiesLoader = propertiesLoader;
    }
}
