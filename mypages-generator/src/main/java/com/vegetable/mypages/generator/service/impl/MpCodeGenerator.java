package com.vegetable.mypages.generator.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.vegetable.mypages.generator.bo.MpConfigInfo;
import com.vegetable.mypages.generator.service.CodeGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis-Plus 代码生成器实现类
 */
@Service(value = "mpCodeGenerator")
public class MpCodeGenerator implements CodeGenerator<MpConfigInfo> {

    @Override
    public void run(MpConfigInfo mpConfigInfo) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 模板配置
        // 模板文件可以直接到 mybatis-plus-generator 源码里面复制到 resources 下的 templates 目录
        TemplateConfig tc = new TemplateConfig();
        // 配置自定义输出模板
        // 如果模板引擎是 freemarker
        //String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        String templatePathController = "/templates/controller.java";
        String templatePathService = "/templates/service.java";
        String templatePathServiceImpl = "/templates/serviceImpl.java";
        String templatePathEntity = "/templates/entity.java";
        String templatePathMapper = "/templates/mapper.java";
        String templatePathMapperXML = "/templates/mapper.xml";

        tc.setController(templatePathController);
        tc.setService(templatePathService);
        tc.setServiceImpl(templatePathServiceImpl);
        tc.setEntity(templatePathEntity);
        tc.setMapper(templatePathMapper);
        tc.setXml(templatePathMapperXML);

        // 设置模板引擎
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(mpConfigInfo.getGcOutputDir());
        gc.setAuthor(mpConfigInfo.getGcAuthor());
        gc.setOpen(mpConfigInfo.isGcOpen()); // 是否打开目录
        gc.setFileOverride(mpConfigInfo.isGcFileOverride()); // 重新生成文件时是否覆盖
        gc.setServiceName(mpConfigInfo.getGcServiceName()); // 去掉 I 前缀，默认生成的 service 会有 I 前缀
        gc.setSwagger2(mpConfigInfo.isGcSwagger2()); // 是否启用实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);


        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(mpConfigInfo.getDsUrl());
        //dsc.setSchemaName("public"); // 可以直接在 url 中指定数据库名
        dsc.setDriverName(mpConfigInfo.getDsDriverName()); // 新版本数据库驱动包
        dsc.setUsername(mpConfigInfo.getDsUsername());
        dsc.setPassword(mpConfigInfo.getDsPassword());
        dsc.setDbType(getDbTypeByName(mpConfigInfo.getDsDbName()));
        mpg.setDataSource(dsc);


        // 包配置
        PackageConfig pc = new PackageConfig();
        // 配置父包名（需要修改）
        pc.setParent(mpConfigInfo.getPcParent());
        // 配置模块名（需要修改）
        if (StringUtils.isNotEmpty(mpConfigInfo.getPcModuleName()))
            pc.setModuleName(mpConfigInfo.getPcModuleName());
        // 配置 entity 包名
        pc.setEntity(mpConfigInfo.getPcEntity());
        // 配置 mapper 包名
        pc.setMapper(mpConfigInfo.getPcMapper());
        // 配置 service 包名
        pc.setService(mpConfigInfo.getPcService());
        // 配置 controller 包名
        pc.setController(mpConfigInfo.getPcController());
        mpg.setPackageInfo(pc);


        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude(mpConfigInfo.getDbTables());
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(mpConfigInfo.isScEntityLombokModel());
        strategy.setRestControllerStyle(mpConfigInfo.isScRestControllerStyle());
        strategy.setControllerMappingHyphenStyle(mpConfigInfo.isScControllerMappingHyphenStyle()); // localhost:8080/hello_id_2
        //strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);


        // 自定义属性注入，不需要可以不定义
        InjectionConfig injectionConfig = new InjectionConfig() {
            // 自定义属性注入:abc
            // 在.ftl(或者是.vm)模板中，通过${cfg.abc}获取属性
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };
        // 配置自定义属性注入
        mpg.setCfg(injectionConfig);

        // 执行生成器
        mpg.execute();
    }

    private DbType getDbTypeByName(String dbName) {
        if (StringUtils.isNotEmpty(dbName)) {
            switch(dbName.trim().toLowerCase()) {
                case "oracle":
                    return DbType.ORACLE;
            }
        }
        return DbType.MYSQL;
    }

}
