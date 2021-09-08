package cn.m1yellow.mypages.service;

import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.dto.UserFollowingDto;
import cn.m1yellow.mypages.service.impl.UserInfoExcavationFallbackServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户信息远程调用
 */
//@Component
@FeignClient(value = "mypages-excavation", fallback = UserInfoExcavationFallbackServiceImpl.class)
public interface UserInfoExcavationService {

    @PostMapping(value = "/excavation/syncOne")
    CommonResult<String> syncFollowingInfo(@RequestParam UserFollowingDto following);

}
