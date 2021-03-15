package com.feel.common.config;

import com.feel.common.utils.BitcoinRpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:43 PM 3/15/21
 * @Modified By
 */
@Configuration
@Slf4j
public class BitcoinConfig {


    /**
     * 初始化BitcoinRpcClient
     * @param uri
     * @return
     */
    @Bean
    public BitcoinRpcClient setClient(@Value("${coin.rpc}") String uri){
        try {
            log.info("uri={}",uri);
            BitcoinRpcClient client =  new BitcoinRpcClient(uri);
            log.info("=============================");
            log.info("client={}",client);
            log.info("=============================");
            return client;
        } catch (MalformedURLException e) {
            log.error("init wallet failed" ,e);
            return null;
        }
    }
}
