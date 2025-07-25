package org.idea.eaglemq.common.cache;

import org.idea.eaglemq.common.remote.SyncFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author idea
 * @Date: Created in 09:59 2024/6/16
 * @Description
 */
public class BrokerServerSyncFutureManager {

    private static Map<String, SyncFuture> syncFutureMap = new ConcurrentHashMap<>();

    public static void put(String key, SyncFuture syncFuture) {
        syncFutureMap.put(key, syncFuture);
    }

    public static SyncFuture get(String key) {
        return syncFutureMap.get(key);
    }

    public static void remove(String key) {
        syncFutureMap.remove(key);
    }
}
