package com.feel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WalletErc20Application {
    public static void main(String[] args){
        SpringApplication.run(WalletErc20Application.class,args);
    }
}
