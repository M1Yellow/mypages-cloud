package cn.m1yellow.mypages.god.service.impl;

import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.god.service.AuthService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AuthService 熔断降级处理
 */
@Component
public class AuthFallbackServiceImpl implements AuthService {
    @Override
    public CommonResult<Map<String, Object>> getAccessToken(Map<String, String> parameters) {
        return CommonResult.failed();
    }
}
