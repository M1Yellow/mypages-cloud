package cn.m1yellow.mypages.god;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.m1yellow.mypages"}) // 放宽扫描路径，确保依赖的模块能扫到
//@EnableDubbo // 注解方式使用 dubbo
public class MypagesGodApplication {

    public static void main(String[] args) {
        SpringApplication.run(MypagesGodApplication.class, args);
    }

}
