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
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    /**
     * TODO
     * JWT 自带属性，表示所属者，类型必须是字符串类型，否则校验会失败。
     * JWT 转换 com.nimbusds.jwt.JWTClaimsSet#parse(java.util.Map)
     * 如果项目内 JWT 解析错误日志被 try-catch 吃掉了，那么问题排错就得 debug 一步一步调试跟踪了。
     * 这个问题卡了一天，先是网上各种找博客资料，然后各种参考开源项目，硬生生地看着别人地项目就没问题，自己地项目就是出错！
     * 原来是自己使用了内部自带字段，还特么有特定类型要求！时间、精力、头发都非常宝贵，框架虽然好用，但出了问题，要排查，那就是非常令人头秃的事情了！
     * 结论：组件自带字段有他特定的用处，没有业务明确需要，尽量使用新增自定义字段！
     */
    private static final String CLAIM_KEY_USER_ID = "userId";
    /** 用户名 */
    private static final String CLAIM_KEY_USERNAME = "username";
    /** 创建时间，单位：秒 */
    private static final String CLAIM_KEY_IAT = "iat";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Map<String, Object> info = new HashMap<>();
        info.put(CLAIM_KEY_USER_ID, securityUser.getUserId());
        info.put(CLAIM_KEY_USERNAME, securityUser.getUsername());
        info.put(CLAIM_KEY_IAT, DateUtil.currentSeconds());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
