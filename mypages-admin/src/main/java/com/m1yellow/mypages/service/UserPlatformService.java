package com.m1yellow.mypages.service;

import com.m1yellow.mypages.entity.UserPlatform;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 社交媒体平台表 服务类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
public interface UserPlatformService extends IService<UserPlatform> {

    /**
     * 逻辑删除平台
     * @param platform
     * @return
     */
    boolean deleteById(UserPlatform platform);

}
