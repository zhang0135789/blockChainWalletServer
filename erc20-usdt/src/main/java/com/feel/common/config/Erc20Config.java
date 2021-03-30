package com.feel.common.config;

import com.feel.common.utils.EtherApiUtils;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Contract;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


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
     * 加载web3j
     * @param coin
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "coin.keystore-path")
    public Web3j web3j(Coin coin) {
        log.info("======>Start connect eth Node url={} ======",coin.getRpc());
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30*1000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(30*1000, TimeUnit.MILLISECONDS);
        builder.readTimeout(30*1000, TimeUnit.MILLISECONDS);
        OkHttpClient httpClient = builder.build();
        Web3j web3j = Web3j.build(new HttpService(coin.getRpc(),httpClient,false));
        return web3j;
    }




    /**
     * 注入jsonrpcClient
     * @param coin
     * @return
     * @throws MalformedURLException
     */
    @Bean
    public JsonRpcHttpClient jsonrpcClient(Coin coin) throws MalformedURLException {
        log.info("======>init jsonRpcClient");
        JsonRpcHttpClient jsonrpcClient = new JsonRpcHttpClient(new URL(coin.getRpc()));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        jsonrpcClient.setHeaders(headers);
        return jsonrpcClient;
    }


    @Bean
    @ConfigurationProperties(prefix = "etherscan")
    public EtherApiUtils etherscanApi(){
        log.info("======>初始化EtherApiUtils");
        EtherApiUtils apiUtils = new EtherApiUtils();
        return apiUtils;
    }

}
