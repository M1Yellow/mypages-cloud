package cn.m1yellow.mypages.service.impl;

import cn.m1yellow.mypages.bo.SecurityUser;
import cn.m1yellow.mypages.god.entity.SysRole;
import cn.m1yellow.mypages.god.entity.UserBase;
import cn.m1yellow.mypages.god.service.SysUserRoleService;
import cn.m1yellow.mypages.god.service.UserBaseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义 UserDetailsServiceImpl 实现 spring security 的 UserDetailsService 接口
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @DubboReference(interfaceClass = UserBaseService.class, version = "1.0.0")
    private UserBaseService userBaseService;
    @DubboReference(interfaceClass = SysUserRoleService.class, version = "1.0.0")
    private SysUserRoleService userRoleService;


    @Override
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        UserBase userBase = userBaseService.getByUserName(username);
        if (userBase == null) {
            return null;
        }
        List<SysRole> roleList = userRoleService.queryRoleListByUserId(userBase.getId());

        return new SecurityUser(userBase, roleList);
    }
}
