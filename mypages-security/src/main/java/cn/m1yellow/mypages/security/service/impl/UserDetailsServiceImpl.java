package cn.m1yellow.mypages.security.service.impl;

import cn.m1yellow.mypages.god.entity.SysRole;
import cn.m1yellow.mypages.god.entity.UserBase;
import cn.m1yellow.mypages.god.service.SysUserRoleService;
import cn.m1yellow.mypages.god.service.UserBaseService;
import cn.m1yellow.mypages.security.bo.SecurityUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义 UserDetailsServiceImpl 实现 spring security 的 UserDetailsService 接口
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    //@Autowired // 不能识别注入传递依赖模块 god 中的 bean
    @Resource
    private UserBaseService userBaseService;
    @Resource
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
