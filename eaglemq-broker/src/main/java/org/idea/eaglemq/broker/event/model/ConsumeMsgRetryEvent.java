package org.idea.eaglemq.broker.event.model;

import org.idea.eaglemq.common.dto.ConsumeMsgRetryReqDTO;
import org.idea.eaglemq.common.dto.ConsumeMsgRetryReqDetailDTO;
import org.idea.eaglemq.common.event.model.Event;

/**
 * @Author idea
 * @Date: Created at 2024/7/21
 * @Description 
 */
public class ConsumeMsgRetryEvent extends Event {

    private ConsumeMsgRetryReqDTO consumeMsgRetryReqDTO;

    public ConsumeMsgRetryReqDTO getConsumeMsgRetryReqDTO() {
        return consumeMsgRetryReqDTO;
    }

    public void setConsumeMsgRetryReqDTO(ConsumeMsgRetryReqDTO consumeMsgRetryReqDTO) {
        this.consumeMsgRetryReqDTO = consumeMsgRetryReqDTO;
    }
}
