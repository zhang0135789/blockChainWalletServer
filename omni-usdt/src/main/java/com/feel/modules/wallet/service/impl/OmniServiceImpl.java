package com.feel.modules.wallet.service.impl;

import com.feel.common.utils.BitcoinRpcClient;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.OmniService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:30 PM 3/15/21
 * @Modified By
 */
@Service
@Slf4j
public class OmniServiceImpl implements OmniService  {


    @Autowired
    private BitcoinRpcClient bitcoinClient;

    @Autowired
    private Coin coin;

    @Autowired
    private AccountService accountService;

    /**
     * 创建地址
     * @param accountName
     * @return
     */
    @Override
    public Account createNewAddress(String accountName) {
        String newAddress = bitcoinClient.getNewAddress(accountName);

        Account account = Account.builder()
                .account(accountName)
                .address(newAddress)
//                .walletFile(fileName)
                .createDate(new Date())
                .build();

        account = accountService.save(account);

        log.info("new address [{}]" , account.getAddress());
        return account;
    }

    /**
     * 区块高度
     * @return
     */
    @Override
    public Long height() {
        Long count = Long.valueOf(bitcoinClient.getBlockCount());
        Long height = count - 1;
        log.info("block height [{}]" , height);
        return height;
    }

    /**
     * 获取地址总资产-btc
     * @param address
     * @return
     */
    @Override
    public BigDecimal getBalance(String address) {
        BigDecimal balance = BigDecimal.valueOf(bitcoinClient.getAddressBalance(address).getBalance());
        log.info("omni balance : address[{}],balance[{}]" , address , balance);
        return balance;
    }

    /**
     * 获取地址总资产-usdt
     * @param address
     * @return
     * @throws Exception
     */
    @Override
    public BigDecimal getTokenBalance(String address) throws Exception {
        BigDecimal balance = bitcoinClient.getOmniBalance(address);
        log.info("omni balance : address[{}],balance[{}]" , address , balance);
        return balance;
    }

    /**
     * 交易-btc
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    @Override
    public String transfer(String from, String to, BigDecimal amount, BigDecimal fee) {
        String txid = bitcoinClient.sendFrom(from , to , amount);
        log.info("omni transfer : txid[{}]" , txid);
        return txid;
    }

    /**
     * 交易- usdt
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     * @throws Exception
     */
    @Override
    public String transferToken(String from, String to, BigDecimal amount, BigDecimal fee) throws Exception {
        String txid = bitcoinClient.omniSend(from , to , amount);
        log.info("omni transfer : txid[{}]" , txid);
        return txid;
    }

    /**
     * 提现
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    @Override
    public String withdrawTransfer(String to, BigDecimal amount, BigDecimal fee) {
        //TODO 提现
        return null;
    }


}
