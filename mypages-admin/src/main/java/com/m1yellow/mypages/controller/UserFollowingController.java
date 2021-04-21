package com.m1yellow.mypages.controller;


import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.util.UUIDGenerateUtil;
import com.m1yellow.mypages.entity.UserFollowing;
import com.m1yellow.mypages.entity.UserFollowingRemark;
import com.m1yellow.mypages.excavation.bo.UserInfoItem;
import com.m1yellow.mypages.service.UserFollowingRemarkService;
import com.m1yellow.mypages.service.UserFollowingService;
import com.m1yellow.mypages.vo.home.UserFollowingItem;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户关注表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@RestController
@RequestMapping("/user-following")
public class UserFollowingController {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowingController.class);

    @Value("${user.avatar.savedir}")
    private String saveDir;

    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserFollowingRemarkService userFollowingRemarkService;

    /**
     * @RequestPart与@RequestParam的区别
     * @RequestPart这个注解用在multipart/form-data表单提交请求的方法上。 支持的请求方法的方式MultipartFile，属于Spring的MultipartResolver类。这个请求是通过http协议传输的。
     * @RequestParam也同样支持multipart/form-data请求。 他们最大的不同是，当请求方法的请求参数类型不再是String类型的时候。
     * @RequestParam适用于name-valueString类型的请求域，@RequestPart适用于复杂的请求域（像JSON，XML）。 <b>添加关注用户</b>
     */

    @ApiOperation("添加关注用户")
    @RequestMapping(value = "add")
    public CommonResult<UserFollowingItem> addFollowing(UserFollowing following, @RequestPart(required = false) MultipartFile profile, @RequestParam(required = false) String[] remarks) {

        if (following == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 校验、初始数据
        if (StringUtils.isBlank(following.getName()) || StringUtils.isBlank(following.getMainPage())) {
            logger.error("请检查必须参数");
            return CommonResult.failed("请检查必须参数");
        }

        if (!following.getIsUser()) { // 非用户
            if (profile == null) {
                logger.error("非用户需要上传头像");
                return CommonResult.failed("非用户需要上传头像");
            } else {
                // 上传文件
                String fileName = profile.getOriginalFilename();
                // 获取后缀 .jpg
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                // 重新生成文件名
                fileName = UUIDGenerateUtil.getUUID40() + suffixName;
                String filePath = saveDir + fileName;
                File dest = new File(filePath);
                try {
                    Files.copy(profile.getInputStream(), dest.toPath());
                    //file.transferTo(new File(filePath));
                } catch (IOException e) {
                    logger.error("用户头像上传出错：" + e.getMessage());
                    return CommonResult.failed("用户头像上传失败");
                }
                // 处理头像本地路径
                String profileDir = filePath.substring(saveDir.lastIndexOf("/images"));
                following.setProfilePhoto(profileDir);
            }
        }

        // 保存关注用户
        if (!userFollowingService.saveOrUpdate(following)) {
            logger.error("添加关注用户失败");
            return CommonResult.failed("添加关注用户失败");
        }

        // 重新加载新增用户
        if (following.getId() == null || following.getId() <= 0) {
            logger.error("新增用户信息错误");
            return CommonResult.failed("新增用户信息错误");
        }

        // 保存用户标签
        if (remarks != null && remarks.length > 0) {
            // 先查询用户标签，判断标签是否存在
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("following_id", following.getId());
            List<UserFollowingRemark> followingRemarkList = userFollowingRemarkService.queryUserFollowingRemarkListRegularly(queryParams);
            List<String> remarkList = null;
            if (followingRemarkList != null && followingRemarkList.size() > 0) {
                remarkList = followingRemarkList.stream().map(UserFollowingRemark::getLabelName).collect(Collectors.toList());
            }

            for (String remark : remarks) {
                if (StringUtils.isBlank(remark)) {
                    continue;
                }
                if (remarkList != null && remarkList.size() > 0) {
                    // 判断标签是否已存在
                    if (remarkList.contains(remark)) continue;
                }
                UserFollowingRemark followingRemark = new UserFollowingRemark();
                followingRemark.setUserId(following.getUserId());
                followingRemark.setFollowingId(following.getId());
                followingRemark.setLabelName(remark);
                if (!userFollowingRemarkService.saveOrUpdate(followingRemark)) {
                    logger.error("用户标签保存失败");
                    return CommonResult.failed("用户标签保存失败");
                }
            }
        }

        // 如果是用户，执行一次信息同步
        if (following.getIsUser()) {
            // 获取用户信息
            UserInfoItem userInfoItem = userFollowingService.doExcavate(following);
            if (userInfoItem == null) {
                logger.error("用户信息获取失败，following id:" + following.getId());
                return CommonResult.failed("用户信息获取失败");
            }
            // 更新信息，保存入库
            if (!userFollowingService.saveUserInfo(userInfoItem, following)) {
                logger.error("用户信息同步保存失败");
                return CommonResult.failed("用户信息同步保存失败");
            }
        }

        // 重新加载关注用户
        following = userFollowingService.getById(following.getId());
        UserFollowingItem followingItem = new UserFollowingItem();
        followingItem.setUserFollowing(following);

        // 重新加载用户标签
        if (remarks != null && remarks.length > 0) {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("following_id", following.getId());
            List<UserFollowingRemark> followingRemarkList = userFollowingRemarkService.queryUserFollowingRemarkListRegularly(queryParams);
            followingItem.setUserFollowingRemarkList(followingRemarkList);
        }

        return CommonResult.success(followingItem);
    }

}
