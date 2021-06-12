package cn.m1yellow.mypages.auth.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.auth.bo.SecurityUser;
import cn.m1yellow.mypages.auth.config.JwtSecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenUtil implements InitializingBean {

    private static final String CLAIM_KEY_USER_ID = "sub";
    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_IAT = "iat";


    @Autowired
    public JwtSecurityProperties jwtSecurityProperties;


    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO 可以在这初始化变量

    }


    /**
     * 生成 token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                //.setId(UUIDGenerateUtil.getUUID32())
                .setExpiration(generateExpirationDate())
                //.compressWith(CompressionCodecs.DEFLATE) // 压缩内容，在传输内容较多时有用，一般情况不需要
                .signWith(SignatureAlgorithm.HS512, jwtSecurityProperties.getBase64Secret())
                .compact();
    }

    /**
     * 根据用户信息生成 token
     */
    public String generateToken(SecurityUser userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userDetails.getUserId());
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_IAT, DateUtil.currentSeconds());
        return generateToken(claims);
    }

    /**
     * 从 token 中获取 JWT 负载
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtSecurityProperties.getBase64Secret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.info("JWT格式验证失败: {}", token);
        }
        return claims;
    }

    /**
     * 生成 token 的过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + jwtSecurityProperties.getExpiration() * 1000);
    }

    /**
     * 从 token 中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = ObjectUtil.getString(claims.get(CLAIM_KEY_USERNAME));
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从 token 中获取登录用户id
     */
    public Long getUserIdFromToken(String token) {
        Long userId;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    /**
     * 验证用户的 token 是否有效
     *
     * @param token       客户端传入的token
     * @param userDetails 从数据库中查询出来的用户信息
     */
    public boolean validateUserToken(String token, SecurityUser userDetails) {
        Long userId = getUserIdFromToken(token);
        String username = getUserNameFromToken(token);
        if (userId == null || userId < 1 || StringUtils.isBlank(username)) {
            return false;
        }
        return userId.equals(userDetails.getUserId()) && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断 token 是否已经失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从 token 中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 当原来的 token 没过期时是可以刷新的
     *
     * @param oldToken 带tokenHead的token
     */
    public String refreshHeadToken(String oldToken) {
        if (StrUtil.isEmpty(oldToken)) {
            return null;
        }
        String token = oldToken.substring(jwtSecurityProperties.getTokenStart().length());
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        // token 校验不通过
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        // 如果 token 已经过期，不支持刷新
        if (isTokenExpired(token)) {
            return null;
        }
        // 如果 token 在 30 分钟之内刚刷新过，返回原 token
        if (tokenRefreshJustBefore(token, 30 * 60)) {
            return token;
        } else {
            claims.put(CLAIM_KEY_IAT, new Date());
            return generateToken(claims);
        }
    }

    /**
     * 判断 token 在指定时间内是否刚刚刷新过
     *
     * @param token 原 token
     * @param time  指定时间（秒）
     */
    private boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = getClaimsFromToken(token);
        Date created = claims.get(CLAIM_KEY_IAT, Date.class);
        Date refreshDate = new Date();
        // 刷新时间在创建时间的指定时间内
        if (refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time))) {
            return true;
        }
        return false;
    }

}
