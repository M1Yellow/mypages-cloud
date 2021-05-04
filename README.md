# mypages

**我的主页我定义！**<br>
Define the homepage of your interest.




## 前言
这个项目只是个人兴趣驱动，用于运用及熟练各大主流技术框架。
目前整合的技术不多，业务功能也还未完全实现，但是会一直完善下去。

> 前、后端项目及说明文档都有参考借鉴 [@macrozheng](https://github.com/macrozheng) 的`mall`项目。他的项目我没有跑起来，学开源项目不一定非得运行起来，其中的项目结构及文档内容都还是很清晰完整的。<br>
> 开源项目非常多，自认为做得很好的项目，也许别人并不感兴趣。用开源技术，做好自己的本职工作及兴趣领域项目，就已经很不错了！


## 项目背景
憎恶了首页动态穿插垃圾广告？厌倦了大数据个性推荐？担心沉迷浪费宝贵时间？
试一下自定义社交主页，`简洁`、`清静`、`原味`，不错过有品质的用户内容，不被无关繁杂内容扰乱视野和思绪。




## 项目介绍
mypages 项目是一个致力于聚合多社交平台用户主页的小型应用。后端基于 SpringBoot + MyBatisPlus 实现，使用 Docker 部署项目及数据库。
主要业务功能包括，平台管理、平台观点管理、关注用户管理、用户类型管理、用户标签管理、检查用户内容更新、同步用户头像昵称等。




## 目录结构
```
mypages -- 项目目录
├── document -- 文档文件
│   └── sql -- 数据库脚本目录
│       └── mypages.sql -- 数据库初始脚本
├── mypages-admin -- 后端主项目模块
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   └── resources
│   │   └── test
│   │       └── java
│   ├── docker-compose.yml
│   ├── Dockerfile
│   └── pom.xml
├── mypages-common -- 公共模块
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   └── resources
│   │   └── test
│   │       └── java
│   ├── pom.xml
│   └── README.md
├── mypages-excavation -- 数据发掘模块
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   └── resources
│   │   └── test
│   │       └── java
│   ├── pom.xml
│   └── README.md
├── mypages-generator -- 代码生成模块
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   └── resources
│   │   └── test
│   │       └── java
│   ├── pom.xml
│   └── README.md
├── mypages-security -- 安全控制模块
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   └── resources
│   │   └── test
│   │       └── java
│   ├── pom.xml
│   └── README.md
├── LICENSE
├── pom.xml
└── README.md

```



## 项目演示

由于是前后端分离项目，目前后端项目只提供接口服务。

前端主页效果预览：[传送门](https://github.com/M1Yellow/mypages-web) 。




## 技术应用
### 后端项目
| 技术                 | 说明                | 官网                                           |
| -------------------- | ------------------- | ---------------------------------------------- |
| SpringBoot           | 容器+MVC框架        | https://spring.io/projects/spring-boot         |
| SpringSecurity       | 认证和授权框架      | https://spring.io/projects/spring-security     |
| MyBatis              | ORM框架             | http://www.mybatis.org/mybatis-3/zh/index.html |
| MyBatisPlus          | 数据层代码生成      | https://baomidou.com/                           |
| Nginx                | 静态资源服务器      | https://www.nginx.com/                         |
| Docker               | 应用容器引擎        | https://www.docker.com                         |
| Jenkins              | 自动化部署工具      | https://github.com/jenkinsci/jenkins           |
| Druid                | 数据库连接池        | https://github.com/alibaba/druid               |
| JWT                  | JWT登录支持         | https://github.com/jwtk/jjwt                   |
| Lombok               | 简化对象封装工具    | https://github.com/rzwitserloot/lombok         |
| Swagger-UI           | 文档生成工具        | https://github.com/swagger-api/swagger-ui      |



### 开发环境

| 工具          | 版本号 | 下载                                                         |
| ------------- | ------ | ------------------------------------------------------------ |
| JDK           | 1.8    | https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html |
| Maven         | 3.6.3  | https://maven.apache.org/                                       |
| Mysql         | 5.7    | https://www.mysql.com/                                       |
| Redis         | 5.0    | https://redis.io/download                                    |
| RabbitMQ      | 3.7.14 | http://www.rabbitmq.com/download.html                        |
| Nginx         | 1.19   | http://nginx.org/en/download.html                            |



### 开发工具

| 工具          | 说明                | 官网                                            |
| ------------- | ------------------- | ----------------------------------------------- |
| IDEA          | 开发IDE             | https://www.jetbrains.com/idea/download         |
| X-shell       | Linux远程连接工具   | http://www.netsarang.com/download/software.html |
| Navicat       | 数据库连接工具      | http://www.formysql.com/xiazai.html             |
| ProcessOn     | 流程图绘制工具      | https://www.processon.com/                      |
| Postman       | API接口调试工具      | https://www.postman.com/                        |
| Typora        | Markdown编辑器      | https://typora.io/                              |



## 部署运行

1. 下载并安装开发环境和工具
2. 克隆项目到本地，用 IDEA 打开项目
3. 配置 Maven 并下载项目依赖
4. 导入数据库脚本，初始化数据库
5. 修改`mypages-admin/src/main/resources/application.yml`配置文件，MySQL、Redis 数据源
6. 从启动类入口启动项目


> **初始数据说明**<br>
> 其中对各个平台的观点看法仅仅是个人观点，可能有些片面、过激，或是认知错误，不理会便是了，做好自己的事已不易。
