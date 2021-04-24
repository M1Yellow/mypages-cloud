package com.m1yellow.mypages.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.m1yellow.mypages.bo.UserPlatformBo;
import com.m1yellow.mypages.entity.UserPlatform;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社交媒体平台表 Mapper 接口
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
public interface UserPlatformMapper extends BaseMapper<UserPlatform> {

    UserPlatformBo getUserPlatform(Map params);

    List<UserPlatformBo> queryUserPlatformList(Map params);

}
