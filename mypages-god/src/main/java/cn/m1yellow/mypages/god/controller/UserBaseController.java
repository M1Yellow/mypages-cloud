package cn.m1yellow.mypages.god.controller;


import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.api.ResultCode;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.common.constant.AuthConstant;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.god.entity.UserBase;
import cn.m1yellow.mypages.god.service.AuthService;
import cn.m1yellow.mypages.god.service.UserBaseService;
import cn.m1yellow.mypages.god.vo.home.UserInfoDetail;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
@Slf4j
@RestController
@RequestMapping("/user")
public class UserBaseController {

    @Autowired
    private UserBaseService userBaseService;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private AuthService authService;


    @ApiOperation("添加/更新用户")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @CacheEvict(value = GlobalConstant.CACHE_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_INFO_DETAIL_CACHE_KEY + #userBase.id")
    public CommonResult<UserBase> add(UserBase userBase) {

        if (userBase == null) {
            log.error("请求参数错误");
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
            log.error("添加/更新用户失败");
            return CommonResult.failed("添加/更新用户失败");
        }

        return CommonResult.success(userBase);
    }


    @ApiOperation("用户登录")
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<Map<String, Object>> login(@RequestParam String userName, @RequestParam String password) {

        /**
         * TODO 关于参数校验的想法
         * 适当放过一些参数校验，每个参数都校验，然后都要加上错误信息，代码会显得很臃肿，让人感觉很浅显，全是参数校验
         * 其实，直接不校验，传入不合适的参数，程序自然报错，有时候报错也是一种日志，同样便于排查问题，简洁原始之美
         */

        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        Map<String, String> params = new HashMap<>();
        params.put("client_id", AuthConstant.ADMIN_CLIENT_ID);
        params.put("client_secret", AuthConstant.ADMIN_CLIENT_PWD);
        params.put("grant_type", AuthConstant.GRANT_TYPE_PASSWORD);
        params.put("username", userName);
        params.put("password", password);

        CommonResult<Map<String, Object>> restResult = null;
        try {
            restResult = authService.getAccessToken(params);
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage());
        }
        if (restResult == null || restResult.getCode() != ResultCode.SUCCESS.getCode()) {
            return CommonResult.validateFailed("用户名或密码错误");
        }

        return restResult;
    }


    @ApiOperation("获取用户信息详情")
    @RequestMapping(value = "detail", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @Cacheable(value = GlobalConstant.CACHE_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_INFO_DETAIL_CACHE_KEY + #userId", unless = "#result==null")
    public CommonResult<UserInfoDetail> getUserInfoDetail(@RequestParam(required = false) Long userId, @RequestParam(required = false) String userName) {
        if (userId == null && StringUtils.isBlank(userName)) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
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

        return CommonResult.success(userInfoDetail);
    }


    @ApiOperation("通过用户名获取用户信息")
    @RequestMapping(value = "getByUserName", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @Cacheable(value = GlobalConstant.CACHE_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_INFO_DETAIL_CACHE_KEY + #userName", unless = "#result==null")
    public CommonResult<UserBase> getByUserName(@RequestParam String userName) {
        if (StringUtils.isBlank(userName)) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        QueryWrapper<UserBase> userBaseQueryWrapper = new QueryWrapper<>();
        userBaseQueryWrapper.eq("user_name", userName);
        UserBase userBase = userBaseService.getOne(userBaseQueryWrapper);

        if (userBase == null) {
            log.error("通过用户名获取用户信息失败，userName: {}", userName);
            return CommonResult.failed("加载用户信息失败");
        }

        return CommonResult.success(userBase);
    }


    @ApiOperation("移除用户")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @CacheEvict(value = GlobalConstant.CACHE_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_INFO_DETAIL_CACHE_KEY + #id")
    public CommonResult<String> remove(@RequestParam Long id) {

        if (id == null) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userBaseService.removeById(id)) {
            log.error("移除失败，id:" + id);
            return CommonResult.failed("移除失败");
        }

        return CommonResult.success();
    }

}