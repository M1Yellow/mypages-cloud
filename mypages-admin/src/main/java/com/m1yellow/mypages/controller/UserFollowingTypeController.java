package com.m1yellow.mypages.controller;


import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.aspect.DoCache;
import com.m1yellow.mypages.common.aspect.WebLog;
import com.m1yellow.mypages.entity.UserFollowingType;
import com.m1yellow.mypages.service.UserFollowingTypeService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户关注分类表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@RestController
@RequestMapping("/type")
public class UserFollowingTypeController {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowingTypeController.class);

    @Autowired
    private UserFollowingTypeService userFollowingTypeService;


    @ApiOperation("添加/更新类型")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<UserFollowingType> add(UserFollowingType type) {

        if (type == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userFollowingTypeService.saveOrUpdate(type)) {
            logger.error("添加/更新类型失败");
            return CommonResult.failed("操作失败");
        }

        return CommonResult.success(type);
    }


    @ApiOperation("移除分类类型")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long id) {

        if (userId == null || id == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("id", id);
        if (!userFollowingTypeService.removeByMap(params)) {
            logger.error("移除失败，userId: {}, id: {}", userId, id);
            return CommonResult.failed("操作失败");
        }

        return CommonResult.success();
    }

}
