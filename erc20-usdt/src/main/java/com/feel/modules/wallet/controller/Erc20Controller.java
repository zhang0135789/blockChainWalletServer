package com.feel.modules.wallet.controller;

import com.feel.modules.wallet.service.Erc20Service;
import com.feel.modules.wallet.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:24 PM 3/15/21
 * @Modified By
 */
@RestController
@RequestMapping("/rpc")
@Api(tags = "币种信息 : erc20-usdt" ,value = "erc20-usdt")
public class Erc20Controller extends WalletController<Erc20Service>{


    @Override
    @GetMapping("/getNewAddress")
    @ApiOperation("获取新地址")
    public R<String> getNewAddress(String accountName) {
        String newAddress = walletService.createNewAddress(accountName);
        return R.ok(newAddress);
    }

    @Override
    @GetMapping("/height")
    @ApiOperation("获取区块高度")
    public R<Integer> getBlockHeight() {
        Integer height = walletService.height();
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
    public R<BigDecimal> balance(String address) {
        BigDecimal balance = walletService.getBalance(address);
        return R.ok(balance);
    }

    @Override
    @GetMapping("/transfer")
    @ApiOperation("交易")
    public R<String> transfer(String from, String to, BigDecimal amount, BigDecimal fee) {
        String txid = walletService.transfer(from,to,amount,fee);
        return R.ok(txid);
    }


}
