package com.feel.modules.wallet.controller;

import com.alibaba.fastjson.JSONObject;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.Trc20Service;
import com.feel.modules.wallet.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:24 PM 3/15/21
 * @Modified By
 */
@RestController
@RequestMapping("/rpc")
@Api(tags = "币种信息 : trc20-usdt" ,value = "trc20-usdt")
public class Trc20Controller extends WalletController<Trc20Service>{

    @Resource
    private AccountService accountService;


    @Override
    @GetMapping("/getNewAddress")
    @ApiOperation("获取新地址")
    public R<String> getNewAddress(String accountName) throws NoSuchAlgorithmException {
        String addressInfo = walletService.createNewAddress(accountName);

        Account account  = JSONObject.parseObject(addressInfo,Account.class);
        accountService.save(account);
        return R.ok(account.getAddress());
    }

    @Override
    @GetMapping("/height")
    @ApiOperation("获取区块高度")
    public R getBlockHeight() {
        Long height = walletService.height();
        return R.ok(height);
    }


    @Override
    @GetMapping("/balances")
    @ApiOperation("获取节点总资产")
    public R balances() {
        //TODO
        return null;
    }

    @Override
    @GetMapping("/balance")
    @ApiOperation("获取地址总资产")
    public R<BigDecimal> balance(String address) throws IOException {
        BigDecimal balance = walletService.getBalance(address);
        return R.ok(balance);
    }

    @Override
    @GetMapping("/transfer")
    @ApiOperation("交易")
    public R<String> transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Throwable {
        String txid = walletService.transfer(from,to,amount,fee);
        return R.ok(txid);
    }


}
