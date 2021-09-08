package cn.m1yellow.mypages.excavation.component;

import cn.m1yellow.mypages.common.api.ResultCode;
import cn.m1yellow.mypages.common.exception.AccessNotAllowException;
import cn.m1yellow.mypages.common.util.HeaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 项目访问控制过滤器，用于控制访问IP
 */
@Slf4j
@Component
public class AccessControlFilter extends OncePerRequestFilter {

    @Value("#{'${project.access.allow.list}'.split(',')}")
    private List<String> accessAllowList;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 获取访问IP
        String requestIp = HeaderUtil.getRequestIp(request);
        if (StringUtils.isBlank(requestIp)) {
            log.error(">>>> AccessControlFilter request ip is invalid.");
            throw new AccessNotAllowException(ResultCode.FORBIDDEN.getMessage());
        }
        // 校验访问IP
        if (!accessAllowList.contains(requestIp)) {
            log.error(">>>> AccessControlFilter request ip: {} is not allowed.", requestIp);
            throw new AccessNotAllowException(ResultCode.FORBIDDEN.getMessage());
        }

        // 放行，继续往下执行
        chain.doFilter(request, response);
    }
}
