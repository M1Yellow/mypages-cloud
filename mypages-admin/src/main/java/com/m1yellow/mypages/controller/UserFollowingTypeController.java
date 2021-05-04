package com.m1yellow.mypages.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.aspect.DoCache;
import com.m1yellow.mypages.common.aspect.WebLog;
import com.m1yellow.mypages.common.constant.GlobalConstant;
import com.m1yellow.mypages.common.util.FastJsonUtil;
import com.m1yellow.mypages.common.util.ObjectUtil;
import com.m1yellow.mypages.common.util.RedisUtil;
import com.m1yellow.mypages.entity.UserFollowingType;
import com.m1yellow.mypages.service.UserFollowingTypeService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation("添加/更新类型")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<UserFollowingType> add(UserFollowingType type) {

        if (type == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 校验必须参数
        if (StringUtils.isBlank(type.getTypeName())) {
            logger.error("typeName must be not null.");
            return CommonResult.failed("请检查必须参数");
        }

        if (!userFollowingTypeService.saveOrUpdate(type)) {
            logger.error("添加/更新类型失败");
            return CommonResult.failed("操作失败");
        }

        // 新增或修改记录后，清空缓存
        String cacheKey = GlobalConstant.USER_TYPE_LIST_CACHE_KEY + type.getUserId();
        redisUtil.del(cacheKey);
        logger.info(">>>> type add 删除用户对应类型列表缓存，cache key: {}", cacheKey);

        return CommonResult.success(type);
    }


    @ApiOperation("获取分类类型列表")
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<List<UserFollowingType>> list(@RequestParam Long userId, @RequestParam(required = false) Long platformId) {

        if (userId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 先从缓存中获取
        String cacheStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.USER_TYPE_LIST_CACHE_KEY + userId));
        if (StringUtils.isNotBlank(cacheStr)) {
            List<UserFollowingType> UserFollowingTypeList = FastJsonUtil.json2List(cacheStr, UserFollowingType.class);
            if (UserFollowingTypeList != null && UserFollowingTypeList.size() > 0) {
                return CommonResult.success(UserFollowingTypeList);
            }
        }

        QueryWrapper<UserFollowingType> typeQueryWrapper = new QueryWrapper<>();
        typeQueryWrapper.eq("user_id", userId);
        typeQueryWrapper.orderByDesc("sort_no");
        List<UserFollowingType> UserFollowingTypeList = userFollowingTypeService.list(typeQueryWrapper);

        // 查询完成之后，设置缓存
        if (UserFollowingTypeList != null && UserFollowingTypeList.size() > 0) {
            redisUtil.set(GlobalConstant.USER_TYPE_LIST_CACHE_KEY + userId, FastJsonUtil.bean2Json(UserFollowingTypeList),
                    GlobalConstant.USER_PLATFORM_TYPE_LIST_CACHE_TIME);
        }

        return CommonResult.success(UserFollowingTypeList);
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
