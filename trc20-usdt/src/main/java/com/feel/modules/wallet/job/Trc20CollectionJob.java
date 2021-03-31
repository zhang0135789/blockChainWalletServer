package com.feel.modules.wallet.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.Trc20Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Trc20CollectionJob implements CollectionJob {
    @Resource
    private Trc20Service trc20Service;
    @Resource
    private Contract contract;
    @Resource
    private Coin coin;
    @Resource
    private AccountService accountService;
    @Override
    public void collectionCoin() {

    }

    @Override
    public void checkAccount() {
        try {
            //获取需要汇集用户地址,并且余额大于或等于体现最小金额
            List<Account> accounts = accountService.findByBalance(coin.getMinCollectAmount());
            //汇集到的地址
            String toAddress = coin.getCollectionAddress();
            if(accounts.size() > 0) {
                accounts.forEach( i -> {
                    //查询链上余额
//                    String trc20Account = trc20Service.getTrc20Account(contract.getAddress(), i.getAddress());
//                    JSONObject jsonObject = JSON.parseObject(trc20Account);
//                    String constant_result = jsonObject.getString("constant_result");
//
//                    if (StringUtils.isEmpty(constant_result)) {
//                        accountService.updateBalance(i.getAddress(),BigDecimal.ZERO);
//                        return;
//                    }
//
//                    List<String> strings = JSON.parseArray(constant_result.toString(), String.class);
//
//                    String data = strings.get(0).replaceAll("^(0+)", "");
//                    if (data.length() == 0) {
//                        accountService.updateBalance(i.getAddress(),BigDecimal.ZERO);
//                        return;
//                    }
//
//                    String amountStr = new BigInteger(data, 16).toString();
                    //BigDecimal amount = new BigDecimal(amountStr).divide(contract.getDecimal());
                    BigDecimal amount = BigDecimal.ZERO;
                    try {
                        amount = trc20Service.getTrcBalance(i.getAddress());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (amount.compareTo(BigDecimal.ONE) < 0) {
                        accountService.updateBalance(i.getAddress(),BigDecimal.ZERO);
                        return;
                    }

//                    String account = trc20Service.getAccount(i.getAddress());
//                    String accountBalance = JSON.parseObject(account).getString("balance");
//                    BigDecimal balance = BigDecimal.ZERO;
//
//                    if (StringUtils.isNotEmpty(accountBalance)) {
//                        balance = new BigDecimal(accountBalance).divide(contract.getDecimal());
//                    }

//                    if (balance.compareTo(new BigDecimal("0.5")) < 0) {
//                        // 充值手续费
//                        String transaction = trc20Service.transaction(i.getAddress(), new BigDecimal("0.5"));
//                        return;
//                    }

                    // 汇集 转账
                    String transaction = trc20Service.trc20Transaction(contract.getAddress(),i.getAddress(), toAddress, amount);
                    log.info("提现结果："+transaction);

                    //提现成功需要，更新本地余额
                    accountService.updateBalance(i.getAddress(),BigDecimal.ZERO);
                });


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
