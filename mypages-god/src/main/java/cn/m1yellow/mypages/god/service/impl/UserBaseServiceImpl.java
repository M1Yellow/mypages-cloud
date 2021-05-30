package cn.m1yellow.mypages.god.service.impl;


import cn.m1yellow.mypages.god.entity.UserBase;
import cn.m1yellow.mypages.god.mapper.UserBaseMapper;
import cn.m1yellow.mypages.god.service.UserBaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@DubboService(interfaceClass = UserBaseService.class, version = "1.0.0")
//@Service("userBaseService")
public class UserBaseServiceImpl extends ServiceImpl<UserBaseMapper, UserBase> implements UserBaseService {

    private static final Logger logger = LoggerFactory.getLogger(UserBaseServiceImpl.class);

    @Override
    public UserBase getByUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return null;
        }
        QueryWrapper<UserBase> userBaseQueryWrapper = new QueryWrapper<>();
        userBaseQueryWrapper.eq("user_name", userName);
        UserBase userBase = getOne(userBaseQueryWrapper);
        return userBase;
    }
}
