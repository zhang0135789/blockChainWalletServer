package com.feel.modules.wallet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.CollectionTran;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.service.AccountCollectionService;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.CollectionService;
import com.feel.modules.wallet.service.Erc20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:54 PM 3/28/21
 * @Modified By
 */
@Service("AccountCollectionService")
@Slf4j
public class AccountCollectionServiceImpl implements AccountCollectionService {

    @Autowired
    private Erc20Service erc20Service;
    @Autowired
    private Contract contract;
    @Autowired
    private Coin coin;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CollectionService collectionService;

    /**
     * 检查账户手续费
     * @param account
     */
    @Override
    public void checkAccount(Account account) {
        try {
            BigDecimal minerFee = erc20Service.getMinerFee(contract.getGasLimit());
            BigDecimal ethBalance = erc20Service.getBalance(account.getAddress());
            BigDecimal tokenBalance = erc20Service.getTokenBalance(account.getAddress());
            //给满足条件的地址充矿工费，条件1：eth额度小于minerFee,条件2:balance大于等于minCollectAmount
            if (ethBalance.compareTo(minerFee) < 0
                    && tokenBalance.compareTo(coin.getMinCollectAmount()) >= 0) {
                log.info("======>process account:{}", account.getAddress());
                //计算本次要转的矿工费
                BigDecimal feeAmt = minerFee.subtract(ethBalance);
                //发送旷工费
                String hash = erc20Service.transfer("",account.getAddress(), feeAmt , null);
                log.info("======>transfer fee {},result:{}", feeAmt, hash);
                if(ObjectUtil.isNotNull(hash)){
                    ethBalance = minerFee;
                }
                accountService.updateStatus(account.getAddress(),1);
            }
            //同步账户余额
            accountService.updateBalanceAndGas(account.getAddress(), tokenBalance, ethBalance);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("check account error : " , e);
        }
    }

    /**
     * 归集
     * @param account
     */
    @Override
    public void collectionCoin(Account account) {
        try {
            String collectionAddress = coin.getCollectionAddress();
            BigDecimal minerFee = erc20Service.getMinerFee(contract.getGasLimit());
            BigDecimal tokenBalance = erc20Service.getTokenBalance(account.getAddress());
            String hash = erc20Service.transferToken(account.getAddress(), collectionAddress, tokenBalance, minerFee);

            accountService.updateStatus(account.getAddress(),0);

            log.info("collection success : from[{}],to[{}],amount[{}],hash[{}]",account.getAddress(),coin.getCollectionAddress(),tokenBalance,hash);

            //TODO 归集记录
            CollectionTran collectionTran = new CollectionTran();
            collectionTran.setFromAddress(account.getAddress());
            collectionTran.setToAddress(collectionAddress);
            collectionTran.setAmount(tokenBalance);
            collectionTran.setTxid(hash);
            collectionTran.setTime(new Date());
            collectionService.save(collectionTran);


        } catch (Exception e) {
            log.error("collection error :" ,e);
        }


    }
}
