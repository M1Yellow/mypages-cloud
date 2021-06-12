package cn.m1yellow.mypages.auth.service.impl;

import cn.m1yellow.mypages.auth.bo.SecurityUser;
import cn.m1yellow.mypages.auth.entity.SysRole;
import cn.m1yellow.mypages.auth.entity.UserBase;
import cn.m1yellow.mypages.auth.service.SysUserRoleService;
import cn.m1yellow.mypages.auth.service.UserBaseService;
import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.api.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义 UserDetailsServiceImpl 实现 spring security 的 UserDetailsService 接口
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserBaseService userBaseService;
    @Autowired
    private SysUserRoleService userRoleService;


    @Override
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        CommonResult<UserBase> restResult = userBaseService.getByUserName(username);
        if (restResult == null || restResult.getCode() != ResultCode.SUCCESS.getCode()) {
            return null;
        }
        UserBase userBase = restResult.getData();
        if (userBase == null) {
            return null;
        }
        List<SysRole> roleList = userRoleService.queryRoleListByUserId(userBase.getId());

        return new SecurityUser(userBase, roleList);
    }
}
