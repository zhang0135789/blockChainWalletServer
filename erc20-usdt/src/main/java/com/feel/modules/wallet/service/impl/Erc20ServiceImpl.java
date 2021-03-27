package com.feel.modules.wallet.service.impl;

import com.feel.common.utils.BitcoinRpcClient;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.service.Erc20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:30 PM 3/15/21
 * @Modified By
 */
@Service
@Slf4j
public class Erc20ServiceImpl implements Erc20Service {


    @Autowired
    private Coin coin;


    /**
     * 创建地址
     * @param accountName
     * @return
     */
    @Override
    public String createNewAddress(String accountName) {
        String newAddress = "bitcoinClient.getNewAddress(accountName)";
        log.info("new address [{}]" , newAddress);
        return newAddress;
    }

    /**
     * 区块高度
     * @return
     */
    @Override
    public Integer height() {
        Integer count = 0;
        Integer height = count - 1;
        log.info("block height [{}]" , height);
        return height;
    }

    /**
     * 获取Omni地址总资产
     * @param address
     * @return
     */
    @Override
    public BigDecimal getBalance(String address) {
        BigDecimal balance =BigDecimal.ONE;
        log.info("omni balance : address[{}],balance[{}]" , address , balance);
        return balance;
    }

    /**
     * 交易
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    @Override
    public String transfer(String from, String to, BigDecimal amount, BigDecimal fee) {
        String txid = "bitcoinClient.omniSend(from , to , amount)";
        log.info("omni transfer : txid[{}]" , txid);
        return txid;
    }


}
