package com.m1yellow.mypages.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m1yellow.mypages.bo.UserFollowingBo;
import com.m1yellow.mypages.entity.UserFollowingRemark;
import com.m1yellow.mypages.mapper.UserFollowingRemarkMapper;
import com.m1yellow.mypages.service.UserFollowingRemarkService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    public boolean saveRemarks(List<UserFollowingRemark> remarkList, UserFollowingBo following) {

        if (remarkList == null || remarkList.size() < 1) {
            return false;
        }

        // 先查询用户标签，判断标签是否存在
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("user_id", following.getUserId());
        queryParams.put("following_id", following.getFollowingId()); // saveFollowing.getId()
        List<UserFollowingRemark> followingRemarkList = queryUserFollowingRemarkListRegularly(queryParams);
        List<String> existingRemarkList = null;
        if (followingRemarkList != null && followingRemarkList.size() > 0) {
            existingRemarkList = followingRemarkList.stream().map(UserFollowingRemark::getLabelName).collect(Collectors.toList());
        }

        for (UserFollowingRemark remark : remarkList) {
            if (remark == null || StringUtils.isBlank(remark.getLabelName())) {
                continue;
            }
            if (existingRemarkList != null && existingRemarkList.size() > 0) {
                // 判断标签是否已存在
                if (existingRemarkList.contains(remark.getLabelName().trim())) continue;
            }
            UserFollowingRemark followingRemark = new UserFollowingRemark();
            followingRemark.setId(remark.getId()); // 如果有id，则是更新记录
            followingRemark.setUserId(following.getUserId());
            followingRemark.setFollowingId(following.getFollowingId()); // 是关注用户表的id，标签自然是跟关注的用户对应
            followingRemark.setLabelName(remark.getLabelName().trim());
            followingRemark.setSortNo(remark.getSortNo());

            if (!saveOrUpdate(followingRemark)) {
                logger.error("用户标签保存失败");
                return false;
            }
        }

        return true;
    }
}
