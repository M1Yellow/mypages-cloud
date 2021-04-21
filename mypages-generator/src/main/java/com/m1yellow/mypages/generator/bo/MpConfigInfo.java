package com.m1yellow.mypages.generator.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MyBatis-Plus 代码生成器的参数配置信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "MpConfigInfo对象", description = "代码生成器的参数配置信息")
public class MpConfigInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据库表")
    private String[] dbTables;

    // GlobalConfig 全局配置
    @ApiModelProperty(value = "代码生成主目录")
    private String gcOutputDir;
    @ApiModelProperty(value = "代码作者")
    private String gcAuthor;
    @ApiModelProperty(value = "是否打开目录")
    private boolean gcOpen;
    @ApiModelProperty(value = "重新生成文件时是否覆盖")
    private boolean gcFileOverride;
    @ApiModelProperty(value = "去掉 I 前缀，默认生成的 service 会有 I 前缀")
    private String gcServiceName;
    @ApiModelProperty(value = "是否启用实体属性 Swagger2 注解")
    private boolean gcSwagger2;

    // DataSourceConfig 数据源配置
    @ApiModelProperty(value = "数据库连接地址")
    private String dsUrl;
    @ApiModelProperty(value = "数据库驱动包")
    private String dsDriverName;
    @ApiModelProperty(value = "数据库连接用户名")
    private String dsUsername;
    @ApiModelProperty(value = "数据库连接密码")
    private String dsPassword;
    @ApiModelProperty(value = "数据库类型名称")
    private String dsDbName;

    // PackageConfig 包配置
    @ApiModelProperty(value = "父包名")
    private String pcParent;
    @ApiModelProperty(value = "模块名")
    private String pcModuleName;
    @ApiModelProperty(value = "entity 包名")
    private String pcEntity;
    @ApiModelProperty(value = "mapper 包名")
    private String pcMapper;
    @ApiModelProperty(value = "service 包名")
    private String pcService;
    @ApiModelProperty(value = "controller 包名")
    private String pcController;

    // StrategyConfig 策略配置
    @ApiModelProperty(value = "要操作的数据库表")
    private String[] scInclude;
    @ApiModelProperty(value = "是否启用 lombok")
    private boolean scEntityLombokModel;
    @ApiModelProperty(value = "是否启用 restful 风格")
    private boolean scRestControllerStyle;
    @ApiModelProperty(value = "是否启用 url 地址驼峰分隔")
    private boolean scControllerMappingHyphenStyle;

}
