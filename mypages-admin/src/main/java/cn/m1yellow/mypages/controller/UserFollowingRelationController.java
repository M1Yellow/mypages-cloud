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

        if (!isNew) { // 更新关联关系，比如，变更类型分组
            // 清空关注用户缓存
            // cacheKey 格式：USER_FOLLOWING_PAGE_LIST_CACHE_1_3_9
            String cacheKey = GlobalConstant.USER_FOLLOWING_PAGE_LIST_CACHE_KEY + relation.getUserId()
                    + "_" + relation.getPlatformId() + "_" + relation.getTypeId();
            userFollowingService.operatingFollowingItemPageCache(cacheKey, null, null, true, null);
            logger.info(">>>> user following relation update 清空关注用户缓存，cacheKey: {}", cacheKey);

            // 变更分组，清除之前分组的用户列表缓存
            if (relation.getOldTypeId() != null) {
                cacheKey = GlobalConstant.USER_FOLLOWING_PAGE_LIST_CACHE_KEY + relation.getUserId()
                        + "_" + relation.getPlatformId() + "_" + relation.getOldTypeId();
                userFollowingService.operatingFollowingItemPageCache(cacheKey, null, null, true, null);
                logger.info(">>>> user following relation update 清空原始分组关注用户缓存，cacheKey: {}", cacheKey);
            }

        }

        return CommonResult.success(reloadFollowing);
    }

}
