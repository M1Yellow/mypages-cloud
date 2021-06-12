package cn.m1yellow.mypages.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "cn.m1yellow.mypages")
public class MypagesAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MypagesAuthApplication.class, args);
    }

}
