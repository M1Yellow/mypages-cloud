package cn.m1yellow.mypages.god.service.impl;

import cn.m1yellow.mypages.god.entity.SysRole;
import cn.m1yellow.mypages.god.mapper.SysRoleMapper;
import cn.m1yellow.mypages.god.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-05-08
 */
@DubboService(interfaceClass = SysRoleService.class, version = "1.0.0")
//@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

}
