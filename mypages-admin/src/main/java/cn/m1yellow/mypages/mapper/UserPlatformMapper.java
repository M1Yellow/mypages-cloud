package cn.m1yellow.mypages.mapper;

import cn.m1yellow.mypages.dto.UserPlatformDto;
import cn.m1yellow.mypages.entity.UserPlatform;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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

    UserPlatformDto getUserPlatform(Map params);

    List<UserPlatformDto> queryUserPlatformList(Map params);

}
