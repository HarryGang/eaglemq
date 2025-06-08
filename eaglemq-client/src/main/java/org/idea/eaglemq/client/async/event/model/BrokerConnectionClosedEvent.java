package org.idea.eaglemq.client.async.event.model;

import org.idea.eaglemq.common.event.model.Event;

/**
 * @Author idea
 * @Date: Created at 2024/7/16
 * @Description
 */
public class BrokerConnectionClosedEvent extends Event {

    private String brokerReqId;

    public String getBrokerReqId() {
        return brokerReqId;
    }

    public void setBrokerReqId(String brokerReqId) {
        this.brokerReqId = brokerReqId;
    }
}
