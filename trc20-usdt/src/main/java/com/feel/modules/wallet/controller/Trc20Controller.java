package com.feel.modules.wallet.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.Trc20Service;
import com.feel.modules.wallet.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class Trc20Controller extends WalletController<Trc20Service>{

    @Resource
    private AccountService accountService;
    @Resource
    private Coin coin;


    @Override
    @GetMapping("/getNewAddress")
    @ApiOperation("获取新地址")
    public R<String> getNewAddress(String accountName) {
        Account account = accountService.findByName(accountName,"TRON");
        if(ObjectUtil.isNotEmpty(account)) {
            return R.ok(account);
        }
        try {
            account = walletService.createNewAddress(accountName);
        } catch (Exception e) {
            log.error("获取地址失败",e);
            return R.error("获取地址失败");
        }

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
        BigDecimal balance = null;
        try {
            balance = walletService.getBalance(address);
        } catch (Exception e) {
            log.error("获取地址总资产失败",e);
            return R.error("获取地址总资产失败");
        }
        return R.ok(balance);
    }

    @Override
    @GetMapping("/transfer")
    @ApiOperation("交易")
    public R<String> transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Throwable {
        String txid = walletService.transfer(from,to,amount,fee);
        return R.ok(txid);
    }

    @Override
    R withdrawTransfer(String to, BigDecimal amount, BigDecimal fee) {
        String fromAddress = coin.getWithdrawAddress();
        String txid = "";
        try{
            txid = walletService.withdrawTransfer(to,amount,fee);
        }catch (Exception e){
            return R.error();
        }
        return R.ok(txid);
    }


}
