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


    public String trc20Transaction(String contractAddress,String fromAddress, String toAddress, BigDecimal amount);

    public  String getTransactionInfoByBlockNum(BigInteger num);

    public  String getTransactionById(String txId);

    public Recharge triggerSmartContract(List<String> addressList, String txId, JSONObject parseObject);

    public  String getTrc20Account(String contractAddress, String address);

    public  String getAccount(String address);
}
