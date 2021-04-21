package com.m1yellow.mypages.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.m1yellow.mypages.constant.PlatformInfo;
import com.m1yellow.mypages.entity.UserFollowing;
import com.m1yellow.mypages.excavation.bo.UserInfoItem;
import com.m1yellow.mypages.excavation.service.DataExcavateService;
import com.m1yellow.mypages.mapper.UserFollowingMapper;
import com.m1yellow.mypages.service.UserFollowingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户关注表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@Service
public class UserFollowingServiceImpl extends ServiceImpl<UserFollowingMapper, UserFollowing> implements UserFollowingService {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowingServiceImpl.class);

    @Value("${user.avatar.savedir}")
    private String saveDir;

    @Resource(name = "dataOfBiliExcavateService")
    private DataExcavateService dataOfBiliExcavateService;
    @Resource(name = "dataOfWeiboExcavateService")
    private DataExcavateService dataOfWeiboExcavateService;

    @Override
    public UserInfoItem doExcavate(UserFollowing following) {

        if (following == null) {
            return null;
        }



        // 参数封装
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(following.getProfilePhoto())) {
            params.put("profileOriginalDir", following.getProfilePhoto());
        }

        String apiUrl = null; // 请求地址模板
        String fromUrl = null; // 调整后的请求地址
        UserInfoItem userInfoItem = null;

        switch (PlatformInfo.getPlatformInfo(following.getPlatformId().intValue())) {
            case BILIBILI:
                apiUrl = "https://api.bilibili.com/x/space/acc/info?mid=userId&jsonp=jsonp";
                fromUrl = apiUrl.replace("userId", following.getMainPage().split("/")[3]);
                userInfoItem = dataOfBiliExcavateService.singleImageDownloadFromJson(fromUrl, saveDir, params);
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
                userInfoItem = dataOfWeiboExcavateService.singleImageDownloadFromJson(fromUrl, saveDir, params);
                break;
            case DOUBAN:

                break;
            case ZHIHU:

                break;
            default:

        }
        return userInfoItem;
    }

    @Override
    public boolean saveUserInfo(UserInfoItem userInfoItem, UserFollowing following) {
        boolean isSuc = false;

        following.setName(userInfoItem.getUserName());
        following.setSignature(userInfoItem.getSignature());
        following.setProfilePhoto(userInfoItem.getHeadImgPath());

        isSuc = saveOrUpdate(following);

        return isSuc;
    }
}
