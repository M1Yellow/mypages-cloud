package cn.m1yellow.mypages.security.service.impl;

import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.FastJsonUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.security.entity.SysRole;
import cn.m1yellow.mypages.security.entity.SysUserRole;
import cn.m1yellow.mypages.security.mapper.SysUserRoleMapper;
import cn.m1yellow.mypages.security.service.SysRoleService;
import cn.m1yellow.mypages.security.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-06-02
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Autowired
    private SysRoleService roleService;
    @Resource
    private RedisUtil redisUtil;

    // TODO 是否可以使用注解，传入缓存KEY，自动获取缓存和设置缓存？？这样就可以省掉前后一部分代码了
    @Override
    public List<SysUserRole> queryUserRoleListByUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        // 先从缓存中获取
        String userRoleListCacheStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.USER_ROLE_RELATION_LIST_CACHE_KEY + userId));
        if (StringUtils.isNotBlank(userRoleListCacheStr)) {
            List<SysUserRole> userRoleList = FastJsonUtil.json2List(userRoleListCacheStr, SysUserRole.class);
            if (userRoleList != null && userRoleList.size() > 0) {
                return userRoleList;
            }
        }

        QueryWrapper<SysUserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("user_id", userId);
        List<SysUserRole> userRoleList = list(userRoleQueryWrapper);

        // 设置缓存
        if (userRoleList != null && userRoleList.size() > 0) {
            redisUtil.set(GlobalConstant.USER_ROLE_RELATION_LIST_CACHE_KEY + userId, FastJsonUtil.bean2Json(userRoleList));
        }

        return userRoleList;
    }

    @Override
    public List<SysRole> queryRoleListByUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        // 先从缓存中获取
        String userRoleListCacheStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.USER_ROLE_LIST_CACHE_KEY + userId));
        if (StringUtils.isNotBlank(userRoleListCacheStr)) {
            List<SysRole> userRoleList = FastJsonUtil.json2List(userRoleListCacheStr, SysRole.class);
            if (userRoleList != null && userRoleList.size() > 0) {
                return userRoleList;
            }
        }

        List<SysUserRole> userRoleList = queryUserRoleListByUserId(userId);
        if (userRoleList == null || userRoleList.size() < 1) {
            return null;
        }

        List<Long> roleIdList = new ArrayList<>();
        for (SysUserRole userRole : userRoleList) {
            if (userRole == null || userRole.getRoleId() == null) {
                continue;
            }
            roleIdList.add(userRole.getRoleId());
        }
        roleIdList = roleIdList.stream().distinct().collect(Collectors.toList());
        if (roleIdList == null || roleIdList.size() < 1) {
            return null;
        }
        List<SysRole> roleList = roleService.listByIds(roleIdList);

        // 设置缓存
        if (roleList != null && roleList.size() > 0) {
            redisUtil.set(GlobalConstant.USER_ROLE_LIST_CACHE_KEY + userId, FastJsonUtil.bean2Json(roleList));
        }

        return roleList;
    }

}
