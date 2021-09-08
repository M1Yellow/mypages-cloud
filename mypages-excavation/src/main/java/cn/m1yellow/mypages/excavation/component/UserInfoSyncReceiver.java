package cn.m1yellow.mypages.excavation.component;

import cn.m1yellow.mypages.common.dto.MessageTask;
import cn.m1yellow.mypages.common.util.GsonUtil;
import cn.m1yellow.mypages.excavation.bo.UserInfoItem;
import cn.m1yellow.mypages.excavation.dto.UserFollowingDto;
import cn.m1yellow.mypages.excavation.entity.UserFollowing;
import cn.m1yellow.mypages.excavation.service.UserFollowingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户信息同步队列消息接收者
 */
@Slf4j
@Component
@RabbitListener(queues = "mypages.userinfo.sync")
public class UserInfoSyncReceiver {

    @Autowired
    private UserFollowingService userFollowingService;

    @RabbitHandler
    public void handle(MessageTask messageTask) {
        log.info(">>>> UserInfoSyncReceiver messageTask={}", messageTask);

        // 获取封装对象
        UserFollowingDto userFollowingDto = GsonUtil.json2Bean(messageTask.getPayload(), UserFollowingDto.class);
        if (userFollowingDto == null) {
            log.error(">>>> UserInfoSyncReceiver messageTask 消息内容加载失败，taskId:{}", messageTask.getTaskId());
            return;
        }

        // 获取用户信息
        UserInfoItem userInfoItem = userFollowingService.doExcavate(userFollowingDto);
        if (userInfoItem == null) {
            log.error(">>>> UserInfoSyncReceiver 获取用户信息失败，following id:{}", userFollowingDto.getFollowingId());
            return;
        }

        // 更新信息，保存入库。（注意，内容未改动，即影响行数为 0，返回的也是 false，实际上并不算失败）
        UserFollowing saveFollowing = new UserFollowing();
        BeanUtils.copyProperties(userFollowingDto, saveFollowing);
        // 修正id
        saveFollowing.setId(userFollowingDto.getFollowingId());
        // 更新信息，保存入库
        if (!userFollowingService.saveUserInfo(userInfoItem, saveFollowing)) {
            log.error(">>>> UserInfoSyncReceiver 保存用户信息失败，following id:{}", userFollowingDto.getFollowingId());
            return;
        }
    }

}
