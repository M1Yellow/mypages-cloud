package cn.m1yellow.mypages.god.service.impl;

import cn.m1yellow.mypages.god.entity.SysRolePermission;
import cn.m1yellow.mypages.god.mapper.SysRolePermissionMapper;
import cn.m1yellow.mypages.god.service.SysRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色权限关联表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-05-08
 */
@DubboService(interfaceClass = SysRolePermissionService.class, version = "1.0.0")
//@Service("sysRolePermissionService")
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

}
