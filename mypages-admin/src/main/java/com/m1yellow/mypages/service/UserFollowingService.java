package com.m1yellow.mypages.service;

import com.m1yellow.mypages.entity.UserFollowing;
import com.baomidou.mybatisplus.extension.service.IService;
import com.m1yellow.mypages.excavation.bo.UserInfoItem;

/**
 * <p>
 * 用户关注表 服务类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
public interface UserFollowingService extends IService<UserFollowing> {

    /**
     * 获取用户信息
     * @param following
     * @return
     */
    UserInfoItem doExcavate(UserFollowing following);

    /**
     * 保存同步用户信息
     * @param userInfoItem
     * @param following
     * @return
     */
    boolean saveUserInfo(UserInfoItem userInfoItem, UserFollowing following);

}
