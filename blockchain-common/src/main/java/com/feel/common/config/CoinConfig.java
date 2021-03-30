package com.feel.common.config;

import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.entity.ScanRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: zz
 * @Description: 配置币种信息
 * @Date: 3:45 PM 3/10/21
 * @Modified By
 */
@Configuration
public class CoinConfig {

    /**
     * 批量注入配置参数
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "coin")
    public Coin getCoin(){
        Coin coin = new Coin();
        return coin;
    }

    /**
     * 注入扫快规则参数
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "rule")
    public ScanRule getScanRule() {
        ScanRule scanRule = new ScanRule();
        return scanRule;
    }

    /**
     * 初始化合约配置
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "contract")
    public Contract getContract(){
        Contract contract = new Contract();
        return contract;
    }
}
