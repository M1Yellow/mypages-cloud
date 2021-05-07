package cn.m1yellow.mypages.service.impl;

import cn.m1yellow.mypages.service.UserBaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.m1yellow.mypages.bo.UserBaseDetails;
import cn.m1yellow.mypages.entity.UserBase;
import cn.m1yellow.mypages.mapper.UserBaseMapper;
import cn.m1yellow.mypages.security.util.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@Service
public class UserBaseServiceImpl extends ServiceImpl<UserBaseMapper, UserBase> implements UserBaseService {

    private static final Logger logger = LoggerFactory.getLogger(UserBaseServiceImpl.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


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

    @Override
    public UserDetails loadUserByUsername(String userName) {
        if (StringUtils.isBlank(userName)) {
            return null;
        }
        return new UserBaseDetails(getByUserName(userName));
    }

    @Override
    public String login(String userName, String password) {
        String token = null;
        try {
            UserDetails userDetails = loadUserByUsername(userName);
            // 这里的 password 是客户端加密后的
            //if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            if (!password.equals(userDetails.getPassword())) {
                throw new BadCredentialsException("用户名或密码错误");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);

        } catch (AuthenticationException e) {
            logger.warn("登录异常: {}", e.getMessage());
        }
        return token;
    }
}
