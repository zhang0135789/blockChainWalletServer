package com.feel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WalletOmniApplication {
    public static void main(String[] args){
        SpringApplication.run(WalletOmniApplication.class,args);
    }
}
