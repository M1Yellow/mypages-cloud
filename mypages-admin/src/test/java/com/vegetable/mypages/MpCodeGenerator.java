package com.vegetable.mypages;

import com.vegetable.mypages.generator.bo.MpConfigInfo;
import com.vegetable.mypages.generator.service.CodeGenerator;

/**
 * MyBatis-Plus 代码生成器
 */
public class MpCodeGenerator {

    /** 作者 */
    private static final String AUTHOR = "Ming1";

    /** 数据库类型，mysql、oracle */
    private static final String DB_NAME = "mysql";

    /** 数据库表 */
    private static final String[] TABLES = {
            "check_following_update",
            "platform",
            "user_base",
            "user_following",
            "user_following_remark",
            "user_following_type",
            "user_opinion"
    };

    // 数据库连接信息
    private static final String URL = "jdbc:mysql://192.168.3.151:3306/mypage?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8";
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456.a";

    /** 模块名称 */
    private static final String MODULE_NAME = "mypages-admin";


    public static void main(String[] args) {
        // 配置信息封装
        MpConfigInfo mpConfigInfo = new MpConfigInfo();

        // GlobalConfig 全局配置
        mpConfigInfo.setGcOutputDir(System.getProperty("user.dir") + "/" + MODULE_NAME + "/src/main/java"); // System.getProperty("user.dir") 为当前项目根目录
        mpConfigInfo.setGcAuthor(AUTHOR);
        mpConfigInfo.setGcOpen(false);
        mpConfigInfo.setGcFileOverride(true);
        mpConfigInfo.setGcServiceName("%sService");
        mpConfigInfo.setGcSwagger2(true);

        // DataSourceConfig 数据源配置
        mpConfigInfo.setDbTables(TABLES);
        mpConfigInfo.setDsUrl(URL);
        mpConfigInfo.setDsDriverName(DRIVER_NAME);
        mpConfigInfo.setDsUsername(USERNAME);
        mpConfigInfo.setDsPassword(PASSWORD);
        mpConfigInfo.setDsDbName(DB_NAME);

        // PackageConfig 包配置
        mpConfigInfo.setPcParent("com.vegetable.mypages");
        mpConfigInfo.setPcModuleName(null);
        mpConfigInfo.setPcEntity("entity");
        mpConfigInfo.setPcMapper("mapper");
        mpConfigInfo.setPcService("service");
        mpConfigInfo.setPcController("controller");

        // StrategyConfig 策略配置
        //mpConfigInfo.setScInclude(TABLES);
        mpConfigInfo.setScEntityLombokModel(true);
        mpConfigInfo.setScRestControllerStyle(true);
        mpConfigInfo.setScControllerMappingHyphenStyle(true);

        // 执行生成器
        CodeGenerator mpCodeGenerator = new com.vegetable.mypages.generator.service.impl.MpCodeGenerator();
        mpCodeGenerator.run(mpConfigInfo);
    }
}
