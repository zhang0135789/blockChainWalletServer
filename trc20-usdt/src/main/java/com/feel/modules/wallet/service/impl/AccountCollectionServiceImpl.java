package com.feel.modules.wallet.service.impl;


import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.CollectionTran;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.service.AccountCollectionService;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.CollectionService;
import com.feel.modules.wallet.service.Trc20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:54 PM 3/28/21
 * @Modified By
 */
@Service("AccountCollectionService")
@Slf4j
public class AccountCollectionServiceImpl implements AccountCollectionService {

    @Autowired
    private Trc20Service trc20Service;
    @Autowired
    private Contract contract;
    @Autowired
    private Coin coin;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CollectionService collectionService;

    /**
     * 检查账户手续费
     * @param account
     */
    @Override
    public void checkAccount(Account account) {
        try {
            List<Account> accounts = accountService.findByBalance(coin.getMinCollectAmount());
            accounts.forEach( i -> {
                accountService.updateStatus(i.getAddress(),1);
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("check account error : " , e);
        }
    }

    /**
     * 归集
     * @param account
     */
    @Override
    public void collectionCoin(Account account) {
        try {

            //汇集到的地址
            String toAddress = coin.getCollectionAddress();


                    BigDecimal amount = BigDecimal.ZERO;
                    try {
                        amount = trc20Service.getTrcBalance(account.getAddress());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (amount.compareTo(BigDecimal.ONE) < 0) {
                        accountService.updateBalance(account.getAddress(),BigDecimal.ZERO);
                        return;
                    }

//
                    // 汇集 转账
                    String transaction = trc20Service.trc20Transaction(contract.getAddress(),account.getAddress(), toAddress, amount);
                    log.info("提现结果："+transaction);

                    //提现成功需要，更新本地余额
                    accountService.updateBalance(account.getAddress(),BigDecimal.ZERO);

            //TODO 归集记录
            CollectionTran collectionTran = new CollectionTran();
            collectionTran.setFromAddress(account.getAddress());
            collectionTran.setToAddress(toAddress);
            collectionTran.setAmount(amount);
            collectionTran.setTxid(transaction);//测试后修改
            collectionTran.setTime(new Date());
            collectionService.save(collectionTran);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
