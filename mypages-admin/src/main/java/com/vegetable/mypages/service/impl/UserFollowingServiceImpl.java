package com.vegetable.mypages.service.impl;

import com.vegetable.mypages.entity.UserFollowing;
import com.vegetable.mypages.mapper.UserFollowingMapper;
import com.vegetable.mypages.service.UserFollowingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户关注表 服务实现类
 * </p>
 *
 * @author Ming1
 * @since 2021-04-01
 */
@Service
public class UserFollowingServiceImpl extends ServiceImpl<UserFollowingMapper, UserFollowing> implements UserFollowingService {

}
