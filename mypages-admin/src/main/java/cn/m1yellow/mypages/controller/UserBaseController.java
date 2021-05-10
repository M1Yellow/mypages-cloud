package cn.m1yellow.mypages.controller;


import cn.m1yellow.mypages.god.entity.UserBase;
import cn.m1yellow.mypages.god.service.UserBaseService;
import cn.m1yellow.mypages.security.config.JwtSecurityProperties;
import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.security.util.JwtTokenUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@RestController
@RequestMapping("/user")
public class UserBaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserBaseController.class);

    @Autowired
    private UserBaseService userBaseService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtSecurityProperties jwtSecurityProperties;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @ApiOperation("添加/更新用户")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<UserBase> add(UserBase userBase) {

        if (userBase == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userBaseService.saveOrUpdate(userBase)) {
            logger.error("添加/更新用户失败");
            return CommonResult.failed("添加/更新用户失败");
        }

        return CommonResult.success(userBase);
    }


    @ApiOperation("用户登录")
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<Map<String, String>> login(@RequestParam String userName, @RequestParam String password) {

        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            // 这里的 password 是客户端加密后的
            //if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            if (!password.equals(userDetails.getPassword())) {
                throw new BadCredentialsException("用户名或密码错误");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            logger.warn("登录异常: {}", e.getMessage());
        }
        if (StringUtils.isBlank(token)) {
            return CommonResult.validateFailed("用户名或密码错误");
        }

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenStart", jwtSecurityProperties.getTokenStart());
        tokenMap.put("tokenHeader", jwtSecurityProperties.getTokenHeader());
        return CommonResult.success(tokenMap);
    }


    @ApiOperation("移除用户")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<String> remove(@RequestParam Long id) {

        if (id == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userBaseService.removeById(id)) {
            logger.error("移除失败，id:" + id);
            return CommonResult.failed("移除失败");
        }

        return CommonResult.success();
    }

}
