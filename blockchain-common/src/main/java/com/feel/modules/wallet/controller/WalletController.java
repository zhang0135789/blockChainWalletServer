package com.feel.modules.wallet.controller;

import com.feel.modules.wallet.service.WalletService;
import com.feel.modules.wallet.utils.R;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: zz
 * @Description: 钱包外部访问接口
 * @Date: 3:04 PM 3/15/21
 * @Modified By
 */
public abstract class WalletController<S extends WalletService> {

    S walletService;


    public S getS() {
        return walletService;
    }
    /**
     * 注入服务层
     */
    @Autowired(required = false)
    public void setS(S s) {
        this.walletService = s;
    }


    /**
     * 获取新地址
     * @param accountName
     * @return
     */
    abstract R getNewAddress(String accountName) ;

    /**
     * 获取网络区块高度
     * @return
     */
    abstract R getBlockHeight();

    /**
     * 获取节点总资产
     * @return
     */
    abstract R balances();

    /**
     * 获取地址总资产
     * @param address
     * @return
     */
    abstract R balance(String address) ;

    /**
     * 转账
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    abstract R transfer(String from , String to , BigDecimal amount , BigDecimal fee);

    /**
     * 提现接口
     * @param to
     * @param amount
     * @param fee
     * @return
     * @throws Throwable
     */
    abstract R withdrawTransfer(String to , BigDecimal amount , BigDecimal fee);

}
