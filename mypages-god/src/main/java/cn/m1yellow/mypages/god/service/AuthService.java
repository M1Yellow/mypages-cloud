package cn.m1yellow.mypages.god.service;

import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.god.service.impl.AuthFallbackServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 认证服务远程调用
 */
//@Component
@FeignClient(value = "mypages-auth", fallback = AuthFallbackServiceImpl.class)
public interface AuthService {

    @PostMapping(value = "/oauth/token")
    CommonResult<Map<String, Object>> getAccessToken(@RequestParam Map<String, String> parameters);

}
