package com.m1yellow.mypages.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.entity.UserPlatform;
import com.m1yellow.mypages.entity.UserPlatformRelation;
import com.m1yellow.mypages.service.UserPlatformRelationService;
import com.m1yellow.mypages.service.UserPlatformService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 社交媒体平台表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@RestController
@RequestMapping("/platform")
public class UserPlatformController {

    private static final Logger logger = LoggerFactory.getLogger(UserPlatformController.class);

    @Autowired
    private UserPlatformService userPlatformService;
    @Autowired
    private UserPlatformRelationService userPlatformRelationService;


    @ApiOperation("添加/更新平台")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public CommonResult<UserPlatform> add(UserPlatform platform) {

        if (platform == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userPlatformService.saveOrUpdate(platform)) {
            logger.error("添加/更新平台失败");
            return CommonResult.failed("添加/更新平台失败");
        }

        return CommonResult.success(platform);
    }


    @ApiOperation("移除平台")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public CommonResult<String> remove(@RequestParam Long id) {

        if (id == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userPlatformService.removeById(id)) {
            logger.error("移除平台失败，id:" + id);
            return CommonResult.failed("移除失败");
        }

        return CommonResult.success("操作成功");
    }


    @ApiOperation("移除用户平台")
    @RequestMapping(value = "removeRelation", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long platformId) {

        if (userId == null || platformId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        UpdateWrapper<UserPlatformRelation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_deleted", 1);
        updateWrapper.eq("user_id", userId);
        updateWrapper.eq("platform_id", platformId);

        if (!userPlatformRelationService.update(updateWrapper)) {
            logger.error("移除失败，platform id:" + platformId);
            return CommonResult.failed("移除失败");
        }

        return CommonResult.success("操作成功");
    }

}
