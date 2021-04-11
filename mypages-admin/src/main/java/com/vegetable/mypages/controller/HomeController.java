package com.vegetable.mypages.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vegetable.mypages.common.api.CommonResult;
import com.vegetable.mypages.constant.PlatformInfo;
import com.vegetable.mypages.entity.*;
import com.vegetable.mypages.excavation.bo.UserInfoItem;
import com.vegetable.mypages.excavation.service.DataExcavateService;
import com.vegetable.mypages.service.*;
import com.vegetable.mypages.vo.home.PlatformItem;
import com.vegetable.mypages.vo.home.UserFollowingItem;
import com.vegetable.mypages.vo.home.UserFollowingTypeItem;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页内容 controller
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private PlatformService platformService;
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
    @Resource(name = "dataOfBiliExcavateService")
    private DataExcavateService dataOfBiliExcavateService;
    @Resource(name = "dataOfWeiboExcavateService")
    private DataExcavateService dataOfWeiboExcavateService;


    @ApiOperation("首页平台所有内容")
    @RequestMapping(value = "platformList")
    public CommonResult<List<PlatformItem>> platformList() {
        // 查询平台表，获取平台数据
        QueryWrapper<Platform> platformQueryWrapper = new QueryWrapper();
        platformQueryWrapper.eq("is_deleted", 0);
        platformQueryWrapper.orderByDesc("sort_no");
        platformQueryWrapper.orderByAsc("id");
        List<Platform> PlatformList = platformService.list(platformQueryWrapper);

        if (PlatformList == null || PlatformList.size() <= 0) { // 平台表数据异常
            logger.error("平台表数据异常");
            return CommonResult.failed();
        }

        // >>>> 首页平台内容数据封装 >>>>
        List<PlatformItem> platformItemList = new ArrayList<>();
        for (Platform platform : PlatformList) {
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
                    QueryWrapper<UserFollowingRemark> followingRemarkQueryWrapper = new QueryWrapper();
                    followingRemarkQueryWrapper.eq("is_deleted", 0);
                    followingRemarkQueryWrapper.eq("following_id", userFollowing.getId());
                    followingRemarkQueryWrapper.orderByDesc("sort_no");
                    followingRemarkQueryWrapper.orderByAsc("id");
                    List<UserFollowingRemark> followingRemarkList = userFollowingRemarkService.list(followingRemarkQueryWrapper);
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


    @ApiOperation("同步关注用户的信息")
    //@RequestMapping(value = "syncFollowingUserInfo/{fuid}")
    @RequestMapping(value = "syncFollowingUserInfo")
    public CommonResult<UserInfoItem> syncFollowingUserInfo(@RequestParam Long platformId, @RequestParam Long fuid) {

        if (platformId == null || fuid == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询用户主页
        QueryWrapper<UserFollowing> followingQueryWrapper = new QueryWrapper();
        followingQueryWrapper.eq("is_deleted", 0);
        followingQueryWrapper.eq("platform_id", platformId);
        followingQueryWrapper.eq("id", fuid);
        followingQueryWrapper.eq("is_user", 1);
        UserFollowing following = userFollowingService.getOne(followingQueryWrapper);

        if (following == null) {
            logger.error("关注用户表不存在id:" + fuid);
            return CommonResult.failed();
        }

        /**
         * 使用工程相对路径是靠不住的。
         * 使用CLASSPATH路径是可靠的。
         * 对于程序要读取的文件，尽可能放到CLASSPATH下，这样就能保证在开发和发布时候均正常读取。
         */
        // 获取用户信息
        UserInfoItem userInfoItem = doExcavate(following);
        if (userInfoItem == null) {
            logger.error("用户信息获取失败，following id:" + fuid);
            return CommonResult.failed();
        }

        // 更新信息，保存入库
        saveUserInfo(userInfoItem, following);

        return CommonResult.success(userInfoItem);
    }


    @ApiOperation("批量同步关注用户的信息")
    @RequestMapping(value = "syncFollowingUserInfoBatch")
    public CommonResult<List<UserInfoItem>> syncFollowingUserInfoBatch(@RequestParam Long platformId) {

        if (platformId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询用户主页
        QueryWrapper<UserFollowing> followingQueryWrapper = new QueryWrapper();
        followingQueryWrapper.eq("is_deleted", 0);
        followingQueryWrapper.eq("platform_id", platformId);
        followingQueryWrapper.eq("is_user", 1);

        /*
        新增数据
        -- 8-思想、学习；7-美食、营养；6、健身、锻炼；5-兴趣、生活；4~其他
        select * from user_following where platform_id = 2 order by sort_no desc, id asc;

        INSERT INTO `mypage`.`user_following` (`user_id`, `platform_id`, `ftype_id`, `name`, `main_page`) VALUES (1, 3, 3, '帅soserious', 'https://m.weibo.cn/u/2289940200');

        select @muid := max(id) from user_following;
        INSERT INTO `mypage`.`user_following_remark` (`user_id`, `following_id`, `label_name`)
        VALUES
        (1, @muid, '时尚'),
        (1, @muid, '穿搭'),
        (1, @muid, '生活');

        处理中断之后的数据
        select * from user_following where platform_id = 3 and is_user = 1 and is_deleted = 0 and signature like '这个人%';

        */
        //followingQueryWrapper.like("signature", "这个人%");
        //followingQueryWrapper.gt("id", 59);
        followingQueryWrapper.isNull("profile_photo");

        List<UserFollowing> userFollowingList = userFollowingService.list(followingQueryWrapper);

        if (userFollowingList == null || userFollowingList.size() <= 0) {
            logger.error("关注用户表数据异常");
            return CommonResult.failed();
        }

        List<UserInfoItem> userInfoItemList = new ArrayList<>();
        for (UserFollowing following : userFollowingList) {
            // 获取用户信息
            UserInfoItem userInfoItem = doExcavate(following);
            if (userInfoItem == null) {
                logger.error("用户信息获取失败，following id:" + following.getId());
                return CommonResult.failed();
            }

            // 更新信息，保存入库
            saveUserInfo(userInfoItem, following);

            userInfoItemList.add(userInfoItem);

            // 避免频繁访问被封
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return CommonResult.success(userInfoItemList);
    }


    /**
     * 执行用户信息同步
     *
     * @param following
     * @return
     */
    private UserInfoItem doExcavate(UserFollowing following) {

        //Map<String, Object> params = new HashMap<>();
        String saveDir = "mypages-admin/src/main/resources/public/images/user-profile-photo/";
        String apiUrl = null; // 请求地址模板
        String fromUrl = null; // 调整后的请求地址

        UserInfoItem userInfoItem = null;

        switch (PlatformInfo.getPlatformInfo(following.getPlatformId().intValue())) {
            case BILIBILI:
                apiUrl = "https://api.bilibili.com/x/space/acc/info?mid=userId&jsonp=jsonp";
                fromUrl = apiUrl.replace("userId", following.getMainPage().split("/")[3]);
                userInfoItem = dataOfBiliExcavateService.singleImageDownloadFromJson(fromUrl, saveDir, null);
                break;
            case WEIBO:
                //apiUrl = "https://weibo.com/u/userId";
                //apiUrl = "https://m.weibo.cn/api/container/getIndex?type=uid&value=userId&containerid=1005056488142313";
                apiUrl = "https://m.weibo.cn/api/container/getIndex?type=uid&value=userId";
                fromUrl = following.getMainPage();
                if (following.getMainPage().indexOf("m.weibo.cn") > -1) {
                    // h5 地址统一调整为 web 请求地址
                    fromUrl = apiUrl.replace("userId", following.getMainPage().split("/")[4]);
                }
                userInfoItem = dataOfWeiboExcavateService.singleImageDownloadFromJson(fromUrl, saveDir, null);
                break;
            case DOUBAN:

                break;
            case ZHIHU:

                break;
            default:

        }
        return userInfoItem;
    }


    /**
     * 同步保存用户信息
     *
     * @param userInfoItem 用户信息封装对象
     * @param following    数据库关注用户表实体对象
     * @return
     */
    private boolean saveUserInfo(UserInfoItem userInfoItem, UserFollowing following) {
        boolean isSuc = false;

        following.setName(userInfoItem.getUserName());
        following.setSignature(userInfoItem.getSignature());
        following.setProfilePhoto(userInfoItem.getHeadImgPath());

        isSuc = userFollowingService.saveOrUpdate(following);

        return isSuc;
    }


}
