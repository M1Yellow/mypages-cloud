package com.m1yellow.mypages.service.impl;

import com.m1yellow.mypages.entity.UserPlatform;
import com.m1yellow.mypages.mapper.UserPlatformMapper;
import com.m1yellow.mypages.service.UserPlatformService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 社交媒体平台表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@Service
public class UserPlatformServiceImpl extends ServiceImpl<UserPlatformMapper, UserPlatform> implements UserPlatformService {

    @Override
    public boolean deleteById(UserPlatform platform) {
        return updateById(platform);
    }
}
