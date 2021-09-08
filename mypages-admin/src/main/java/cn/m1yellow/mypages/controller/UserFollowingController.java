package cn.m1yellow.mypages.controller;


import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.exception.AtomicityException;
import cn.m1yellow.mypages.common.exception.FileSaveException;
import cn.m1yellow.mypages.common.service.OssService;
import cn.m1yellow.mypages.common.util.CheckParamUtil;
import cn.m1yellow.mypages.common.util.CommonUtil;
import cn.m1yellow.mypages.common.util.FileUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.component.UserInfoSyncSender;
import cn.m1yellow.mypages.constant.PlatformInfo;
import cn.m1yellow.mypages.dto.UserFollowingDto;
import cn.m1yellow.mypages.entity.UserFollowing;
import cn.m1yellow.mypages.entity.UserFollowingRelation;
import cn.m1yellow.mypages.entity.UserFollowingRemark;
import cn.m1yellow.mypages.service.UserFollowingRelationService;
import cn.m1yellow.mypages.service.UserFollowingRemarkService;
import cn.m1yellow.mypages.service.UserFollowingService;
import cn.m1yellow.mypages.service.UserInfoExcavationService;
import cn.m1yellow.mypages.vo.home.UserFollowingItem;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户关注表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@Slf4j
@RestController
@RequestMapping("/following")
public class UserFollowingController {

    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserFollowingRelationService userFollowingRelationService;
    @Autowired
    private UserFollowingRemarkService userFollowingRemarkService;
    @Autowired
    private UserInfoSyncSender userInfoSyncSender;
    @Autowired
    private UserInfoExcavationService userInfoExcavationService;
    @Autowired
    private OssService ossService;

    @Value("${user.avatar.savedir}")
    private String saveDir;

    @Value("${aliyun.oss.bucketName}")
    private String ALIYUN_OSS_BUCKET_NAME;
    @Value("${aliyun.oss.dir.avatar}")
    private String ALIYUN_OSS_DIR_AVATAR;


    /*
    // TODO 技术知识点回顾记录，温故而知新
    @RequestPart与@RequestParam的区别
    @RequestPart这个注解用在multipart/form-data表单提交请求的方法上。 支持的请求方法的方式MultipartFile，属于Spring的MultipartResolver类。这个请求是通过http协议传输的。
    @RequestParam也同样支持multipart/form-data请求。 他们最大的不同是，当请求方法的请求参数类型不再是String类型的时候。
    @RequestParam适用于name-valueString类型的请求域，@RequestPart适用于复杂的请求域（像JSON，XML）。
    @RequestParam <= 注解空缺
    注意，application/json 和 multipart/form-data 不兼容。传文件，就传不了 json 对象，但可以传字符串，后台解析转换为对象

    @Transactional 失效的场景？
    https://juejin.cn/post/6844904096747503629
    https://blog.csdn.net/weixin_41816847/article/details/105995453

    */


