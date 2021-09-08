package cn.m1yellow.mypages.excavation.service.impl;


import cn.m1yellow.mypages.common.util.FileUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.common.util.UUIDGenerateUtil;
import cn.m1yellow.mypages.excavation.bo.UserInfoItem;
import cn.m1yellow.mypages.excavation.constant.PlatformInfo;
import cn.m1yellow.mypages.excavation.dto.UserFollowingDto;
import cn.m1yellow.mypages.excavation.entity.UserFollowing;
import cn.m1yellow.mypages.excavation.mapper.UserFollowingMapper;
import cn.m1yellow.mypages.excavation.service.DataExcavateService;
import cn.m1yellow.mypages.excavation.service.UserFollowingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Service
public class UserFollowingServiceImpl extends ServiceImpl<UserFollowingMapper, UserFollowing> implements UserFollowingService {

    @Value("${user.avatar.savedir}")
    private String saveDir;
    @Autowired
    private RedisUtil redisUtil;


    /**
     * @Resource、@Autowired、@Qualifier的注解注入及区别 在Java代码中可以使用 @Resource  或者 @Autowired 注解方式来进行注入。
     * 虽然 @Resource 和 @Autowried 都可以完成依赖注入，但是他们是有区别的。
     * @Resource 默认是按照名称来装配注入的，只有当找不到与名称匹配的bean才会按照类型来注入。
     * 它有两个属性是比较重要的:
     * ①. name: Spring 将 name 的属性值解析为 bean 的名称， 使用 byName 的自动注入策略
     * ②. type: Spring 将 type的属性值解析为 bean 的类型，使用 byType 的自动注入策略
     * 注: 如果既不指定 name 属性又不指定 type 属性，Spring这时通过反射机制使用 byName 自动注入策略
     * @Resource 的装配顺序
     * 1. 如果同时指定了 name 属性和 type 属性，那么 Spring 将从容器中找唯一匹配的 bean 进行装配，找不到则抛出异常
     * 2. 如果指定了 name 属性值，则从容器中查找名称匹配的 bean 进行装配，找不到则抛出异常
     * 3. 如果指定了 type 属性值，则从容器中查找类型匹配的唯一的 bean 进行装配，找不到或者找到多个都会抛出异常
     * 4. 如果都不指定，则会自动按照 byName 方式进行装配， 如果没有匹配，则回退一个原始类型进行匹配，如果匹配则自动装配
     * @Autowried 默认是按照类型进行装配注入，默认情况下，它要求依赖对象必须存在，如果允许 null 值，可以设置它 required 为false。
     * 如果我们想要按名称进行装配的话，可以添加一个 @Qualifier 注解解决。
     */

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
            log.error("用户主页不符合要求，following id:" + following.getFollowingId());
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

        // 去字符串字段两边空格
        ObjectUtil.stringFiledTrim(following);

        isSuc = saveOrUpdate(following);

        return isSuc;
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
