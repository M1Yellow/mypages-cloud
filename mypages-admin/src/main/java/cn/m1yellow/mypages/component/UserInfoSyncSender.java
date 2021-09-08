package cn.m1yellow.mypages.component;

import cn.m1yellow.mypages.common.constant.QueueEnum;
import cn.m1yellow.mypages.dto.UserFollowingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户信息同步消息发送者
 */
@Slf4j
@Component
public class UserInfoSyncSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(UserFollowingDto userFollowingDto) {
        // 往用户信息同步队列发送消息
        log.info(">>>> UserInfoSyncSender userFollowingDto={}", userFollowingDto);
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_USER_INFO_SYNC.getExchange(), QueueEnum.QUEUE_USER_INFO_SYNC.getRouteKey(), userFollowingDto);
    }

}
