package cn.m1yellow.mypages.common.constant;

import lombok.Getter;

/**
 * 消息队列枚举配置
 */
@Getter
public enum QueueEnum {

    /**
     * 用户信息同步队列
     */
    QUEUE_USER_INFO_SYNC("mypages.userinfo.direct", "mypages.userinfo.sync", "mypages.userinfo.sync"),

    /**
     * 用户信息同步ttl队列
     */
    QUEUE_USER_INFO_SYNC_TTL("mypages.userinfo.direct.ttl", "mypages.userinfo.sync.ttl", "mypages.userinfo.sync.ttl"),

    /**
     * 检查用户主页内容更新队列
     */
    QUEUE_CHECK_USER_UPDATE("mypages.check.update.direct", "mypages.check.user.update", "mypages.check.user.update"),

    /**
     * 检查用户主页内容更新ttl队列
     */
    QUEUE_CHECK_USER_UPDATE_TTL("mypages.check.update.direct.ttl", "mypages.check.user.update.ttl", "mypages.check.user.update.ttl");


    /**
     * 交换名称
     */
    private String exchange;

    /**
     * 队列名称
     */
    private String name;

    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }

}
