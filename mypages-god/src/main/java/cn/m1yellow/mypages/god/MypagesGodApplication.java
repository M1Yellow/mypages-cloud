package cn.m1yellow.mypages.god;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.m1yellow.mypages"})
public class MypagesGodApplication {

    public static void main(String[] args) {
        SpringApplication.run(MypagesGodApplication.class, args);
    }

}
