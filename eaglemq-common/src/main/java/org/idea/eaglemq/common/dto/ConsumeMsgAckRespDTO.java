package org.idea.eaglemq.common.dto;


/**
 * @author idea
 * @create 2024/6/26 08:46
 * @description
 */
public class ConsumeMsgAckRespDTO extends BaseBrokerRemoteDTO{

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
