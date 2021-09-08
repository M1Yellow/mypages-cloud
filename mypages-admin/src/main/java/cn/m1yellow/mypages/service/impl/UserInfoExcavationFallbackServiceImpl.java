package cn.m1yellow.mypages.service.impl;

import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.dto.UserFollowingDto;
import cn.m1yellow.mypages.service.UserInfoExcavationService;
import org.springframework.stereotype.Component;

/**
 * 熔断降级处理
 */
@Component
public class UserInfoExcavationFallbackServiceImpl implements UserInfoExcavationService {

    @Override
    public CommonResult<String> syncFollowingInfo(UserFollowingDto following) {
        return CommonResult.failed();
    }

}
