package com.m1yellow.mypages.controller;


import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.aspect.DoCache;
import com.m1yellow.mypages.common.aspect.WebLog;
import com.m1yellow.mypages.common.util.CheckParamUtil;
import com.m1yellow.mypages.dto.UserFollowingDto;
import com.m1yellow.mypages.dto.UserFollowingRelationDto;
import com.m1yellow.mypages.entity.UserFollowingRelation;
import com.m1yellow.mypages.service.UserFollowingRelationService;
import com.m1yellow.mypages.service.UserFollowingService;
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

        if (!userFollowingRelationService.saveOrUpdate(saveRelation)) {
            logger.error("添加/更新用户关系失败");
            return CommonResult.failed("操作失败");
        }

        // 重新加载封装对象
        UserFollowingDto reloadFollowing = null;
        if (!isNew) { // 更新记录才重新加载，新增记录，首页会整体重新加载
            Map<String, Object> params = new HashMap<>();
            params.put("userId", relation.getUserId());
            params.put("followingId", relation.getFollowingId());
            reloadFollowing = userFollowingService.getUserFollowing(params);
        }

        return CommonResult.success(reloadFollowing);
    }

}
