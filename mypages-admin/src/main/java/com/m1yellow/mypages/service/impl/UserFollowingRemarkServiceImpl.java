package com.m1yellow.mypages.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m1yellow.mypages.bo.UserFollowingBo;
import com.m1yellow.mypages.entity.UserFollowingRemark;
import com.m1yellow.mypages.mapper.UserFollowingRemarkMapper;
import com.m1yellow.mypages.service.UserFollowingRemarkService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户关注备注（便签）表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@Service
public class UserFollowingRemarkServiceImpl extends ServiceImpl<UserFollowingRemarkMapper, UserFollowingRemark> implements UserFollowingRemarkService {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowingRemarkServiceImpl.class);

    @Override
    public List<UserFollowingRemark> queryUserFollowingRemarkListRegularly(Map<String, Object> params) {
        QueryWrapper<UserFollowingRemark> followingRemarkQueryWrapper = new QueryWrapper();
        followingRemarkQueryWrapper.eq("user_id", params.get("user_id"));
        followingRemarkQueryWrapper.eq("following_id", params.get("following_id"));
        //followingRemarkQueryWrapper.eq("is_deleted", 0); // 已经有逻辑删除功能
        followingRemarkQueryWrapper.orderByDesc("sort_no");
        return this.list(followingRemarkQueryWrapper);
    }

    @Override
    @Transactional // 多条修改 sql 需要开启事务
    public boolean saveRemarks(List<UserFollowingRemark> remarkList, UserFollowingBo following) {

        logger.info(">>>> saveRemarks, remarkList={}, following={}", remarkList, following);

        if (remarkList == null || remarkList.size() < 1) {
            logger.info(">>>> save remark: remarkList is invalid.");
            return false;
        }

        if (following == null || following.getUserId() == null || following.getFollowingId() == null) {
            logger.info(">>>> save remark: following is invalid.");
            return false;
        }

        // 先查询用户标签，判断标签是否存在
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("user_id", following.getUserId());
        queryParams.put("following_id", following.getFollowingId()); // saveFollowing.getId()
        List<UserFollowingRemark> existedRemarkList = queryUserFollowingRemarkListRegularly(queryParams);
        logger.info(">>>> save remark: existedRemarkList: {}", existedRemarkList);

        // 直接在循环外面创建实例，放在循环内 new 实例容易产生坑！会导致永远只有一个元素
        List<Long> savedRemarkIdList = new ArrayList<>();
        for (UserFollowingRemark remark : remarkList) {
            if (remark == null || StringUtils.isBlank(remark.getLabelName())) {
                logger.info(">>>> save remark: userId={}, followingId={}, labelName is blank.",
                        following.getUserId(), following.getFollowingId());
                continue;
            }

            if (remark.getUserId() == null) {
                remark.setUserId(following.getUserId());
            }
            if (remark.getFollowingId() == null) {
                remark.setFollowingId(following.getFollowingId());
            }

            if (existedRemarkList != null && existedRemarkList.size() > 0) {
                for (UserFollowingRemark existedRemark : existedRemarkList) {
                    // 判断标签是否已存在：标签存在，且添加的 remark id 为 null，则不重复添加
                    if (existedRemark.getLabelName().trim().equals(remark.getLabelName().trim()) && remark.getId() == null) {

                        // 已经存在的，也算本次成功保存，总不能把已存在的删掉吧
                        savedRemarkIdList.add(existedRemark.getId());

                        logger.info(">>>> save remark: userId={}, followingId={}, remark is already existed: {}",
                                following.getUserId(), following.getFollowingId(), remark.getLabelName());
                        continue;
                    }
                }
            }
            UserFollowingRemark followingRemark = new UserFollowingRemark();
            followingRemark.setId(remark.getId()); // 有id则更新，无则新增
            followingRemark.setUserId(following.getUserId());
            followingRemark.setFollowingId(following.getFollowingId()); // 是关注用户表的id，标签自然是跟关注的用户对应
            followingRemark.setLabelName(remark.getLabelName().trim());
            followingRemark.setSortNo(remark.getSortNo());
            followingRemark.setIsDeleted(false);

            saveOrUpdate(followingRemark);

            // 记录保存成功的id
            savedRemarkIdList.add(followingRemark.getId());

        }

        // id 去重
        savedRemarkIdList = savedRemarkIdList.stream().distinct().collect(Collectors.toList());
        logger.info(">>>> save remark: savedRemarkIdList: {}", savedRemarkIdList);

        // 删除本次保存id以外的记录
        if (savedRemarkIdList != null && savedRemarkIdList.size() > 0) {
            UpdateWrapper<UserFollowingRemark> updateWrapper = new UpdateWrapper();
            updateWrapper.set("is_deleted", 1);
            updateWrapper.eq("user_id", following.getUserId());
            updateWrapper.eq("following_id", following.getFollowingId());
            updateWrapper.notIn("id", savedRemarkIdList);

            update(updateWrapper);
        }

        return true;
    }
}
