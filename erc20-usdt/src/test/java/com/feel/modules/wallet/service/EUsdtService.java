package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:50 PM 4/5/21
 * @Modified By
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EUsdtService {


    @Autowired
    private WalletService walletService;

    @Test
    public void getNewAddress() throws Exception {
        String accountName = "zz";
        Account account = walletService.createNewAddress(accountName);


        System.out.println(account.getAddress());
    }

    @Test
    public void height() {
        walletService.height();
    }


    @Autowired
    private AccountService accountService;

    @Test
    public void testExit() {
        boolean flag = accountService.isAddressExist("0xdac17f958d2ee523a2206206994597c13d831ec7");
        System.out.println(false);
    }

}
