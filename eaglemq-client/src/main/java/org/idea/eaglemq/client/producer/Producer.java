package org.idea.eaglemq.client.producer;

import org.idea.eaglemq.common.dto.MessageDTO;
import org.idea.eaglemq.common.transaction.TransactionListener;

/**
 * @Author idea
 * @Date: Created in 20:00 2024/6/15
 * @Description
 */
public interface Producer {

    /**
     * 同步发送
     *
     * @param messageDTO
     * @return
     */
    SendResult send(MessageDTO messageDTO);

    /**
     * 异步发送
     *
     * @param messageDTO
     * @return
     */
    void sendAsync(MessageDTO messageDTO);


    /**
     * 发送事务消息
     *
     * @param messageDTO
     * @return
     */
    SendResult sendTxMessage(MessageDTO messageDTO);
}
