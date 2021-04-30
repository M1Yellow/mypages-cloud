package com.m1yellow.mypages.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.m1yellow.mypages.bo.UserFollowingBo;
import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.aspect.DoCache;
import com.m1yellow.mypages.common.aspect.WebLog;
import com.m1yellow.mypages.common.exception.AtomicityException;
import com.m1yellow.mypages.common.exception.FileSaveException;
import com.m1yellow.mypages.common.util.FileUtil;
import com.m1yellow.mypages.common.util.RedisUtil;
import com.m1yellow.mypages.entity.UserFollowing;
import com.m1yellow.mypages.entity.UserFollowingRelation;
import com.m1yellow.mypages.entity.UserFollowingRemark;
import com.m1yellow.mypages.excavation.bo.UserInfoItem;
import com.m1yellow.mypages.service.UserFollowingRelationService;
import com.m1yellow.mypages.service.UserFollowingRemarkService;
import com.m1yellow.mypages.service.UserFollowingService;
import com.m1yellow.mypages.vo.home.UserFollowingItem;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
@RestController
@RequestMapping("/following")
public class UserFollowingController {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowingController.class);

    @Value("${user.avatar.savedir}")
    private String saveDir;

    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserFollowingRelationService userFollowingRelationService;
    @Autowired
    private UserFollowingRemarkService userFollowingRemarkService;
    @Autowired
    private RedisUtil redisUtil;


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
    @DoCache
    public CommonResult<UserFollowingItem> add(UserFollowingBo following, @RequestPart(required = false) MultipartFile profile) {

        logger.info(">>>> following/add, following: {}", following);

        if (following == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 校验、初始数据
        if (StringUtils.isBlank(following.getName()) || StringUtils.isBlank(following.getMainPage())) {
            logger.error("请检查必须参数");
            return CommonResult.failed("请检查必须参数");
        }
        if (!following.getIsUser() && following.getFollowingId() == null) { // 新增的非用户头像不能为空
            if (profile == null) {
                logger.error("新增非用户需要上传头像");
                return CommonResult.failed("新增非用户需要上传头像");
            }
        }

        // 根据关注用户id是否为空，判断是新增还是更新
        Map<String, Object> params = new HashMap<>();
        params.put("userId", following.getUserId());
        params.put("followingId", following.getFollowingId());

        UserFollowingBo originalFollowing = null;
        if (following.getFollowingId() == null) { // 新增
            originalFollowing = new UserFollowingBo();
        } else { // 更新
            // 查询数据库中的记录
            originalFollowing = userFollowingService.getUserFollowing(params);
        }
        // 旧头像路径，用于删除旧头像
        String oldFilePath = originalFollowing.getProfilePhoto();

        if (profile != null) {
            // 获取新头像相对路径
            String newFilePath = FileUtil.getFilePath(UserFollowingController.class, profile, saveDir, oldFilePath, true, false);
            logger.info(">>>> init newFilePath: {}", newFilePath);
            // 设置新头像路径
            following.setProfilePhoto(newFilePath);
        }

        // 获取关注用户所在平台的id或标识
        String userKey = userFollowingService.getUserKeyFromMainPage(following);
        if (StringUtils.isBlank(userKey)) {
            logger.error("获取关注用户所在平台标识失败");
            throw new AtomicityException("获取关注用户所在平台标识失败");
        }
        following.setUserKey(userKey);

        // 保存关注用户
        UserFollowing saveFollowing = new UserFollowing();

        // 设置修改内容
        /*
        // beanCopier.copy 要求类型一致
        BeanCopier beanCopier = BeanCopier.create(UserFollowingBo.class, UserFollowing.class, true);
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
            // 确认同平台、同用户（id）标识是否已经有关注的用户（别人的关注也可以），如果有，直接用这个用户id
            if (saveFollowing.getPlatformId() != null && StringUtils.isNotBlank(saveFollowing.getUserKey())) {
                QueryWrapper<UserFollowing> existedFollowingWrapper = new QueryWrapper<>();
                existedFollowingWrapper.eq("platform_id", saveFollowing.getPlatformId());
                existedFollowingWrapper.eq("user_key", saveFollowing.getUserKey());
                UserFollowing existedFollowing = userFollowingService.getOne(existedFollowingWrapper);
                if (existedFollowing != null && existedFollowing.getId() != null) {
                    saveFollowing.setId(existedFollowing.getId());
                }
            }
        }

        // 保存入库
        if (!userFollowingService.saveOrUpdate(saveFollowing)) {
            logger.error("添加关注用户失败");
            throw new AtomicityException("添加关注用户失败");
        }

        // 校验保存后的新增用户
        if (saveFollowing.getId() == null || saveFollowing.getId() <= 0) {
            logger.error("新增用户信息错误");
            throw new AtomicityException("新增用户信息错误");
        }

        // 设置保存后的 following id
        following.setFollowingId(saveFollowing.getId());

        // 新增保存用户与关注用户关系之前，先根据 userId 和 followingId 判断是否已经存在记录
        // 为什么不跟上面的检查关注用户是否存在一起校验？因为已存在的关注用户可以时其他用户关注的啊，别人的关注
        if (following.getId() == null) {
            QueryWrapper<UserFollowingRelation> existedRelationWrapper = new QueryWrapper<>();
            existedRelationWrapper.eq("user_id", following.getUserId());
            existedRelationWrapper.eq("following_id", following.getFollowingId());
            UserFollowingRelation existedRelation = userFollowingRelationService.getOne(existedRelationWrapper);
            if (existedRelation != null && existedRelation.getId() != null) {
                following.setId(existedRelation.getId());
            }
        }

        // 保存用户与关注用户关系记录
        UserFollowingRelation relation = new UserFollowingRelation();
        relation.setId(following.getId()); // 注意，UserFollowingBo 中的 id 是用户关系表的 id
        relation.setUserId(following.getUserId());
        relation.setFollowingId(saveFollowing.getId()); // 新增或修改后的关注用户id
        relation.setSortNo(following.getSortNo());
        if (!userFollowingRelationService.saveOrUpdate(relation)) {
            logger.error("保存用户与关注用户关系记录失败");
            throw new AtomicityException("保存用户与关注用户关系记录失败");
        }

        // 解析用户标签列表
        List<UserFollowingRemark> remarkList = null;
        String remarkListJson = following.getRemarkListJson();
        if (StringUtils.isNotBlank(remarkListJson)) {
            remarkList = JSONObject.parseArray(remarkListJson, UserFollowingRemark.class);
            // 保存用户标签
            if (remarkList != null && remarkList.size() > 0) {
                if (!userFollowingRemarkService.saveRemarks(remarkList, following)) {
                    logger.error("保存用户标签失败");
                    throw new AtomicityException("保存用户标签失败");
                }
            }
        }

        // 如果是用户，执行一次信息同步
        if (following.getIsUser()) {
            // 获取用户信息
            UserInfoItem userInfoItem = userFollowingService.doExcavate(following);
            if (userInfoItem == null) {
                logger.error("获取用户信息失败，following id:" + following.getFollowingId());
                throw new AtomicityException("获取用户信息失败");
            }
            // 更新信息，保存入库
            if (!userFollowingService.saveUserInfo(userInfoItem, saveFollowing)) {
                logger.error("保存用户信息失败");
                throw new AtomicityException("保存用户信息失败");
            }
        }

        // 重新加载关注用户
        params.clear();
        params.put("userId", following.getUserId());
        params.put("followingId", following.getFollowingId());
        UserFollowingBo reloadFollowing = userFollowingService.getUserFollowing(params);
        UserFollowingItem followingItem = new UserFollowingItem();
        followingItem.setUserFollowing(reloadFollowing);

        // 重新加载用户标签
        if (remarkList != null && remarkList.size() > 0) {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("user_id", following.getUserId());
            queryParams.put("following_id", saveFollowing.getId());
            List<UserFollowingRemark> followingRemarkList = userFollowingRemarkService.queryUserFollowingRemarkListRegularly(queryParams);
            followingItem.setUserFollowingRemarkList(followingRemarkList);
        }

        // TODO 注意，上面用户头像文件已经上传完成了，如果下面的代码出错异常（放任何地方，只要代码异常，数据库记录会回滚，但已上传的文件不能自动撤销），
        //  会导致头像文件成为孤立的垃圾文件，后续优化。
        //  方案一：自定义文件保存异常，把保存文件方法放在数据操作之后，一旦文件保存出错，抛出异常，整个方法事务回滚，数据和文件都不会生效
        //  方案二：使用定时任务清理没有用户关联的头像文件
        // 保存头像文件，放在代码最后，出异常则抛出自定义文件保存异常，数据也会回滚
        if (!following.getIsUser()) { // 非用户
            if (profile != null) {
                // saveFile 文件保存出错会抛出自定义文件保存异常，整个方法的事务回滚
                saveFile(profile, oldFilePath, following.getProfilePhoto());
            }
        }

        return CommonResult.success(followingItem);
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
            logger.info(">>>> oldFilePath: {}", oldFilePath);
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && oldFile.isFile()) {
                if (!oldFile.delete()) {
                    logger.error("删除用户旧头像失败");
                    throw new FileSaveException("删除用户旧头像失败");
                }
            }
        }

        // 上传新头像文件
        String fileFullPath = FileUtil.getFilePath(UserFollowingController.class, null, saveDir, newFilePath, false, true);
        logger.info(">>>> saving fileFullPath: {}", fileFullPath);
        File dest = new File(fileFullPath);
        try {
            Files.copy(profile.getInputStream(), dest.toPath());
            //file.transferTo(new File(filePath));
        } catch (IOException e) {
            logger.error("保存用户头像失败：" + e.getMessage());
            throw new FileSaveException("保存用户头像失败");
        }
    }


    @ApiOperation("同步关注用户的信息")
    //@RequestMapping(value = "syncOne/{fuid}") // @PathVariable String fuid
    @RequestMapping(value = "syncOne", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<UserInfoItem> syncFollowingInfo(@RequestParam Long userId, @RequestParam Long fuid) {

        if (userId == null || fuid == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询关注用户
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("followingId", fuid);
        UserFollowingBo following = userFollowingService.getUserFollowing(params);

        if (following == null) {
            logger.error("关注用户表不存在，following id:" + fuid);
            return CommonResult.failed("关注用户表不存在");
        }

        // 获取用户信息
        UserInfoItem userInfoItem = userFollowingService.doExcavate(following);
        if (userInfoItem == null) {
            logger.error("用户信息获取失败，following id:" + fuid);
            return CommonResult.failed("用户信息获取失败");
        }

        // 更新信息，保存入库。（注意，内容未改动，即影响行数为 0，返回的也是 false，实际上并不算失败）
        UserFollowing saveFollowing = new UserFollowing();
        BeanUtils.copyProperties(following, saveFollowing);
        // 修正id
        saveFollowing.setId(following.getFollowingId());
        if (!userFollowingService.saveUserInfo(userInfoItem, saveFollowing)) {
            logger.error("用户信息保存失败，following id:" + fuid);
            return CommonResult.failed("用户信息保存失败");
        }

        return CommonResult.success(userInfoItem);
    }


    @ApiOperation("批量同步关注用户的信息")
    @RequestMapping(value = "syncBatch", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<List<UserInfoItem>> syncFollowingInfoBatch(@RequestParam Long userId, @RequestParam Long platformId, @RequestParam(required = false) Long typeId) {

        if (userId == null || platformId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询用户信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("platformId", platformId);
        if (typeId != null) params.put("typeId", typeId);
        List<UserFollowingBo> userFollowingList = userFollowingService.queryUserFollowingList(params);

        if (userFollowingList == null || userFollowingList.size() < 1) {
            logger.error("获取关注用户记录失败");
            return CommonResult.failed("获取关注用户记录失败");
        }

        List<UserInfoItem> userInfoItemList = new ArrayList<>();
        for (UserFollowingBo following : userFollowingList) {
            // 获取用户信息
            UserInfoItem userInfoItem = userFollowingService.doExcavate(following);
            if (userInfoItem == null) {
                logger.error("用户信息获取失败，following id:" + following.getFollowingId());
                return CommonResult.failed("用户信息获取失败");
            }

            // 更新信息，保存入库
            UserFollowing saveFollowing = new UserFollowing();
            BeanUtils.copyProperties(following, saveFollowing);
            // 修正id
            saveFollowing.setId(following.getFollowingId());
            userFollowingService.saveUserInfo(userInfoItem, saveFollowing);

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


    @ApiOperation("移除关注用户")
    @RequestMapping(value = "removeRelation", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long followingId) {

        if (userId == null || followingId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        /*
        // 手动实现逻辑删除
        UpdateWrapper<UserFollowingRelation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_deleted", 1);
        updateWrapper.eq("user_id", userId);
        updateWrapper.eq("following_id", followingId);
        if (!userFollowingRelationService.update(updateWrapper)) {
            logger.error("移除失败，userId: {}, followingId: {}", userId, followingId);
            return CommonResult.failed("操作失败");
        }
        */

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("following_id", followingId);
        // TODO 会自动替换为逻辑删除，即执行更新语句
        if (!userFollowingRelationService.removeByMap(params)) {
            logger.error("移除失败，userId: {}, followingId: {}", userId, followingId);
            return CommonResult.failed("操作失败");
        }

        return CommonResult.success();
    }

}
