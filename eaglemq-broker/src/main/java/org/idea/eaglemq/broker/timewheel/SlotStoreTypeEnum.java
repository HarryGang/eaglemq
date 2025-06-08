package org.idea.eaglemq.broker.timewheel;

import org.idea.eaglemq.common.dto.MessageDTO;
import org.idea.eaglemq.common.dto.MessageRetryDTO;
import org.idea.eaglemq.common.dto.TxMessageDTO;

/**
 * @Author idea
 * @Date: Created at 2024/7/28
 * @Description
 */
public enum SlotStoreTypeEnum {

    MESSAGE_RETRY_DTO(MessageRetryDTO.class),
    DELAY_MESSAGE_DTO(MessageDTO.class),
    TX_MESSAGE_DTO(TxMessageDTO.class),
    ;
    Class clazz;

    SlotStoreTypeEnum(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }
}
