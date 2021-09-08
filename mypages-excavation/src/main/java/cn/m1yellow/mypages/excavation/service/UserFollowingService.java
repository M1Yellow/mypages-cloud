package cn.m1yellow.mypages.excavation.service;

import cn.m1yellow.mypages.excavation.bo.UserInfoItem;
import cn.m1yellow.mypages.excavation.dto.UserFollowingDto;
import cn.m1yellow.mypages.excavation.entity.UserFollowing;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

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
     *
     * @param following
     * @return
     */
    UserInfoItem doExcavate(UserFollowingDto following);

    /**
     * 保存同步用户信息
     *
     * @param userInfoItem
     * @param following
     * @return
     */
    boolean saveUserInfo(UserInfoItem userInfoItem, UserFollowing following);

    /**
     * 获取关注用户所在平台的id或标识
     *
     * @param following
     * @return
     */
    String getUserKeyFromMainPage(UserFollowingDto following);

}
