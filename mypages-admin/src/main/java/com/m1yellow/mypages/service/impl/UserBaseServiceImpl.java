package com.m1yellow.mypages.service.impl;

import com.m1yellow.mypages.service.UserBaseService;
import com.m1yellow.mypages.entity.UserBase;
import com.m1yellow.mypages.mapper.UserBaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@Service
public class UserBaseServiceImpl extends ServiceImpl<UserBaseMapper, UserBase> implements UserBaseService {

}
