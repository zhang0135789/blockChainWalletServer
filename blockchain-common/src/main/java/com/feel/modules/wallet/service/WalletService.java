package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.utils.R;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author: zz
 * @Description: 钱包对接接口
 * @Date: 2:51 PM 3/15/21
 * @Modified By
 */
public interface WalletService {

    /**
     * 创建新账户
     * @param accountName
     * @return
     */
    Account createNewAddress(String accountName) throws Exception;

    /**
     * 区块高度
     * @return
     */
    Long height();

    /**
     * 获取地址总资产
     * @param address
     * @return
     */
    BigDecimal getBalance(String address) throws Exception;

    /**
     * 交易
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    String transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Exception;


    /**
     * 提现接口
     * @param to
     * @param amount
     * @param fee
     * @return
     * @throws Throwable
     */
    String withdrawTransfer(String to , BigDecimal amount , BigDecimal fee);

    public   String getPrivateKey(String address);

}
