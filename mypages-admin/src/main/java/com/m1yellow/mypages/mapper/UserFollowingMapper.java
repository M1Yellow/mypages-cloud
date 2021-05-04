package com.m1yellow.mypages.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m1yellow.mypages.dto.UserFollowingDto;
import com.m1yellow.mypages.entity.UserFollowing;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户关注表 Mapper 接口
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
public interface UserFollowingMapper extends BaseMapper<UserFollowing> {

    UserFollowingDto getUserFollowing(Map params);

    List<UserFollowingDto> queryUserFollowingList(Map params);

    List<Long> queryTypeIdList(Map params);

}
