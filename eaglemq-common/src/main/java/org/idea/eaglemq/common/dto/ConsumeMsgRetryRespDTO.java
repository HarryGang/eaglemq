package org.idea.eaglemq.common.dto;

/**
 * @Author idea
 * @Date: Created at 2024/7/21
 * @Description 消息重试确认返回DTO
 */
public class ConsumeMsgRetryRespDTO extends BaseBrokerRemoteDTO{

    /**
     * ack是否成功
     * @see org.idea.eaglemq.common.enums.AckStatus
     */
    private int ackStatus;

    public int getAckStatus() {
        return ackStatus;
    }

    public void setAckStatus(int ackStatus) {
        this.ackStatus = ackStatus;
    }
}
