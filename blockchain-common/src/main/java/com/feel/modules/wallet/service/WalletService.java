package com.feel.modules.wallet.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

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
    String createNewAddress(String accountName) throws NoSuchAlgorithmException;

    /**
     * 区块高度
     * @return
     */
    Integer height();

    /**
     * 获取地址总资产
     * @param address
     * @return
     */
    BigDecimal getBalance(String address) throws IOException;

    /**
     * 交易
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    String transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Throwable;
}
