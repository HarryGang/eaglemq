package org.idea.eaglemq.broker.event.model;

import org.idea.eaglemq.common.dto.CreateTopicReqDTO;
import org.idea.eaglemq.common.event.model.Event;

/**
 * @author idea
 * @create 2024/7/3 08:07
 * @description 创建topic事件
 */
public class CreateTopicEvent extends Event {

    private CreateTopicReqDTO createTopicReqDTO;

    public CreateTopicReqDTO getCreateTopicReqDTO() {
        return createTopicReqDTO;
    }

    public void setCreateTopicReqDTO(CreateTopicReqDTO createTopicReqDTO) {
        this.createTopicReqDTO = createTopicReqDTO;
    }
}
