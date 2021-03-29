package com.feel.modules.wallet.service.impl;

import com.feel.common.config.CoinConfig;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.service.RechargeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RechargeServiceImplTest {
    @Resource
    private RechargeService rechargeService;

    @Test
    void save() {

        Recharge r=  Recharge.builder()
                .amount(new BigDecimal("1000"))
                .blockHash("0000000000ce9242996b51c042cb61bf2af86696d55cab517b6c5204a00d2378")
                .blockHeight(10000L)
                .txid("9ce4d116ad9d1c73c25f949e7a153bf4299a1108191b3dee4f1627238b4dd156")
                .fromAddress("dsfgsdfsdfsd")
                .toAddress("sdfsdfsdfsdf")
                .time(new Date())
                .status(0)
                .build();
        rechargeService.save(r);
    }
}