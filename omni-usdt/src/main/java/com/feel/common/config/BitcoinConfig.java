package com.feel.common.config;

import com.feel.common.utils.BitcoinRpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
        BitcoinRpcClient client = null;
        try {
            log.info("uri={}",uri);
            client =  new BitcoinRpcClient(uri);
            log.info("=============================");
            log.info("client block={}",client.getBlockCount());
            log.info("=============================");
            return client;
        } catch (Exception e) {
            log.error("init wallet failed" ,e);
            return client;
        }
    }
}
