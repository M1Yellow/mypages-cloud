package cn.m1yellow.mypages.common.domian;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * jwt payload
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class JwtPayloadDto {
    @ApiModelProperty("主题")
    private String sub;
    @ApiModelProperty("签发时间")
    private Long iat;
    @ApiModelProperty("过期时间")
    private Long exp;
    @ApiModelProperty("JWT的ID")
    private String jti;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("用户名称")
    private String username;
    @ApiModelProperty("访问客户端来源")
    private String clientId;
    @ApiModelProperty("用户拥有的权限")
    private List<String> authorities;
}
