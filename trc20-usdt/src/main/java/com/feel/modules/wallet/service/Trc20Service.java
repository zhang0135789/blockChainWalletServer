package com.feel.modules.wallet.service;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:29 PM 3/15/21
 * @Modified By
 */
public interface Trc20Service extends WalletService{

    public String createNewAddress(String accountName);

    /**
     * 区块高度
     * @return
     */
    public Integer height() ;

    /**
     * 获取地址总资产
     * @param address
     * @return
     */
    public BigDecimal getTrcBalance(String address) throws IOException;

    /**
     * 交易
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    public String transferTrc(String from, String to, BigDecimal amount, BigDecimal fee) throws Throwable;


    public BigDecimal getBalance(String address) throws IOException;

    /**
     * 交易
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    public String transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Throwable;
}
