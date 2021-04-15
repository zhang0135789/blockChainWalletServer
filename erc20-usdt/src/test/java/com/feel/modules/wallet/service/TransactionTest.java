package com.feel.modules.wallet.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:01 PM 4/14/21
 * @Modified By
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionTest {


    @Autowired
    private Erc20Service erc20Service;

    @Test
    public void TestTransaction() throws Exception{

        String transfer = erc20Service.transfer("0x4dbaf19bf532e92806d11474015fe32b4c5d54f0",
                "0x6323297987278Bc22a114d2c6E2a995abD9D98D5",
                new BigDecimal("10"),
                new BigDecimal("0"));

        System.out.println(transfer);
    }


}
