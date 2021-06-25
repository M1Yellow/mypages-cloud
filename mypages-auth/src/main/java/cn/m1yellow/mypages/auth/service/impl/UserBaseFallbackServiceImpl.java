package cn.m1yellow.mypages.auth.service.impl;

import cn.m1yellow.mypages.auth.entity.UserBase;
import cn.m1yellow.mypages.auth.service.UserBaseService;
import cn.m1yellow.mypages.common.api.CommonResult;
import org.springframework.stereotype.Component;

/**
 * UserBaseService 熔断降级处理
 */
@Component
public class UserBaseFallbackServiceImpl implements UserBaseService {
    @Override
    public CommonResult<UserBase> getByUserName(String userName) {
        return CommonResult.failed();
    }
}
