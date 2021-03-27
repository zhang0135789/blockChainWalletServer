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
public class Erc20Config {


    /**
     * 初始化BitcoinRpcClient
     * @param uri
     * @return
     */
//    @Bean
//    public BitcoinRpcClient setClient(@Value("${coin.rpc}") String uri){
//        BitcoinRpcClient client = null;
//        try {
//            log.info("======Start connect Omnicore Node url={} ======",uri);
//            client =  new BitcoinRpcClient(uri);
//            log.info("client block={}",client.getBlockCount());
//            log.info("======Connect Node Success ======");
//            return client;
//        } catch (Exception e) {
//            log.error("======Connect Node failed",e);
//            return client;
//        }
//    }
}
