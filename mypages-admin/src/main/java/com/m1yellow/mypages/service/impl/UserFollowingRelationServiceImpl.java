package com.m1yellow.mypages.service.impl;

import com.m1yellow.mypages.entity.UserFollowingRelation;
import com.m1yellow.mypages.mapper.UserFollowingRelationMapper;
import com.m1yellow.mypages.service.UserFollowingRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与关注用户关联表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-23
 */
@Service
public class UserFollowingRelationServiceImpl extends ServiceImpl<UserFollowingRelationMapper, UserFollowingRelation> implements UserFollowingRelationService {

}
