package cn.m1yellow.mypages.controller;


import cn.m1yellow.mypages.bo.SecurityUser;
import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.FastJsonUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.config.JwtSecurityProperties;
import cn.m1yellow.mypages.god.entity.UserBase;
import cn.m1yellow.mypages.god.service.UserBaseService;
import cn.m1yellow.mypages.util.JwtTokenUtil;
import cn.m1yellow.mypages.vo.home.UserInfoDetail;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @DubboReference(interfaceClass = UserBaseService.class, version = "1.0.0")
    private UserBaseService userBaseService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtSecurityProperties jwtSecurityProperties;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation("添加/更新用户")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<UserBase> add(UserBase userBase) {

        if (userBase == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 是否新增
        boolean isNew = true;
        if (userBase.getId() != null) {
            isNew = false;
        }

        // 去字符串字段两边空格
        ObjectUtil.stringFiledTrim(userBase);

        if (!userBaseService.saveOrUpdate(userBase)) {
            logger.error("添加/更新用户失败");
            return CommonResult.failed("添加/更新用户失败");
        }

        // 更新用户信息，需要清理缓存
        if (!isNew) {
            String cacheKey = GlobalConstant.USER_INFO_DETAIL_CACHE_KEY + userBase.getId();
            redisUtil.del(cacheKey);
            logger.info(">>>> user base info update 删除用户信息缓存，cache key: {}", cacheKey);
            cacheKey = GlobalConstant.USER_INFO_DETAIL_CACHE_KEY + userBase.getUserName();
            redisUtil.del(cacheKey);
            logger.info(">>>> user base info update 删除用户信息缓存，cache key: {}", cacheKey);
        }

        return CommonResult.success(userBase);
    }


    @ApiOperation("用户登录")
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<Map<String, String>> login(@RequestParam String userName, @RequestParam String password) {

        /**
         * TODO 关于参数校验的想法
         * 适当放过一些参数校验，每个参数都校验，然后都要加上错误信息，代码会显得很臃肿，让人感觉很浅显，全是参数校验
         * 其实，直接不校验，传入不合适的参数，程序自然报错，有时候报错也是一种日志，同样便于排查问题，简洁原始之美
         */

        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        String token = null;
        try {
            SecurityUser userDetails = (SecurityUser) userDetailsService.loadUserByUsername(userName);
            // 这里的 password 是客户端加密后的
            //if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            if (userDetails == null || !password.equals(userDetails.getPassword())) {
                throw new BadCredentialsException("用户名或密码错误");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
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


    @ApiOperation("获取用户信息详情")
    @RequestMapping(value = "detail", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<UserInfoDetail> getUserInfoDetail(@RequestParam(required = false) Long userId, @RequestParam(required = false) String userName) {
        if (userId == null && StringUtils.isBlank(userName)) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 先从缓存中获取
        String cacheStr = null;
        if (userId != null) {
            cacheStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.USER_INFO_DETAIL_CACHE_KEY + userId));
        }
        if (StringUtils.isBlank(cacheStr) && StringUtils.isNotBlank(userName)) {
            cacheStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.USER_INFO_DETAIL_CACHE_KEY + userName));
        }
        if (StringUtils.isNotBlank(cacheStr)) {
            UserInfoDetail userInfoDetail = FastJsonUtil.json2Bean(cacheStr, UserInfoDetail.class);
            if (userInfoDetail != null) {
                return CommonResult.success(userInfoDetail);
            }
        }

        UserInfoDetail userInfoDetail = new UserInfoDetail();
        UserBase userBase = null;
        if (userId != null) {
            userBase = userBaseService.getById(userId);
        } else if (StringUtils.isNotBlank(userName)) {
            QueryWrapper<UserBase> userBaseQueryWrapper = new QueryWrapper<>();
            userBaseQueryWrapper.eq("user_name", userName);
            userBase = userBaseService.getOne(userBaseQueryWrapper);
        }
        if (userBase != null) {
            BeanUtils.copyProperties(userBase, userInfoDetail);
            // 补充 userId，userInfoDetail 中的 id 和 userId 都表示用户id，迎合使用习惯
            if (userInfoDetail != null) {
                userInfoDetail.setUserId(userBase.getId());
            }
        }
        // 设置默认头像
        if (StringUtils.isBlank(userInfoDetail.getProfilePhoto())) {
            userInfoDetail.setProfilePhoto(GlobalConstant.USER_DEFAULT_PROFILE_PHOTO_PATH);
        }

        // 设置缓存
        if (userInfoDetail != null) {
            redisUtil.set(GlobalConstant.USER_INFO_DETAIL_CACHE_KEY + userInfoDetail.getUserId(), FastJsonUtil.bean2Json(userInfoDetail));
            redisUtil.set(GlobalConstant.USER_INFO_DETAIL_CACHE_KEY + userInfoDetail.getUserName(), FastJsonUtil.bean2Json(userInfoDetail));
        }

        return CommonResult.success(userInfoDetail);
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
