package cn.m1yellow.mypages.controller;

import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.FastJsonUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.dto.UserFollowingDto;
import cn.m1yellow.mypages.dto.UserPlatformDto;
import cn.m1yellow.mypages.entity.UserFollowingRemark;
import cn.m1yellow.mypages.entity.UserFollowingType;
import cn.m1yellow.mypages.entity.UserOpinion;
import cn.m1yellow.mypages.service.*;
import cn.m1yellow.mypages.vo.home.PlatformItem;
import cn.m1yellow.mypages.vo.home.UserFollowingItem;
import cn.m1yellow.mypages.vo.home.UserFollowingTypeItem;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.service.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页内容 controller
 */
@RestController // 整个 controller 返回 json 格式数据。@ResponseBody 是单个方法返回 json 格式数据
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
    @Autowired
    private RedisUtil redisUtil;


    /*
    RequestMapping 注解中 consumes 与 produces 的区别：
    HTTP协议Header中的ContentType 和Accept

    在 Request 中
    ContentType 用来告诉服务器当前发送的数据是什么格式
    Accept 用来告诉服务器，客户端能认识哪些格式，最好返回这些格式中的其中一种
    accept: Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*\/\*;q=0.8,application/signed-exchange;v=b3;q=0.9

    consumes 用来限制 ContentType
    produces 用来限制 Accept
    charset=utf-8 指定编码格式

    如果使用了 @RestController 或 @ResponseBody，produces 中可以不用重复指定
    , method = RequestMethod.GET, produces = "application/json;charset=utf-8"

    */

    @ApiOperation("首页平台所有内容")
    @RequestMapping(value = "platformList", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<List<PlatformItem>> platformList(@RequestParam Long userId) {

        if (userId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 先从缓存中取，没有再查数据库
        List<PlatformItem> platformListFromCache = getPlatformListFromCache(userId);
        if (platformListFromCache != null && platformListFromCache.size() > 0) {
            return CommonResult.success(platformListFromCache);
        }

        /**
         * 数据查询封装顺序
         * 每个平台的数据 -> 平台下的类型数据 -> 类型下的用户数据
         */

        // 查询平台表，获取平台数据
        Map<String, Object> platformQueryParams = new HashMap<>();
        platformQueryParams.put("userId", userId);
        List<UserPlatformDto> PlatformList = userPlatformService.queryUserPlatformList(platformQueryParams);

        if (PlatformList == null || PlatformList.size() <= 0) { // 平台表数据异常
            logger.error("平台表数据异常");
            return CommonResult.failed("获取平台记录失败");
        }

        // TODO 获取内容代码太长，后续需抽取封装。太长对不熟悉业务的人非常不友好。
        // >>>> 首页平台内容数据封装 >>>>
        List<PlatformItem> platformItemList = new ArrayList<>();
        for (UserPlatformDto platform : PlatformList) {
            PlatformItem platformItem = new PlatformItem();
            // 平台基础信息封装
            platformItem.setPlatformBaseInfo(platform);

            // 用户对平台看法表内容封装
            QueryWrapper<UserOpinion> opinionQueryWrapper = new QueryWrapper();
            //opinionQueryWrapper.eq("is_deleted", 0);
            opinionQueryWrapper.eq("user_id", userId);
            opinionQueryWrapper.eq("platform_id", platform.getPlatformId()); // 对应平台表的id
            opinionQueryWrapper.eq("opinion_type", 0); // 观点对应类型，0-平台；其他-某一类型
            opinionQueryWrapper.orderByDesc("sort_no");
            opinionQueryWrapper.orderByAsc("id"); // TODO 注意，产生 file sort 可能会影响效率
            List<UserOpinion> platformOpinionList = userOpinionService.list(opinionQueryWrapper);
            platformItem.setPlatformOpinionList(platformOpinionList);

            // 关注类型列表封装 start
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("platformId", platform.getId());
            List<Long> typeIdList1 = userFollowingService.queryTypeIdList(params);

            // 没有关注用户的类型，可能也有意见看法（新建的分类，没有用户，但是有观点），查询该平台有观点的类型
            QueryWrapper<UserOpinion> opinionTypeIdQueryWrapper = new QueryWrapper();
            //opinionTypeIdQueryWrapper.eq("is_deleted", 0);
            opinionTypeIdQueryWrapper.eq("user_id", userId);
            opinionTypeIdQueryWrapper.eq("platform_id", platform.getId());
            opinionTypeIdQueryWrapper.ne("opinion_type", 0); // 0-平台的看法
            opinionTypeIdQueryWrapper.select("opinion_type");
            opinionTypeIdQueryWrapper.groupBy("opinion_type");
            List<UserOpinion> opinionTypeList = userOpinionService.list(opinionTypeIdQueryWrapper);

            // 将关注用户的类型 id 和对平台看法的类型 id 合并。业务不熟，看这里可能会懵圈，我自己都懵了。
            List<Long> typeIdList2 = new ArrayList<>();
            if (opinionTypeList != null && opinionTypeList.size() > 0) {
                typeIdList2 = opinionTypeList.stream().map(UserOpinion::getOpinionType)
                        .distinct().collect(Collectors.toList());
            }
            if ((typeIdList1 == null || typeIdList1.size() <= 0) && (typeIdList2 == null || typeIdList2.size() <= 0)) {
                // 没关注用户，且没有观点看法的类型，直接返回
                logger.info(platform.getId() + "-" + platform.getName() + " haven't following and type-opinion.");
                platformItemList.add(platformItem);
                continue;
            }
            // 合并，去重，排序
            typeIdList1.addAll(typeIdList2);
            typeIdList1 = typeIdList1.stream().distinct().sorted().collect(Collectors.toList());
            logger.info(platform.getId() + "-" + platform.getName() + " type id: " + typeIdList1.toString());

            List<UserFollowingTypeItem> userFollowingTypeList = new ArrayList<>();
            for (Long typeId : typeIdList1) {
                UserFollowingTypeItem userFollowingTypeItem = new UserFollowingTypeItem();
                // 用户关注类型信息封装对象
                QueryWrapper<UserFollowingType> followingTypeWrapper = new QueryWrapper();
                followingTypeWrapper.eq("user_id", userId);
                followingTypeWrapper.eq("id", typeId);
                UserFollowingType followingType = userFollowingTypeService.getOne(followingTypeWrapper);
                userFollowingTypeItem.setUserFollowingTypeInfo(followingType);

                // 用户对关注类型的看法列表
                QueryWrapper<UserOpinion> typeOpinionQueryWrapper = new QueryWrapper();
                //typeOpinionQueryWrapper.eq("is_deleted", 0);
                typeOpinionQueryWrapper.eq("user_id", userId);
                typeOpinionQueryWrapper.eq("platform_id", platform.getId()); // 关联目标的id，平台id、关注类型id
                typeOpinionQueryWrapper.eq("opinion_type", typeId); // 观点对应类型，0-平台；其他-某一类型
                typeOpinionQueryWrapper.orderByDesc("sort_no");
                typeOpinionQueryWrapper.orderByAsc("id");
                List<UserOpinion> typeOpinionList = userOpinionService.list(typeOpinionQueryWrapper);
                userFollowingTypeItem.setUserOpinionList(typeOpinionList);

                // 用户在平台某类型下的关注用户列表
                params.clear();
                params.put("userId", userId);
                params.put("platformId", platform.getId());
                params.put("typeId", typeId);
                List<UserFollowingDto> typeFollowingList = userFollowingService.queryUserFollowingList(params);

                // 用户列表封装对象添加对应的标签 List<UserFollowing> 转换为 List<UserFollowingItem>
                List<UserFollowingItem> userFollowingItemList = new ArrayList<>();
                for (UserFollowingDto userFollowing : typeFollowingList) {
                    UserFollowingItem userFollowingItem = new UserFollowingItem();
                    userFollowingItem.setUserFollowing(userFollowing);

                    // 用户在某类型下的关注用户对应的标签列表
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("user_id", userId);
                    queryParams.put("following_id", userFollowing.getFollowingId()); // 对应用户关注表的id
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

        // 查询完成之后，设置缓存
        if (platformItemList != null && platformItemList.size() > 0) {
            redisUtil.set(GlobalConstant.HOME_PLATFORM_LIST_CACHE_KEY + userId, FastJsonUtil.bean2Json(platformItemList),
                    GlobalConstant.HOME_PLATFORM_LIST_CACHE_TIME);
        }

        return CommonResult.success(platformItemList);
    }


    /**
     * 从缓存中获取首页数据
     *
     * @param userId
     * @return
     */
    private List<PlatformItem> getPlatformListFromCache(Long userId) {
        List<PlatformItem> platformItemList = null;
        String platformListCache;
        if (userId != null) { // 用户已登录，根据用户id取缓存
            platformListCache = ObjectUtil.getString(redisUtil.get(GlobalConstant.HOME_PLATFORM_LIST_CACHE_KEY + userId));
        } else { // TODO 用户没登录，默认显示的首页数据
            platformListCache = ObjectUtil.getString(redisUtil.get(GlobalConstant.HOME_PLATFORM_LIST_DEFAULT_CACHE_KEY));
        }
        if (StringUtils.isNotBlank(platformListCache)) {
            // gson 对层层嵌套的复杂对象，由于序列化泛型擦除，类型对应不上，会导致页面解析不了！
            // fastJson debug 能对应上类型，页面也能解析对应类型
            platformItemList = FastJsonUtil.json2List(platformListCache, PlatformItem.class);
        }

        return platformItemList;
    }


    @ApiOperation("全局参数列表")
    @RequestMapping(value = "properties", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<Map<String, Object>> getProperties() {

        Map<String, Object> properties = new HashMap<>();

        // 排序优先级
        int[] sortValues = GlobalConstant.SORT_VALUES;

        properties.put("sortValues", sortValues);

        return CommonResult.success(properties);
    }


}
