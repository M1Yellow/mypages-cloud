package com.m1yellow.mypages.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户平台封装对象
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "UserPlatformBo 对象", description = "用户平台封装对象")
public class UserPlatformBo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户平台关系表id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "关联平台表id")
    private Long platformId;

    @ApiModelProperty(value = "平台名称")
    private String name;

    @ApiModelProperty(value = "平台英文名称")
    private String nameEn;

    @ApiModelProperty(value = "平台主页")
    private String mainPage;

    @ApiModelProperty(value = "平台logo")
    private String platformLogo;

    @ApiModelProperty(value = "平台长logo")
    private String platformLongLogo;

    @ApiModelProperty(value = "优先级由低到高：1-10，默认5。取用户平台关系表的字段")
    private Integer sortNo;

    @ApiModelProperty(value = "本条数据是否已删除，1-是；0-否，默认0。取用户平台关系表的字段")
    @TableLogic
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间。取用户平台关系表的字段")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间。取用户平台关系表的字段")
    private LocalDateTime updateTime;


}
