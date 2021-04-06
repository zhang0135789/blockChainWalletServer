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
     * 获取数据库,trc地址当前余额
     * @param address
     * @return
     */
    public BigDecimal getTrcBalance(String address) throws IOException;


    /**
     * trc20交易
     * @param contractAddress
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @return
     */
    public String trc20Transaction(String contractAddress,String fromAddress, String toAddress, BigDecimal amount);



    /**
     * 交易
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    public String transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Exception;

    /**
     * 根据区块高度，获取区块交易详情
     * @param num
     * @return
     */
    public  String getTransactionInfoByBlockNum(BigInteger num);

    /**
     * 根据交易txid获取交易详情
     * @param txId
     * @return
     */
    public  String getTransactionById(String txId);

    /**
     * 获取trc20交易信息
     * @param addressList
     * @param txId
     * @param parseObject
     * @return
     */
    public Recharge triggerSmartContract(List<String> addressList, String txId, JSONObject parseObject);

    /**
     * 获取合约地址余额
     * @param contractAddress
     * @param address
     * @return
     */
    public  String getTrc20Account(String contractAddress, String address);

    /**
     * 获取trx地址余额
     * @param address
     * @return
     */
    public  String getAccount(String address);

    /**
     * 创建trx交易信息
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @return
     */
    public  String transaction(String fromAddress,String toAddress, BigDecimal amount);
}
