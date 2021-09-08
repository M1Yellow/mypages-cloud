package cn.m1yellow.mypages.config;

import cn.m1yellow.mypages.common.constant.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置
 */
@Configuration
public class RabbitMqConfig {

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 队列定义区域 head >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 用户信息同步队列
     */
    @Bean
    public Queue userInfoSyncQueue() {
        return new Queue(QueueEnum.QUEUE_USER_INFO_SYNC.getName());
    }

    /**
     * 检查用户首页内容更新队列
     */
    @Bean
    public Queue checkUserUpdateQueue() {
        return new Queue(QueueEnum.QUEUE_CHECK_USER_UPDATE.getName());
    }

    /**
     * 用户信息同步延迟队列（死信队列）
     * 暂时没有使用
     */
    /*
    @Bean
    public Queue userInfoSyncTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_USER_INFO_SYNC_TTL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_USER_INFO_SYNC.getExchange()) // 到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_USER_INFO_SYNC.getRouteKey()) // 到期后转发的路由键
                .build();
    }
    */

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 队列定义区域 tail <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 交换机定义区域 head >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 用户信息同步队列绑定的交换机
     */
    @Bean
    DirectExchange userInfoSyncDirect() {
        return ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_USER_INFO_SYNC.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 用户信息同步延迟队列绑定的交换机
     */
    @Bean
    DirectExchange userInfoSyncTtlDirect() {
        return ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_USER_INFO_SYNC_TTL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 检查用户首页内容更新队列绑定的交换机
     */
    @Bean
    DirectExchange checkUserUpdateDirect() {
        return ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_CHECK_USER_UPDATE.getExchange())
                .durable(true)
                .build();
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 交换机定义区域 tail <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 队列绑定交换机区域 head >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 将用户信息同步队列绑定到交换机
     */
    @Bean
    Binding userInfoSyncBinding(DirectExchange userInfoSyncDirect, Queue userInfoSyncQueue) {
        return BindingBuilder
                .bind(userInfoSyncQueue)
                .to(userInfoSyncDirect)
                .with(QueueEnum.QUEUE_USER_INFO_SYNC.getRouteKey());
    }

    /**
     * 将检查用户更新队列绑定到交换机
     */
    @Bean
    Binding checkUserUpdateBinding(DirectExchange checkUserUpdateDirect, Queue checkUserUpdateQueue) {
        return BindingBuilder
                .bind(checkUserUpdateQueue)
                .to(checkUserUpdateDirect)
                .with(QueueEnum.QUEUE_CHECK_USER_UPDATE.getRouteKey());
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 队列绑定交换机区域 tail <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

}
