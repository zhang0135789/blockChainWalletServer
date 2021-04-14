package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.Coin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;

/**
 * @Author: zz
 * @Description:
 * @Date: 2:10 PM 4/14/21
 * @Modified By
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class WalletTest {


    @Autowired
    private Coin coin;

    @Test
    public void testCreateAddress() throws Exception {

        String fileName = WalletUtils.generateNewWalletFile("", new File(coin.getKeystorePath()), true);
        Credentials credentials = WalletUtils.loadCredentials("", coin.getKeystorePath() + "/" + fileName);
        String newAddress = credentials.getAddress();
        System.out.println("address: " + newAddress);
        System.out.println("fileName: " + fileName);



    }

    @Autowired
    private AccountService accountService;

    @Test
    public void testUpdate(){
        accountService.updateStatus("0x4dbaf19bf532e92806d11474015fe32b4c5d54f0",1);

    }
}
