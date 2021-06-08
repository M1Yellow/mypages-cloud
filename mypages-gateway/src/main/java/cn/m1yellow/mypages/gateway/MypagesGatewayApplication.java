package cn.m1yellow.mypages.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "cn.m1yellow.mypages")
public class MypagesGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MypagesGatewayApplication.class, args);
    }

}
