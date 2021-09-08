package cn.m1yellow.mypages.excavation.controller;

import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.excavation.bo.UserInfoItem;
import cn.m1yellow.mypages.excavation.dto.UserFollowingDto;
import cn.m1yellow.mypages.excavation.entity.UserFollowing;
import cn.m1yellow.mypages.excavation.service.UserFollowingService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/excavation")
public class UserInfoExcavationController {

    @Autowired
    private UserFollowingService userFollowingService;


    @ApiOperation("同步关注用户的信息")
    @RequestMapping(value = "syncOne", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<String> syncFollowingInfo(UserFollowingDto following) {
        if (following == null) {
            log.error("关注用户不存在");
            return CommonResult.failed("关注用户不存在");
        }

        // 获取用户信息
        UserInfoItem userInfoItem = userFollowingService.doExcavate(following);
        if (userInfoItem == null) {
            log.error("用户信息获取失败，following id:" + following.getFollowingId());
            return CommonResult.failed("用户信息获取失败");
        }

        // 更新信息，保存入库。（注意，内容未改动，即影响行数为 0，返回的也是 false，实际上并不算失败）
        UserFollowing saveFollowing = new UserFollowing();
        BeanUtils.copyProperties(following, saveFollowing);
        // 修正id
        saveFollowing.setId(following.getFollowingId());
        if (!userFollowingService.saveUserInfo(userInfoItem, saveFollowing)) {
            log.error("用户信息保存失败，following id:" + following.getFollowingId());
            return CommonResult.failed("用户信息保存失败");
        }

        return CommonResult.success();
    }

}
