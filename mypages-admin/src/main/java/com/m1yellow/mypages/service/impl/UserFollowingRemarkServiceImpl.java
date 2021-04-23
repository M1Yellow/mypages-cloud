package com.m1yellow.mypages.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.m1yellow.mypages.service.UserFollowingRemarkService;
import com.m1yellow.mypages.entity.UserFollowingRemark;
import com.m1yellow.mypages.mapper.UserFollowingRemarkMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户关注备注（便签）表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@Service
public class UserFollowingRemarkServiceImpl extends ServiceImpl<UserFollowingRemarkMapper, UserFollowingRemark> implements UserFollowingRemarkService {

    @Override
    public List<UserFollowingRemark> queryUserFollowingRemarkListRegularly(Map<String, Object> params) {
        QueryWrapper<UserFollowingRemark> followingRemarkQueryWrapper = new QueryWrapper();
        followingRemarkQueryWrapper.eq("following_id", params.get("following_id"));
        //followingRemarkQueryWrapper.eq("is_deleted", 0);
        followingRemarkQueryWrapper.orderByDesc("sort_no");
        followingRemarkQueryWrapper.orderByAsc("id");
        return this.list(followingRemarkQueryWrapper);
    }
}
