package com.m1yellow.mypages.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.m1yellow.mypages.bo.UserFollowingBo;
import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.exception.AtomicityException;
import com.m1yellow.mypages.common.exception.FileSaveException;
import com.m1yellow.mypages.common.util.UUIDGenerateUtil;
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


    /**
     * @RequestPart与@RequestParam的区别
     * @RequestPart这个注解用在multipart/form-data表单提交请求的方法上。 支持的请求方法的方式MultipartFile，属于Spring的MultipartResolver类。这个请求是通过http协议传输的。
     * @RequestParam也同样支持multipart/form-data请求。 他们最大的不同是，当请求方法的请求参数类型不再是String类型的时候。
     * @RequestParam适用于name-valueString类型的请求域，@RequestPart适用于复杂的请求域（像JSON，XML）。 <b>添加关注用户</b>
     */

    @ApiOperation("添加/更新关注用户")
    @Transactional //(rollbackFor = {AtomicityException.class, FileSaveException.class})
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public CommonResult<UserFollowingItem> add(@RequestPart UserFollowingBo following, @RequestPart(required = false) MultipartFile profile) {

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
        if (!following.getIsUser()) { // 非用户头像不能为空
            if (profile == null) {
                logger.error("非用户需要上传头像");
                return CommonResult.failed("非用户需要上传头像");
            }
        }

        // 根据关注用户id是否为空，判断是新增还是更新
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", following.getUserId());
        params.put("following_id", following.getFollowingId());

        UserFollowingBo originalFollowing = null;
        if (following.getFollowingId() == null) { // 新增
            originalFollowing = new UserFollowingBo();
        } else { // 更新
            // 查询数据库中的记录
            originalFollowing = userFollowingService.getUserFollowing(params);
        }
        // 旧头像路径，用于删除旧头像
        String oldFilePath = originalFollowing.getProfilePhoto();

        // TODO 设置保存路径为 target 下的 classpath 目录，但本地项目执行 mvn clean 之后，target 会被清理掉
        saveDir = UserFollowingController.class.getResource("/").getPath() + saveDir;
        // 获取新头像路径
        String newFilePath = getFilePath(profile);
        // 设置新头像路径
        following.setProfilePhoto(newFilePath);


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
            // 确认同平台、同用户标识是否已经有关注的用户，如果有，直接用这个用户id
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

        // 保存用户标签
        List<UserFollowingRemark> remarkList = following.getRemarkList();
        if (remarkList != null && remarkList.size() > 0) {
            if (!userFollowingRemarkService.saveRemarks(remarkList, following)) {
                logger.error("保存用户标签失败");
                throw new AtomicityException("保存用户标签失败");
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
        UserFollowingBo reloadFollowing = userFollowingService.getUserFollowing(params); // 注意 params 中的条件，不能被 clear
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
        //  方案一：利用自定义全局异常捕获，加 redis 缓存，出现异常，则从缓存中取出对应用户上传的文件路径，删除
        //  方案二：使用定时任务清理没有用户关联的头像文件
        // 保存头像文件，放在代码最后，出异常则抛出自定义文件保存异常，数据也会回滚
        if (!following.getIsUser()) { // 非用户
            if (profile != null) {
                saveFile(profile, oldFilePath);
            }
        }

        return CommonResult.success(followingItem);
    }


    /**
     * 获取文件路径
     *
     * @param profile
     * @return
     */
    private String getFilePath(MultipartFile profile) {
        if (profile == null) {
            return null;
        }
        // 上传新头像文件
        String fileName = profile.getOriginalFilename();
        // 获取后缀 .jpg
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 重新生成文件名
        fileName = UUIDGenerateUtil.getUUID40() + suffixName;
        String filePath = saveDir + fileName;

        // 处理头像本地路径
        String profileDir = filePath.substring(filePath.lastIndexOf("/images"));

        return profileDir;
    }


    /**
     * 保存头像文件
     *
     * @param profile
     * @param oldFilePath
     */
    private void saveFile(MultipartFile profile, String oldFilePath) {
        // 更新用户头像时，先删除旧头像
        if (StringUtils.isNotBlank(oldFilePath)) {
            String oldProfileName = oldFilePath.substring(oldFilePath.lastIndexOf("/") + 1);
            oldFilePath = saveDir + oldProfileName;
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && oldFile.isFile()) {
                if (!oldFile.delete()) {
                    logger.error("删除用户旧头像失败");
                    throw new FileSaveException("删除用户旧头像失败");
                }
            }
        }

        // 上传新头像文件
        String filePath = getFilePath(profile);
        File dest = new File(filePath);
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
    public CommonResult<List<UserInfoItem>> syncFollowingInfoBatch(@RequestParam Long userId, @RequestParam Long platformId, @RequestParam(required = false) Long typeId) {

        if (platformId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询用户信息
        QueryWrapper<UserFollowing> followingQueryWrapper = new QueryWrapper();
        //followingQueryWrapper.eq("is_deleted", 0);
        followingQueryWrapper.eq("platform_id", platformId);
        followingQueryWrapper.eq("is_user", 1);
        if (typeId != null) followingQueryWrapper.eq("ftype_Id", typeId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("platformId", platformId);
        if (typeId != null) params.put("typeId", typeId);
        List<UserFollowingBo> userFollowingList = userFollowingService.queryUserFollowingList(params);

        if (userFollowingList == null || userFollowingList.size() <= 0) {
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
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long followingId) {

        if (userId == null || followingId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        UpdateWrapper<UserFollowingRelation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_deleted", 1);
        updateWrapper.eq("user_id", userId);
        updateWrapper.eq("following_id", followingId);

        if (!userFollowingRelationService.update(updateWrapper)) {
            logger.error("移除失败，following id:" + followingId);
            return CommonResult.failed("移除失败");
        }

        return CommonResult.success("操作成功");
    }


}
