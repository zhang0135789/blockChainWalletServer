package com.feel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WalletTrc20Application {
    public static void main(String[] args){
        SpringApplication.run(WalletTrc20Application.class,args);
    }
}