    @ApiOperation("添加/更新关注用户")
    @Transactional //(rollbackFor = {AtomicityException.class, FileSaveException.class})
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    // TODO 【清除多个缓存】新增或修改关注用户，清除首页缓存、分页缓存
    @Caching(evict = {
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).HOME_PLATFORM_LIST_CACHE_KEY + #following.userId"),
            // cacheKey 格式：USER_FOLLOWING_PAGE_LIST_CACHE_1_3_9
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_FOLLOWING_PAGE_LIST_CACHE_KEY + #following.userId + '_' + #following.platformId + '_' + #following.typeId")
    })
    public CommonResult<UserFollowingItem> add(UserFollowingDto following, @RequestPart(required = false) MultipartFile profile) {

        // 基础参数校验
        CheckParamUtil.validate(following);

        // 进一步校验 url 是否跟平台对应
        PlatformInfo platformInfo = PlatformInfo.getPlatformInfoByUrl(CommonUtil.getSimpleUrl(following.getMainPage()));
        if (platformInfo == null) {
            log.error("获取平台信息失败");
            return CommonResult.failed("获取平台信息失败");
        }
        if (platformInfo.getId() != following.getPlatformId().intValue()) {
            log.error("主页跟平台不对应，或者平台id参数错误");
            return CommonResult.failed("请检查用户主页跟平台是否对应");
        }

        // 是否为新增记录，默认是
        boolean isNew = true;
        if (following.getFollowingId() != null) {
            isNew = false;
        }

        // 新增的非用户头像不能为空
        if (!following.getIsUser() && isNew) {
            if (profile == null) {
                log.error("新增非用户需要上传头像");
                return CommonResult.failed("新增非用户需要上传头像");
            }
        }


        // 查询是否存在记录
        Map<String, Object> params = new HashMap<>();
        params.put("userId", following.getUserId());
        params.put("platformId", following.getPlatformId()); // TODO 冗余筛选字段，有利于高效利用组合索引
        params.put("typeId", following.getTypeId());
        params.put("followingId", following.getFollowingId());

        UserFollowingDto originalFollowing = null;
        if (isNew) { // 新增
            originalFollowing = new UserFollowingDto();
        } else { // 更新
            // 查询数据库中的记录
            originalFollowing = userFollowingService.getUserFollowing(params);
            if (originalFollowing == null) {
                log.debug("用户信息加载异常");
                return CommonResult.failed("用户信息加载失败");
            }
            // TODO 用户能改的内容，共用会出现内容不一致问题，多人共用一样东西，一个人把东西搞坏了，其他人还怎么用啊？
            // 编辑的时候不支持更改用户类型
            if (following.getIsUser() != originalFollowing.getIsUser()) {
                log.debug("编辑的时候不支持更改用户类型");
                return CommonResult.failed("暂不支持更改用户类型");
            }
        }
        // 旧头像路径，用于删除旧头像
        String oldFilePath = originalFollowing.getProfilePhoto();

        if (profile != null) {
            // 获取新头像相对路径
            //String newFilePath = FileUtil.getFilePath(UserFollowingController.class, profile, saveDir, oldFilePath, true, false);
            // OSS 相对路径
            String newFilePath = FileUtil.getFilePath(UserFollowingController.class, profile, ALIYUN_OSS_DIR_AVATAR, oldFilePath, true, false);
            log.info(">>>> init newFilePath: {}", newFilePath);
            // 设置新头像路径
            following.setProfilePhoto(newFilePath);
        }

        // 获取关注用户所在平台的id或标识
        if (isNew) {
            String userKey = userFollowingService.getUserKeyFromMainPage(following);
            if (StringUtils.isBlank(userKey)) {
                log.error("获取关注用户所在平台标识失败");
                throw new AtomicityException("获取关注用户所在平台标识失败");
            }
            following.setUserKey(userKey);
        }

        // 保存关注用户
        UserFollowing saveFollowing = new UserFollowing();

        // 设置修改内容
        /*
        // beanCopier.copy 要求类型一致
        BeanCopier beanCopier = BeanCopier.create(UserFollowingDto.class, UserFollowing.class, true);
        Converter converter = new CopyConverter();
        beanCopier.copy(following, originalFollowing, converter);
        beanCopier.copy(following, saveFollowing, converter);
        */
        //BeanUtils.copyProperties(following, originalFollowing);
        BeanUtils.copyProperties(following, saveFollowing); // 属性为null也会被复制，内部类不会复制过去

        // 修正 id
        if (following.getFollowingId() != null && following.getFollowingId() > 0) {
            saveFollowing.setId(following.getFollowingId());
        } else {
            // TODO 确认同平台、同用户标识是否已经有关注的用户（别人添加的关注用户也可以），如果有，直接用这个用户id
            // 是用户才共用id，非用户，不共用
            if (saveFollowing.getIsUser() && saveFollowing.getPlatformId() != null
                    && StringUtils.isNotBlank(saveFollowing.getUserKey())) {
                QueryWrapper<UserFollowing> existedFollowingWrapper = new QueryWrapper<>();
                existedFollowingWrapper.eq("platform_id", saveFollowing.getPlatformId());
                existedFollowingWrapper.eq("user_key", saveFollowing.getUserKey());
                UserFollowing existedFollowing = userFollowingService.getOne(existedFollowingWrapper);
                if (existedFollowing != null && existedFollowing.getId() != null) {
                    saveFollowing.setId(existedFollowing.getId());
                }
            }
        }

        // 去字符串字段两边空格
        ObjectUtil.stringFiledTrim(saveFollowing);
        // 保存入库
        if (!userFollowingService.saveOrUpdate(saveFollowing)) {
            log.error("添加关注用户失败");
            throw new AtomicityException("添加关注用户失败");
        }

        // 校验保存后的新增用户
        if (saveFollowing.getId() == null || saveFollowing.getId() <= 0) {
            log.error("新增用户信息错误");
            throw new AtomicityException("新增用户信息错误");
        }

        // 设置保存后的 following id
        following.setFollowingId(saveFollowing.getId());

        // 新增保存用户与关注用户关系之前，先根据 userId 和 followingId 判断是否已经存在记录
        // 为什么不跟上面的检查关注用户是否存在一起校验？上面是判断别人有没有关注用户，现在是判断自己
        if (following.getId() == null) {
            QueryWrapper<UserFollowingRelation> existedRelationWrapper = new QueryWrapper<>();
            existedRelationWrapper.eq("user_id", following.getUserId());
            existedRelationWrapper.eq("platform_id", following.getPlatformId()); // TODO 冗余筛选字段，有利于高效利用组合索引
            existedRelationWrapper.eq("type_id", following.getTypeId());
            existedRelationWrapper.eq("following_id", following.getFollowingId());
            UserFollowingRelation existedRelation = userFollowingRelationService.getOne(existedRelationWrapper);
            if (existedRelation != null && existedRelation.getId() != null) {
                following.setId(existedRelation.getId());
            }
        }

        // 保存用户与关注用户关系记录
        UserFollowingRelation relation = new UserFollowingRelation();
        relation.setId(following.getId()); // 注意，UserFollowingDto 中的 id 是用户关系表的 id
        relation.setUserId(following.getUserId());
        relation.setFollowingId(saveFollowing.getId()); // 新增或修改后的关注用户id
        relation.setPlatformId(following.getPlatformId());
        relation.setTypeId(following.getTypeId());
        relation.setSortNo(following.getSortNo());

        // 去字符串字段两边空格
        ObjectUtil.stringFiledTrim(relation);

        if (!userFollowingRelationService.saveOrUpdate(relation)) {
            log.error("保存用户与关注用户关系记录失败");
            throw new AtomicityException("保存用户与关注用户关系记录失败");
        }

        // 解析用户标签列表
        List<UserFollowingRemark> remarkList = null;
        String remarkListJson = following.getRemarkListJson();
        if (StringUtils.isNotBlank(remarkListJson)) {
            remarkList = JSONObject.parseArray(remarkListJson, UserFollowingRemark.class);
            // 保存用户标签
            if (remarkList != null && remarkList.size() > 0) {
                // TODO 校验用户对关注用户添加的标签数量不能超过 10 个，代码常量可配
                int remarkCount = userFollowingRemarkService.getRemarkCount(following.getUserId(), following.getFollowingId());
                if (remarkCount >= GlobalConstant.SAME_USER_FOLLOWING_REMARK_NUM) {
                    log.info("关注用户标签数量超过限制");
                    throw new AtomicityException("关注用户标签数量超过限制");
                }
                if (!userFollowingRemarkService.saveRemarks(remarkList, following)) {
                    log.error("保存用户标签失败");
                    throw new AtomicityException("保存用户标签失败");
                }
            }
        }

        // 如果是用户，执行一次信息同步
        if (following.getIsUser()) {
            /*
            // 通过 mq 异步请求同步
            userInfoSyncSender.sendMessage(following);
            */

            // TODO 这里需要实时同步，使用 feign 远程调用
            CommonResult<String> syncResult = userInfoExcavationService.syncFollowingInfo(following);
            if (syncResult.getCode() != CommonResult.success().getCode()) {
                log.error("同步用户信息失败，following id:" + following.getFollowingId());
                throw new AtomicityException("同步用户信息失败");
            }

        }

        // 重新加载封装对象
        UserFollowingItem followingItem = null;
        if (!isNew) { // 更新记录才重新加载，新增记录的话，首页会整体重新加载
            // 重新加载关注用户
            params.clear();
            params.put("userId", following.getUserId());
            params.put("platformId", following.getPlatformId()); // TODO 冗余筛选字段，有利于高效利用组合索引
            params.put("typeId", following.getTypeId());
            params.put("followingId", following.getFollowingId());
            UserFollowingDto reloadFollowing = userFollowingService.getUserFollowing(params);
            followingItem = new UserFollowingItem();
            followingItem.setUserFollowing(reloadFollowing);

            // 重新加载用户标签
            if (remarkList != null && remarkList.size() > 0) {
                Map<String, Object> queryParams = new HashMap<>();
                queryParams.put("user_id", following.getUserId());
                queryParams.put("following_id", saveFollowing.getId());
                List<UserFollowingRemark> followingRemarkList = userFollowingRemarkService.queryUserFollowingRemarkListRegularly(queryParams);
                followingItem.setUserFollowingRemarkList(followingRemarkList);
            }
        }

        // TODO 保存头像文件，放在代码最后，出异常则抛出自定义文件保存异常，数据也会回滚，
        //  避免头像上传成功，但业务代码出错，导致文件成为孤立的垃圾
        //  方案一：自定义文件保存异常，把保存文件方法放在数据操作之后，一旦文件保存出错，抛出异常，整个方法事务回滚，数据和文件都不会生效
        //  方案二：自定义文件保存异常，在异常业务处理代码中加上删除已上传的文件代码，方法出错抛出自定义异常，数据回滚，文件删除
        //  方案三：使用定时任务清理没有用户关联的头像文件
        if (!following.getIsUser()) { // 非用户
            if (profile != null) {
                // saveFile 文件保存出错会抛出自定义文件保存异常，整个方法的事务回滚
                //saveFile(profile, oldFilePath, following.getProfilePhoto());
                // 保存到 OSS
                saveFileToOSS(profile, oldFilePath, following.getProfilePhoto());
            }
        }

        return CommonResult.success(followingItem);
    }

    /**
     * 保存头像文件到OSS
     *
     * @param profile
     * @param oldFilePath
     * @param newFilePath
     */
    private void saveFileToOSS(MultipartFile profile, String oldFilePath, String newFilePath) {
        InputStream is = null;
        try {
            is = profile.getInputStream();
            boolean result = ossService.saveFile(ALIYUN_OSS_BUCKET_NAME, is, oldFilePath, newFilePath);
            if (!result) {
                // 抛出自定义文件保存异常，回滚前面的数据操作
                throw new FileSaveException("保存用户头像失败");
            }
        } catch (Exception e) {
            log.error("OSS 保存文件异常:{}", e.getMessage());
            // TODO 出现异常，删除上传文件，不管是否已经上传成功
            ossService.delete(ALIYUN_OSS_BUCKET_NAME, newFilePath);
            // 抛出自定义文件保存异常，回滚前面的数据操作
            throw new FileSaveException("保存用户头像失败");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("OSS 文件流关闭异常", e);
                    // 抛出自定义文件保存异常，回滚前面的数据操作
                    //throw new FileSaveException("保存用户头像失败");
                }
            }
        }
    }

    /**
     * 保存头像文件
     *
     * @param profile
     * @param oldFilePath
     * @param newFilePath
     */
    private void saveFile(MultipartFile profile, String oldFilePath, String newFilePath) {
        // 更新用户头像时，先删除旧头像
        if (StringUtils.isNotBlank(oldFilePath)) {
            oldFilePath = FileUtil.getFilePath(UserFollowingController.class, null, saveDir, oldFilePath, false, true);
            log.info(">>>> oldFilePath: {}", oldFilePath);
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && oldFile.isFile()) {
                if (oldFile.delete()) {
                    log.info(">>>> 用户旧头像已删除：{}", oldFilePath);
                } else {
                    log.error(">>>> 用户旧头像删除失败：{}", oldFilePath);
                    throw new FileSaveException("用户旧头像删除失败：" + oldFilePath);
                }
            }
        }

        // 上传新头像文件
        String fileFullPath = FileUtil.getFilePath(UserFollowingController.class, null, saveDir, newFilePath, false, true);
        log.info(">>>> saving fileFullPath: {}", fileFullPath);
        File dest = new File(fileFullPath);
        try {
            Files.copy(profile.getInputStream(), dest.toPath());
            //file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("保存用户头像失败：" + e.getMessage());
            throw new FileSaveException("保存用户头像失败");
        }
    }


    @ApiOperation("关注用户列表")
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<Page<UserFollowingItem>> list(@RequestParam Long userId, @RequestParam Long platformId, @RequestParam Long typeId,
                                                      @RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {

        if (userId == null || platformId == null || typeId == null) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("platformId", platformId);
        params.put("typeId", typeId);

        // 先查询是否有记录
        int count = userFollowingService.getFollowingCount(params);
        if (count < 1) {
            // TODO 返回成功信息，否则页面会弹出错误提示
            return CommonResult.success();
        }

        // 准备分页数据
        if (pageNo == null) {
            pageNo = GlobalConstant.PAGE_NO_DEFAULT;
        }
        if (pageSize == null) {
            pageSize = GlobalConstant.PAGE_SIZE_DEFAULT;
        }
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);


        // 获取关注用户视图层对象数据列表
        Page<UserFollowingItem> followingItemPage = userFollowingService.getUserFollowingItemListPage(params);
        /*
        // 返回失败，会弹出提示，业务不需要提示
        if (followingItemPage == null || CollectionUtils.isEmpty(followingItemPage.getRecords())) {
            log.error("关注列表数据错误");
            return CommonResult.failed("关注列表加载失败");
        }
        */

        return CommonResult.success(followingItemPage);
    }


    @ApiOperation("同步关注用户的信息")
    //@RequestMapping(value = "syncOne/{followingId}") // @PathVariable String followingId
    @RequestMapping(value = "syncOne", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @Caching(evict = {
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).HOME_PLATFORM_LIST_CACHE_KEY + #userId"),
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_FOLLOWING_PAGE_LIST_CACHE_KEY + #userId + '_' + #platformId + '_' + #typeId")
    })
    public CommonResult<UserFollowingItem> syncFollowingInfo(@RequestParam Long userId, @RequestParam Long platformId, @RequestParam Long typeId, @RequestParam Long followingId) {

        if (userId == null || platformId == null || typeId == null || followingId == null) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询关注用户
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("platformId", platformId);
        params.put("typeId", typeId);
        params.put("followingId", followingId);
        UserFollowingDto following = userFollowingService.getUserFollowing(params);

        if (following == null) {
            log.error("关注用户不存在，following id:" + followingId);
            return CommonResult.failed("关注用户不存在");
        }

        // 通过 mq 异步请求同步
        userInfoSyncSender.sendMessage(following);

        // TODO 重新加载用户信息，注意，mq 消息队列任务因为是异步的，可能还没有执行，这里重新加载信息存在问题
        params.clear();
        params.put("userId", userId);
        params.put("platformId", platformId);
        params.put("typeId", typeId);
        params.put("followingId", followingId);
        UserFollowingDto newFollowing = userFollowingService.getUserFollowing(params);
        if (newFollowing == null) {
            log.error("重新加载用户信息失败，following id:" + followingId);
            return CommonResult.failed("加载用户信息失败");
        }

        // 封装页面显示对象
        UserFollowingItem userFollowingItem = new UserFollowingItem();
        userFollowingItem.setUserFollowing(newFollowing);
        //userFollowingItem.setUserFollowingRemarkList(null); // 没改动，不操作

        return CommonResult.success(userFollowingItem);
    }


    @ApiOperation("批量同步关注用户的信息")
    @RequestMapping(value = "syncBatch", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @Caching(evict = {
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).HOME_PLATFORM_LIST_CACHE_KEY + #userId"),
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_FOLLOWING_PAGE_LIST_CACHE_KEY + #userId + '_' + #platformId + '_' + #typeId")
    })
    public CommonResult<List<UserFollowingItem>> syncFollowingInfoBatch(@RequestParam Long userId, @RequestParam Long platformId, @RequestParam(required = false) Long typeId) {

        if (userId == null || platformId == null) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询用户信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("platformId", platformId);
        if (typeId != null) params.put("typeId", typeId);
        List<UserFollowingDto> userFollowingList = userFollowingService.queryUserFollowingList(params);

        if (userFollowingList == null || userFollowingList.size() < 1) {
            log.error("获取关注用户记录失败");
            return CommonResult.failed("获取关注用户记录失败");
        }

        List<UserFollowingItem> userFollowingItemList = new ArrayList<>();
        for (UserFollowingDto following : userFollowingList) {
            // 通过 mq 异步请求同步
            userInfoSyncSender.sendMessage(following);

            // TODO 重新加载用户信息，注意，mq 消息队列任务因为是异步的，可能还没有执行，这里重新加载信息存在问题
            params.clear(); // 每次循环都 clear，不太友好，但不容易出错，各种判断，反而容易出问题
            params.put("userId", userId);
            params.put("platformId", platformId);
            if (typeId != null) params.put("typeId", typeId);
            params.put("followingId", following.getFollowingId()); // following id 每次循环都是新的
            UserFollowingDto newFollowing = userFollowingService.getUserFollowing(params);
            if (newFollowing == null) {
                log.error("重新加载用户信息失败，following id:" + following.getFollowingId());
                return CommonResult.failed("加载用户信息失败");
            }

            // 封装页面显示对象
            UserFollowingItem userFollowingItem = new UserFollowingItem();
            userFollowingItem.setUserFollowing(newFollowing);
            //userFollowingItem.setUserFollowingRemarkList(null); // 标签没改动，不操作

            userFollowingItemList.add(userFollowingItem);

            // 避免频繁访问被封
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return CommonResult.success(userFollowingItemList);
    }


    /**
     * 移除关注用户，这个方法本来应该放 UserFollowingRelationController，放这方便找
     *
     * @param userId      用户id
     * @param platformId  平台id
     * @param typeId      类型id
     * @param followingId 关注用户id
     * @return 操作结果
     */
    @ApiOperation("移除关注用户")
    @RequestMapping(value = "removeRelation", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @Caching(evict = {
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).HOME_PLATFORM_LIST_CACHE_KEY + #userId"),
            @CacheEvict(value = GlobalConstant.CACHE_USER_FOLLOWING_2HOURS, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).USER_FOLLOWING_PAGE_LIST_CACHE_KEY + #userId + '_' + #platformId + '_' + #typeId")
    })
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long platformId, @RequestParam Long typeId, @RequestParam Long followingId) {

        if (userId == null || platformId == null || typeId == null || followingId == null) {
            log.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        /*
        // 手动实现逻辑删除
        UpdateWrapper<UserFollowingRelation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_deleted", 1);
        updateWrapper.eq("user_id", userId);
        updateWrapper.eq("following_id", followingId);
        if (!userFollowingRelationService.update(updateWrapper)) {
            log.error("移除失败，userId: {}, followingId: {}", userId, followingId);
            return CommonResult.failed("操作失败");
        }
        */

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("platform_id", platformId);
        params.put("type_id", typeId);
        params.put("following_id", followingId);
        // TODO 会自动替换为逻辑删除，即执行更新语句
        if (!userFollowingRelationService.removeByMap(params)) {
            log.error("移除失败，userId: {}, platformId: {}, typeId: {}, followingId: {}", userId, platformId, typeId, followingId);
            return CommonResult.failed("操作失败");
        }

        return CommonResult.success();
    }

}
