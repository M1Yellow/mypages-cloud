package com.m1yellow.mypages.controller;


import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.aspect.DoCache;
import com.m1yellow.mypages.common.aspect.WebLog;
import com.m1yellow.mypages.dto.UserPlatformDto;
import com.m1yellow.mypages.entity.UserPlatformRelation;
import com.m1yellow.mypages.service.UserPlatformRelationService;
import com.m1yellow.mypages.service.UserPlatformService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户与平台关联表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-23
 */
@RestController
@RequestMapping("/platform-relation")
public class UserPlatformRelationController {

    private static final Logger logger = LoggerFactory.getLogger(UserPlatformRelationController.class);

    @Autowired
    private UserPlatformService userPlatformService;
    @Autowired
    private UserPlatformRelationService userPlatformRelationService;


    @ApiOperation("添加/更新用户平台关系")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<UserPlatformDto> add(UserPlatformDto platform) {

        //CheckParamUtil.validate(platform);
        if (platform == null || platform.getUserId() == null || platform.getPlatformId() == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        UserPlatformRelation savePlatformRelation = new UserPlatformRelation();
        BeanUtils.copyProperties(platform, savePlatformRelation);

        if (!userPlatformRelationService.saveOrUpdate(savePlatformRelation)) {
            logger.error("添加/更新平台失败");
            return CommonResult.failed("操作失败");
        }

        // 重新加载封装对象
        Map<String, Object> params = new HashMap<>();
        params.put("userId", platform.getUserId());
        params.put("platformId", platform.getPlatformId());
        UserPlatformDto reloadUserPlatform = userPlatformService.getUserPlatform(params);

        return CommonResult.success(reloadUserPlatform);
    }


    @ApiOperation("移除用户平台关系")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long platformId) {

        if (userId == null || platformId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("platform_id", platformId);
        if (!userPlatformRelationService.removeByMap(params)) {
            logger.error("移除失败，userId: {}, platformId: {}", userId, platformId);
            return CommonResult.failed("操作失败");
        }

        return CommonResult.success();
    }

}
