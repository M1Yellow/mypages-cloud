package cn.m1yellow.mypages.excavation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"cn.m1yellow.mypages"})
public class MypagesExcavationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MypagesExcavationApplication.class, args);
    }

}
