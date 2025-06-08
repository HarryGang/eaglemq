package org.idea.eaglemq.broker.event.model;

import org.idea.eaglemq.common.dto.ConsumeMsgAckReqDTO;
import org.idea.eaglemq.common.event.model.Event;

/**
 * @author idea
 * @create 2024/6/26 08:39
 * @description
 */
public class ConsumeMsgAckEvent extends Event {

    private ConsumeMsgAckReqDTO consumeMsgAckReqDTO;

    public ConsumeMsgAckReqDTO getConsumeMsgAckReqDTO() {
        return consumeMsgAckReqDTO;
    }

    public void setConsumeMsgAckReqDTO(ConsumeMsgAckReqDTO consumeMsgAckReqDTO) {
        this.consumeMsgAckReqDTO = consumeMsgAckReqDTO;
    }
}
