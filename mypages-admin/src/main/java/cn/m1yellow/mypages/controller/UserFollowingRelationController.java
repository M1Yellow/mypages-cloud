package cn.m1yellow.mypages.controller;


import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.aspect.DoCache;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.CheckParamUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.dto.UserFollowingDto;
import cn.m1yellow.mypages.dto.UserFollowingRelationDto;
import cn.m1yellow.mypages.entity.UserFollowingRelation;
import cn.m1yellow.mypages.service.UserFollowingRelationService;
import cn.m1yellow.mypages.service.UserFollowingService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户与关注用户关联表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-23
 */
@RestController
@RequestMapping("/following-relation")
public class UserFollowingRelationController {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowingRelationController.class);

    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserFollowingRelationService userFollowingRelationService;


    @ApiOperation("添加/更新用户关系")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    // 新增或修改关注用户，清除首页缓存、分页缓存
    @Caching(evict = {
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).HOME_PLATFORM_LIST_CACHE_KEY + #relation.userId"),
            // cacheKey 格式：USER_FOLLOWING_PAGE_LIST_CACHE_1_3_9
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_FOLLOWING_PAGE_LIST_CACHE_KEY + #relation.userId + '_' + #relation.platformId + '_' + #relation.typeId"),
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_FOLLOWING_PAGE_LIST_CACHE_KEY + #relation.userId + '_' + #relation.platformId + '_' + #relation.oldTypeId", condition = "#relation.oldTypeId!=null")
    })
    public CommonResult<UserFollowingDto> add(UserFollowingRelationDto relation) {

        CheckParamUtil.validate(relation);

        // 是否为新增记录，默认是
        boolean isNew = true;
        if (relation.getId() != null) {
            isNew = false;
        }

        UserFollowingRelation saveRelation = new UserFollowingRelation();
        BeanUtils.copyProperties(relation, saveRelation);

        // 去字符串字段两边空格
        ObjectUtil.stringFiledTrim(saveRelation);

        // 判断自己是否已经存在，存在，则使用已存在记录的id，执行更新操作
        Map<String, Object> params = new HashMap<>();
        params.put("userId", relation.getUserId());
        params.put("platformId", relation.getPlatformId()); // TODO 冗余筛选字段，有利于高效利用组合索引
        params.put("typeId", relation.getTypeId());
        params.put("followingId", relation.getFollowingId());
        UserFollowingDto existedFollowing = userFollowingService.getUserFollowing(params);
        if (existedFollowing != null) {
            saveRelation.setId(existedFollowing.getId());
        }

        if (!userFollowingRelationService.saveOrUpdate(saveRelation)) {
            logger.error("添加/更新用户关系失败");
            return CommonResult.failed("操作失败");
        }

        // 重新加载封装对象
        UserFollowingDto reloadFollowing = null;
        if (!isNew) { // 更新记录才重新加载，如果是新增记录，首页会刷新然后重新请求加载
            params.clear();
            params.put("userId", relation.getUserId());
            params.put("platformId", relation.getPlatformId()); // TODO 冗余筛选字段，有利于高效利用组合索引
            params.put("typeId", relation.getTypeId());  // 同上
            params.put("followingId", relation.getFollowingId());
            reloadFollowing = userFollowingService.getUserFollowing(params);
        }

        return CommonResult.success(reloadFollowing);
    }

}
