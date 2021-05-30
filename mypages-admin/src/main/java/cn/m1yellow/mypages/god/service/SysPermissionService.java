package cn.m1yellow.mypages.god.service;

import cn.m1yellow.mypages.god.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-05-08
 */
public interface SysPermissionService extends IService<SysPermission> {

    List<SysPermission> queryAllPermission();

}
