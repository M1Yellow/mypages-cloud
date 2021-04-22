package com.m1yellow.mypages.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.entity.*;
import com.m1yellow.mypages.service.*;
import com.m1yellow.mypages.vo.home.PlatformItem;
import com.m1yellow.mypages.vo.home.UserFollowingItem;
import com.m1yellow.mypages.vo.home.UserFollowingTypeItem;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页内容 controller
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserPlatformService userPlatformService;
    @Autowired
    private UserBaseService userBaseService;
    @Autowired
    private UserOpinionService userOpinionService;
    @Autowired
    private UserFollowingTypeService userFollowingTypeService;
    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserFollowingRemarkService userFollowingRemarkService;


    @ApiOperation("首页平台所有内容")
    @RequestMapping(value = "platformList")
    public CommonResult<List<PlatformItem>> platformList() {
        // 查询平台表，获取平台数据
        QueryWrapper<UserPlatform> platformQueryWrapper = new QueryWrapper();
        platformQueryWrapper.eq("is_deleted", 0);
        platformQueryWrapper.orderByDesc("sort_no");
        platformQueryWrapper.orderByAsc("id");
        List<UserPlatform> PlatformList = userPlatformService.list(platformQueryWrapper);

        if (PlatformList == null || PlatformList.size() <= 0) { // 平台表数据异常
            logger.error("平台表数据异常");
            return CommonResult.failed();
        }

        // TODO 获取内容代码太长，后续需抽取封装。太长对不熟悉业务的人非常不友好。
        // >>>> 首页平台内容数据封装 >>>>
        List<PlatformItem> platformItemList = new ArrayList<>();
        for (UserPlatform platform : PlatformList) {
            PlatformItem platformItem = new PlatformItem();
            // 平台基础信息封装
            platformItem.setPlatformBaseInfo(platform);

            // 用户对平台看法表内容封装
            QueryWrapper<UserOpinion> opinionQueryWrapper = new QueryWrapper();
            opinionQueryWrapper.eq("is_deleted", 0);
            opinionQueryWrapper.eq("platform_id", platform.getId()); // 关联目标的id，平台id、关注类型id
            opinionQueryWrapper.eq("opinion_type", 0); // 观点对应类型，0-平台；其他-某一类型
            opinionQueryWrapper.orderByDesc("sort_no");
            opinionQueryWrapper.orderByAsc("id");
            List<UserOpinion> platformOpinionList = userOpinionService.list(opinionQueryWrapper);
            platformItem.setPlatformOpinionList(platformOpinionList);

            // 用户关注类型列表封装 start
            QueryWrapper<UserFollowing> followingTypeIdQueryWrapper = new QueryWrapper();
            followingTypeIdQueryWrapper.eq("is_deleted", 0);
            followingTypeIdQueryWrapper.eq("platform_id", platform.getId());
            followingTypeIdQueryWrapper.select("ftype_id");
            followingTypeIdQueryWrapper.groupBy("ftype_id");
            List<UserFollowing> followingTypeIdList = userFollowingService.list(followingTypeIdQueryWrapper);

            // 没有关注用户的类型，可能也有意见看法，查询该平台有看法的类型
            QueryWrapper<UserOpinion> opinionTypeIdQueryWrapper = new QueryWrapper();
            opinionTypeIdQueryWrapper.eq("is_deleted", 0);
            opinionTypeIdQueryWrapper.eq("platform_id", platform.getId());
            opinionTypeIdQueryWrapper.ne("opinion_type", 0); // 0-平台的看法
            opinionTypeIdQueryWrapper.select("opinion_type");
            opinionTypeIdQueryWrapper.groupBy("opinion_type");
            List<UserOpinion> opinionTypeList = userOpinionService.list(opinionTypeIdQueryWrapper);

            // 将关注用户的类型 id 和对平台看法的类型 id 合并。也不不熟，看这里可能会蒙。
            List<Long> typeIdList1 = new ArrayList<>();
            List<Long> typeIdList2 = new ArrayList<>();
            if (followingTypeIdList != null && followingTypeIdList.size() > 0) {
                typeIdList1 = followingTypeIdList.stream()
                        .map(UserFollowing::getFtypeId)
                        .collect(Collectors.toList());
            }
            if (opinionTypeList != null && opinionTypeList.size() > 0) {
                typeIdList2 = opinionTypeList.stream()
                        .map(UserOpinion::getOpinionType)
                        .collect(Collectors.toList());
            }
            if ((typeIdList1 == null || typeIdList1.size() <= 0) && (typeIdList2 == null || typeIdList2.size() <= 0)) {
                // 没关注用户，且没有观点看法的类型，直接返回
                logger.info(platform.getId() + "-" + platform.getName() + " haven't following and type-opinion.");
                platformItemList.add(platformItem);
                continue;
            }
            // 合并，去重
            typeIdList1.addAll(typeIdList2);
            typeIdList1 = typeIdList1.stream().distinct().collect(Collectors.toList());
            logger.info(platform.getId() + "-" + platform.getName() + " type id: " + typeIdList1.toString());

            List<UserFollowingTypeItem> userFollowingTypeList = new ArrayList<>();
            for (Long typeId : typeIdList1) {
                UserFollowingTypeItem userFollowingTypeItem = new UserFollowingTypeItem();
                // 用户关注类型信息封装对象
                UserFollowingType followingType = userFollowingTypeService.getById(typeId);
                userFollowingTypeItem.setUserFollowingTypeInfo(followingType);

                // 用户对关注类型的看法列表
                QueryWrapper<UserOpinion> typeOpinionQueryWrapper = new QueryWrapper();
                typeOpinionQueryWrapper.eq("is_deleted", 0);
                typeOpinionQueryWrapper.eq("platform_id", platform.getId()); // 关联目标的id，平台id、关注类型id
                typeOpinionQueryWrapper.eq("opinion_type", typeId); // 观点对应类型，0-平台；其他-某一类型
                typeOpinionQueryWrapper.orderByDesc("sort_no");
                typeOpinionQueryWrapper.orderByAsc("id");
                List<UserOpinion> typeOpinionList = userOpinionService.list(typeOpinionQueryWrapper);
                userFollowingTypeItem.setUserOpinionList(typeOpinionList);

                // 用户在某类型下的关注用户列表
                QueryWrapper<UserFollowing> typeFollowingQueryWrapper = new QueryWrapper();
                typeFollowingQueryWrapper.eq("is_deleted", 0);
                typeFollowingQueryWrapper.eq("platform_id", platform.getId());
                typeFollowingQueryWrapper.eq("ftype_id", typeId);
                typeFollowingQueryWrapper.orderByDesc("sort_no");
                typeFollowingQueryWrapper.orderByAsc("id");
                List<UserFollowing> typeFollowingList = userFollowingService.list(typeFollowingQueryWrapper);

                // 用户列表封装对象添加对应的标签 List<UserFollowing> 转换为 List<UserFollowingItem>
                List<UserFollowingItem> userFollowingItemList = new ArrayList<>();
                for (UserFollowing userFollowing : typeFollowingList) {
                    UserFollowingItem userFollowingItem = new UserFollowingItem();
                    userFollowingItem.setUserFollowing(userFollowing);

                    // 用户在某类型下的关注用户对应的标签列表
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("following_id", userFollowing.getId());
                    List<UserFollowingRemark> followingRemarkList = userFollowingRemarkService.queryUserFollowingRemarkListRegularly(queryParams);
                    userFollowingItem.setUserFollowingRemarkList(followingRemarkList);

                    userFollowingItemList.add(userFollowingItem);
                }

                // 用户关注类型列表封装 end
                userFollowingTypeItem.setUserFollowingList(userFollowingItemList);
                userFollowingTypeList.add(userFollowingTypeItem);

            }

            // <<<< 首页平台内容数据封装 <<<<
            platformItem.setUserFollowingTypeList(userFollowingTypeList);
            platformItemList.add(platformItem);

        }

        return CommonResult.success(platformItemList);
    }

}
