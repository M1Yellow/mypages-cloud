package com.m1yellow.mypages.service;

import com.m1yellow.mypages.dto.UserPlatformDto;
import com.m1yellow.mypages.entity.UserPlatform;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

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

    /**
     * 自定义查询一个对象
     *
     * @param params 参数封装 map
     * @return
     */
    UserPlatformDto getUserPlatform(Map params);

    /**
     * 自定义查询列表
     *
     * @param params
     * @return
     */
    List<UserPlatformDto> queryUserPlatformList(Map params);

}
