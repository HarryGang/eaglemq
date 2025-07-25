package org.idea.eaglemq.common.enums;

/**
 * @Author idea
 * @Date: Created in 14:28 2024/6/15
 * @Description broker服务端事件处理code
 */
public enum BrokerEventCode {

    PUSH_MSG(1001, "推送消息"),
    CONSUME_MSG(1002,"消费消息"),
    CONSUME_SUCCESS_MSG(1003,"消费成功"),
    CREATE_TOPIC(1004,"创建topic"),
    START_SYNC_MSG(1005,"从节点开启同步"),
    CONSUME_LATER_MSG(1006,"消息重试"),
    ;

    int code;
    String desc;

    BrokerEventCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
