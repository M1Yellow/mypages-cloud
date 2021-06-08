package cn.m1yellow.mypages.auth.component;

import cn.hutool.core.date.DateUtil;
import cn.m1yellow.mypages.auth.bo.SecurityUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT 增加自定义内容
 * <br>
 * TODO 注意！！！ spring security oauth2 JwtReactiveAuthenticationManager.authenticate 验证方法默认不包含增强属性字段，
 *  导致 jwt 验证总是失败，oauth2 网上资料不多，找遍了各种博客资料、视频教程，及 debug 源码，整整耗费了一天的时间，排查问题！
 *  时间、精力、头发都非常宝贵，框架虽然好用，但出了问题，要排查，那就是非常令人头秃的事情了！
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    /** 用户id */
    private static final String CLAIM_KEY_USER_ID = "sub";
    /** 用户名 */
    private static final String CLAIM_KEY_USERNAME = "username";
    /** 创建时间，单位：秒 */
    private static final String CLAIM_KEY_IAT = "iat";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Map<String, Object> info = new HashMap<>();
        info.put(CLAIM_KEY_USER_ID, securityUser.getUserId());
        info.put("userId", securityUser.getUserId()); // 冗余用户id
        info.put(CLAIM_KEY_USERNAME, securityUser.getUsername());
        info.put(CLAIM_KEY_IAT, DateUtil.currentSeconds());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
