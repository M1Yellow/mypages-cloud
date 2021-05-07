package cn.m1yellow.mypages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 // 开启 Swagger2
public class SwaggerConfig {

    // http://localhost:8081/swagger-ui.html

    public static final String SWAGGER_SCAN_BASE_PACKAGE = "cn.m1yellow.mypages.controller";
    public static final String VERSION = "0.0.1";

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("MyPages API")
                .description("Swagger MyPages API 接口信息。")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .version(VERSION)
                .contact(new Contact("","", "m1yellow@163.com"))
                .build();
    }

    @Bean
    public Docket docket(Environment environment){
        // 判断只在开放和测试环境开启
        Profiles profiles = Profiles.of("dev", "test");
        boolean enableSwagger = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enableSwagger) // 是否开启 Swagger
                .select()
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))
                .paths(PathSelectors.any()) // 正则匹配请求路径，并分配至当前分组，当前所有接口
                .build()
                .groupName("mypages") // 分组名称
                .globalOperationParameters(null);
    }

    // TODO 如需配置多个分组，复制 ApiInfo 及 Docket，修改配置即可

}
