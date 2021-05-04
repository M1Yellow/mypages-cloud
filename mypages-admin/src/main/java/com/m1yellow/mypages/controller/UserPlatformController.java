package com.m1yellow.mypages.controller;


import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.aspect.DoCache;
import com.m1yellow.mypages.common.aspect.WebLog;
import com.m1yellow.mypages.common.constant.GlobalConstant;
import com.m1yellow.mypages.common.util.CheckParamUtil;
import com.m1yellow.mypages.common.util.FastJsonUtil;
import com.m1yellow.mypages.common.util.ObjectUtil;
import com.m1yellow.mypages.common.util.RedisUtil;
import com.m1yellow.mypages.dto.UserPlatformDto;
import com.m1yellow.mypages.entity.UserPlatform;
import com.m1yellow.mypages.service.UserPlatformService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
    private RedisUtil redisUtil;


    @ApiOperation("添加/更新平台")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<UserPlatformDto> add(UserPlatformDto platform) {

        CheckParamUtil.validate(platform);

        UserPlatform savePlatform = new UserPlatform();
        BeanUtils.copyProperties(platform, savePlatform);
        // 纠正id
        savePlatform.setId(platform.getPlatformId());

        if (!userPlatformService.saveOrUpdate(savePlatform)) {
            logger.error("添加/更新平台失败");
            return CommonResult.failed("操作失败");
        }

        // 新增或修改记录后，清空缓存
        String cacheKey = GlobalConstant.USER_PLATFORM_LIST_CACHE_KEY + platform.getUserId();
        redisUtil.del(cacheKey);
        logger.info(">>>> platform add 删除用户对应平台列表缓存，cache key: {}", cacheKey);

        return CommonResult.success();
    }


    @ApiOperation("获取平台列表")
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<List<UserPlatformDto>> list(@RequestParam Long userId) {

        if (userId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 先从缓存中获取
        String cacheStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.USER_PLATFORM_LIST_CACHE_KEY + userId));
        if (StringUtils.isNotBlank(cacheStr)) {
            List<UserPlatformDto> userPlatformList = FastJsonUtil.json2List(cacheStr, UserPlatformDto.class);
            if (userPlatformList != null && userPlatformList.size() > 0) {
                return CommonResult.success(userPlatformList);
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<UserPlatformDto> userPlatformList = userPlatformService.queryUserPlatformList(params);

        // 查询完成之后，设置缓存
        if (userPlatformList != null && userPlatformList.size() > 0) {
            redisUtil.set(GlobalConstant.USER_PLATFORM_LIST_CACHE_KEY + userId, FastJsonUtil.bean2Json(userPlatformList),
                    GlobalConstant.USER_PLATFORM_TYPE_LIST_CACHE_TIME);
        }

        return CommonResult.success(userPlatformList);
    }


    @ApiOperation("移除平台")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<String> remove(@RequestParam Long id) {

        if (id == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userPlatformService.removeById(id)) {
            logger.error("移除平台失败，id: {}", id);
            return CommonResult.failed("操作失败");
        }

        return CommonResult.success();
    }

}
