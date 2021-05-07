package cn.m1yellow.mypages.service.impl;

import cn.m1yellow.mypages.constant.PlatformInfo;
import cn.m1yellow.mypages.dto.UserFollowingDto;
import cn.m1yellow.mypages.excavation.bo.UserInfoItem;
import cn.m1yellow.mypages.excavation.service.DataExcavateService;
import cn.m1yellow.mypages.service.UserFollowingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.m1yellow.mypages.common.util.FileUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.UUIDGenerateUtil;
import cn.m1yellow.mypages.entity.UserFollowing;
import cn.m1yellow.mypages.mapper.UserFollowingMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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

    @Resource
    private UserFollowingMapper userFollowingMapper;
    @Resource(name = "dataOfBiliExcavateService")
    private DataExcavateService dataOfBiliExcavateService;
    @Resource(name = "dataOfWeiboExcavateService")
    private DataExcavateService dataOfWeiboExcavateService;


    @Override
    public UserInfoItem doExcavate(UserFollowingDto following) {

        if (following == null || !following.getIsUser()) {
            return null;
        }

        // 获取、校验用户所属平台的id
        String userId = getUserKeyFromMainPage(following);
        if (StringUtils.isBlank(userId)) {
            logger.error("用户主页不符合要求，following id:" + following.getFollowingId());
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

        // 设置保存路径为 classpath 下的目录
        String saveDirFullPath = FileUtil.getSaveDirFullPath(UserFollowingServiceImpl.class, saveDir);

        switch (PlatformInfo.getPlatformInfoByUrl(following.getMainPage())) {
            case BILIBILI:
                apiUrl = "https://api.bilibili.com/x/space/acc/info?mid=userId&jsonp=jsonp";
                fromUrl = apiUrl.replace("userId", userId);
                userInfoItem = dataOfBiliExcavateService.singleImageDownloadFromJson(fromUrl, saveDirFullPath, params);
                break;
            case WEIBO:
                //apiUrl = "https://weibo.com/u/userId";
                //apiUrl = "https://m.weibo.cn/api/container/getIndex?type=uid&value=userId&containerid=1005056488142313";
                apiUrl = "https://m.weibo.cn/api/container/getIndex?type=uid&value=userId";
                fromUrl = apiUrl.replace("userId", userId);
                userInfoItem = dataOfWeiboExcavateService.singleImageDownloadFromJson(fromUrl, saveDirFullPath, params);
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

        following.setName(ObjectUtil.getString(userInfoItem.getUserName()));
        following.setSignature(ObjectUtil.getString(userInfoItem.getSignature()));
        following.setProfilePhoto(ObjectUtil.getString(userInfoItem.getHeadImgPath()));

        isSuc = saveOrUpdate(following);

        return isSuc;
    }

    @Override
    public boolean deleteById(UserFollowing following) {
        return updateById(following);
    }

    @Override
    public UserFollowingDto getUserFollowing(Map params) {
        return userFollowingMapper.getUserFollowing(params);
    }

    @Override
    public List<UserFollowingDto> queryUserFollowingList(Map params) {
        return userFollowingMapper.queryUserFollowingList(params);
    }

    @Override
    public List<Long> queryTypeIdList(Map params) {
        return userFollowingMapper.queryTypeIdList(params);
    }

    @Override
    public String getUserKeyFromMainPage(UserFollowingDto following) {
        String userKey = null;
        if (following != null && StringUtils.isNotBlank(following.getMainPage())) {
            String[] mainPageArr = following.getMainPage().split("/");
            if (mainPageArr.length < 2) {
                return null;
            }
            if (following.getIsUser()) {
                switch (PlatformInfo.getPlatformInfoByUrl(following.getMainPage())) {
                    case BILIBILI:
                        userKey = mainPageArr[mainPageArr.length - 2];
                        break;
                    case WEIBO:
                        userKey = mainPageArr[mainPageArr.length - 1];
                        break;
                    case DOUBAN:

                        break;
                    case ZHIHU:

                        break;
                    default:

                }
            } else { // 非用户
                userKey = UUIDGenerateUtil.getUUID32();
            }
        }
        return userKey;
    }
}
