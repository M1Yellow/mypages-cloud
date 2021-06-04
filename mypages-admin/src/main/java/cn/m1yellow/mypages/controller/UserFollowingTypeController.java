package cn.m1yellow.mypages.controller;


import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.exception.AtomicityException;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.entity.UserFollowingRelation;
import cn.m1yellow.mypages.entity.UserFollowingType;
import cn.m1yellow.mypages.service.UserFollowingRelationService;
import cn.m1yellow.mypages.service.UserFollowingService;
import cn.m1yellow.mypages.service.UserFollowingTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
@RestController
@RequestMapping("/type")
public class UserFollowingTypeController {

    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserFollowingTypeService userFollowingTypeService;
    @Autowired
    private UserFollowingRelationService userFollowingRelationService;
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation("添加/更新类型")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @Caching(evict = {
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).HOME_PLATFORM_LIST_CACHE_KEY + #type.userId"),
            @CacheEvict(value = GlobalConstant.CACHE_10MIN, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_TYPE_LIST_CACHE_KEY + #type.userId + '_' + #type.platformId")
    })
    public CommonResult<UserFollowingType> add(UserFollowingType type) {

        // id为0表示默认类型，默认类型系统自动管理，用户不能自己创建或编辑
        if (type == null || (type.getId() != null && type.getId() < 1)) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 校验必须参数
        if (type.getUserId() == null || type.getPlatformId() == null || StringUtils.isBlank(type.getTypeName())) {
            log.error("typeName must be not null.");
            return CommonResult.failed("请检查必须参数");
        }

        // 先判断用户是否已经有相同名称的标签
        QueryWrapper<UserFollowingType> typeQueryWrapper = new QueryWrapper<>();
        typeQueryWrapper.eq("user_id", type.getUserId());
        typeQueryWrapper.eq("platform_id", type.getPlatformId());
        typeQueryWrapper.eq("type_name", type.getTypeName());
        UserFollowingType existedType = userFollowingTypeService.getOne(typeQueryWrapper);
        if (existedType != null) {
            // 存在相同名称的标签，则使用原来的id，执行更新操作
            type.setId(existedType.getId());
        }

        // 去字符串字段两边空格
        ObjectUtil.stringFiledTrim(type);

        if (!userFollowingTypeService.saveOrUpdate(type)) {
            log.error("添加/更新类型失败");
            return CommonResult.failed("操作失败");
        }

        return CommonResult.success(type);
    }


    @ApiOperation("获取分类类型列表")
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @Cacheable(value = GlobalConstant.CACHE_10MIN, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_TYPE_LIST_CACHE_KEY + #userId + '_' + #platformId", unless = "#result==null")
    public CommonResult<List<UserFollowingType>> list(@RequestParam Long userId, @RequestParam Long platformId) {

        if (userId == null || platformId == null) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // TODO 添加游离于数据库类型表之外的默认类型，方便共用
        List<UserFollowingType> UserFollowingTypeMergeList = userFollowingTypeService.getUserFollowingTypeMergeList(userId, platformId, null);

        return CommonResult.success(UserFollowingTypeMergeList);
    }


    @ApiOperation("移除分类类型")
    @Transactional // 加入事务支持
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @Caching(evict = {
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).HOME_PLATFORM_LIST_CACHE_KEY + #userId"),
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_FOLLOWING_PAGE_LIST_CACHE_KEY + #userId + '_' + #platformId + '_' + #typeId"),
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_FOLLOWING_PAGE_LIST_CACHE_KEY + #userId + '_' + #platformId + '_' + 0"),
            @CacheEvict(value = GlobalConstant.CACHE_10MIN, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_TYPE_LIST_CACHE_KEY + #userId + '_' + #platformId")
    })
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long platformId, @RequestParam Long typeId) {

        // id为0表示默认类型，默认类型系统自动管理
        if (userId == null || typeId == null || typeId < 1 || platformId == null) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询类型记录
        QueryWrapper<UserFollowingType> typeQueryWrapper = new QueryWrapper<>();
        typeQueryWrapper.eq("user_id", userId);
        typeQueryWrapper.eq("platform_id", platformId);
        typeQueryWrapper.eq("id", typeId);
        UserFollowingType type = userFollowingTypeService.getOne(typeQueryWrapper);
        if (type == null) {
            log.error("用户类型信息查询失败");
            return CommonResult.failed("用户类型信息查询失败");
        }

        // 删除类型记录，删除之后，其下的关注用户分配到默认分类
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("platform_id", platformId);
        params.put("id", typeId);
        if (!userFollowingTypeService.removeByMap(params)) {
            log.error("移除失败，userId: {}, platformId: {}, typeId: {}", userId, platformId, typeId);
            throw new AtomicityException("操作失败");
        }

        // 先查询要删除的类型下是否有关注用户
        QueryWrapper<UserFollowingRelation> relationQueryWrapper = new QueryWrapper<>();
        relationQueryWrapper.eq("user_id", userId);
        relationQueryWrapper.eq("platform_id", platformId);
        relationQueryWrapper.eq("type_id", typeId);
        relationQueryWrapper.select("id");
        List<UserFollowingRelation> followingList = userFollowingRelationService.list(relationQueryWrapper);

        if (followingList != null && followingList.size() > 0) {
            // TODO 更改关注用户类型，注意！！更新记录为0的结果是false，有可能数据库记录本来就是0条，这个判断不符合业务要求
            boolean result = userFollowingRelationService.changeUserFollowingTypeByTypeId(userId, platformId, typeId,
                    GlobalConstant.USER_FOLLOWING_DEFAULT_TYPE_ID);
            if (!result) {
                log.error("删除类型后变更其下用户类型失败");
                throw new AtomicityException("删除类型后变更其下用户类型失败");
            }
        }

        return CommonResult.success();
    }

}
