package com.vegetable.mypages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class MypagesAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MypagesAdminApplication.class, args);
    }

}
