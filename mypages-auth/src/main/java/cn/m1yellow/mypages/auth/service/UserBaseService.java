package cn.m1yellow.mypages.auth.service;


import cn.m1yellow.mypages.auth.entity.UserBase;
import cn.m1yellow.mypages.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
//@Component
@FeignClient("mypages-god")
public interface UserBaseService {

    @PostMapping(value = "/user/getByUserName")
    CommonResult<UserBase> getByUserName(@RequestParam String userName);
}
