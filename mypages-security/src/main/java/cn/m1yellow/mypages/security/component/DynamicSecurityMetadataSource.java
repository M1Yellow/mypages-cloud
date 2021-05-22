package cn.m1yellow.mypages.security.component;

import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.god.entity.SysPermission;
import cn.m1yellow.mypages.god.entity.SysRole;
import cn.m1yellow.mypages.god.entity.SysRolePermission;
import cn.m1yellow.mypages.god.service.SysPermissionService;
import cn.m1yellow.mypages.god.service.SysRolePermissionService;
import cn.m1yellow.mypages.god.service.SysRoleService;
import cn.m1yellow.mypages.security.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 动态权限数据源<br>
 * TODO 权限校验规则：
 * 比对角色
 * 从用户角色表 user_role 查询到用户拥有的角色列表1
 * 根据请求 url 在 permission 路径权限表中找到对应的 url
 * 拿 url 到 角色权限表 role_permission 中查询对应的角色列表2
 * 最后比对 请求 url 所需权限对应的角色，有没有在用户拥有的角色列表当中，如果在，则说明有权限，反之则没有权限
 * <p>
 * （当然，也可以直接先查出用户拥有的全部 url 路径权限，再判断请求的 url 路径有没有在用户路径权限列表当中。
 * 但是，这种方式，如果用户有很多角色，每个角色又对应很多路径权限，会导致重复查询。）
 */
@Component
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Resource
    private SysRoleService roleService;
    @Resource
    private SysPermissionService permissionService;
    @Resource
    private SysRolePermissionService rolePermissionService;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;


    /**
     * 获取自定义校验参数
     *
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        // OPTIONS 请求直接放行
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            return null;
        }

        // 白名单请求直接放行
        String uri = request.getRequestURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String path : ignoreUrlsConfig.getUrls()) {
            if (pathMatcher.match(path, uri)) {
                return null;
            }
        }

        // 判断用户认证是否正确
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return SecurityConfig.createList(GlobalConstant.NEED_LOGIN);
        }

        // 获取数据库中所有 url
        List<SysPermission> permissionList = permissionService.queryAllPermission();

        /**
         * TODO 几乎每个请求都要校验权限，每次都要多层循环查询数据库，性能非常糟糕！！！
         * 解决方案：
         * 方案一 采用非关系型数据库，比如 mangodb 做权限校验
         * 方案二 查询数据量大的地方，加入缓存，但还是有个别不通用的查询做不了缓存
         */
        for (SysPermission permission : permissionList) {
            // 根据请求 url 匹配 permission
            if (pathMatcher.match(permission.getUrl(), uri)) {
                // 获取该 url 所对应的角色权限 role_permission，可能有多个
                Map<String, Object> params = new HashMap<>();
                params.put("permission_id", permission.getId());
                List<SysRolePermission> rolePermissionList = rolePermissionService.listByMap(params);
                List<String> roles = new ArrayList<>(); // 直接 new 吧，别拆开判断 if null 了，容易出错
                if (rolePermissionList != null && rolePermissionList.size() > 0) {
                    for (SysRolePermission rolePermission : rolePermissionList) {
                        // 由角色权限查角色
                        Long roleId = rolePermission.getRoleId();
                        if (roleId != null) {
                            SysRole role = roleService.getById(roleId);
                            if (role != null)
                                roles.add(role.getCode()); // code like admin
                        }
                    }
                }
                if (roles != null && roles.size() > 0) {
                    // 保存该 url 对应角色权限信息
                    return SecurityConfig.createList(roles.toArray(new String[roles.size()]));
                }
            }
        }
        // 如果数据库中没有配置 url 权限，或者访问的 url 不在权限列表中
        return SecurityConfig.createList(GlobalConstant.URI_NOT_FOUND);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}
