package com.feel.modules.wallet.service;

import com.alibaba.fastjson.JSONObject;
import com.feel.modules.wallet.entity.Recharge;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:29 PM 3/15/21
 * @Modified By
 */
public interface Trc20Service extends WalletService{




    /**
     * 获取地址总资产
     * @param address
     * @return
     */
    public BigDecimal getTrcBalance(String address) throws IOException;


    public String trc20Transaction(String symbol, String toAddress, BigDecimal amount);



    /**
     * 交易
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    public String transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Exception;

    public  String getTransactionInfoByBlockNum(BigInteger num);
    public  String getTransactionById(String txId);
    public Recharge triggerSmartContract(List<String> addressList, String txId, JSONObject parseObject);
}
