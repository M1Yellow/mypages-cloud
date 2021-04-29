package com.m1yellow.mypages.bo;

import com.m1yellow.mypages.entity.UserFollowingRemark;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 关注用户信息封装对象
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "UserFollowingBo 对象", description = "关注用户信息封装对象")
public class UserFollowingBo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "这里的值为用户与关注用户关系表id")
    private Long id;

    @ApiModelProperty(value = "用户与关注用户关系表查询的用户id")
    private Long userId;

    @ApiModelProperty(value = "对应关注用户表的id")
    private Long followingId;

    @ApiModelProperty(value = "关联平台id")
    private Long platformId;

    @ApiModelProperty(value = "关联关注类型表id，1-默认分类")
    private Long typeId;

    @ApiModelProperty(value = "关联用户来源平台的id或标识")
    private String userKey;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "主页地址")
    private String mainPage;

    @ApiModelProperty(value = "形象照片（头像）")
    private String profilePhoto;

    @ApiModelProperty(value = "个性签名")
    private String signature;

    @ApiModelProperty(value = "关注用户的标签列表")
    private List<UserFollowingRemark> remarkList;

    @ApiModelProperty(value = "关注用户的标签列表json格式")
    private String remarkListJson;

    @ApiModelProperty(value = "是否为用户，1-是用户；0-不是，默认1")
    private Boolean isUser;

    @ApiModelProperty(value = "优先级由低到高：1-10，默认5")
    private Integer sortNo;

    @ApiModelProperty(value = "本条数据是否已删除，1-是；0-否，默认0")
    private Boolean isDeleted;

    @ApiModelProperty(value = "用户关系表的创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "用户关系表的更新时间")
    private LocalDateTime updateTime;


}
