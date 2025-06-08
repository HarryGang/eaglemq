package org.idea.eaglemq.broker.event.model;

import org.idea.eaglemq.common.dto.MessageDTO;
import org.idea.eaglemq.common.event.model.Event;

/**
 * @Author idea
 * @Date: Created in 09:45 2024/6/16
 * @Description
 */
public class PushMsgEvent extends Event {

    private MessageDTO messageDTO;

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }

    public void setMessageDTO(MessageDTO messageDTO) {
        this.messageDTO = messageDTO;
    }
}
