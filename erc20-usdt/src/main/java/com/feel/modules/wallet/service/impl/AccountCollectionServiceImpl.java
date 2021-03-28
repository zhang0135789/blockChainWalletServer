package com.feel.modules.wallet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.service.AccountCollectionService;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.Erc20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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


    @Override
    public void checkAccount(Account account) {
        try {
            BigDecimal minerFee = erc20Service.getMinerFee(contract.getGasLimit());
            BigDecimal ethBalance = erc20Service.getEthBalance(account.getAddress());
            BigDecimal tokenBalance = erc20Service.getBalance(account.getAddress());
            //给满足条件的地址充矿工费，条件1：eth额度小于minerFee,条件2:balance大于等于minCollectAmount
            if (ethBalance.compareTo(minerFee) < 0
                    && tokenBalance.compareTo(coin.getMinCollectAmount()) >= 0) {
                log.info("======>process account:{}", account);
                //计算本次要转的矿工费
                BigDecimal feeAmt = minerFee.subtract(ethBalance);

                String hash = erc20Service.transferFromEthWithdrawWallet(account.getAddress(), feeAmt, true, "");
                log.info("======>transfer fee {},result:{}", feeAmt, hash);
                if(ObjectUtil.isNotNull(hash)){
                    ethBalance = minerFee;
                }
            }
            //同步账户余额
            accountService.updateBalanceAndGas(account.getAddress(), tokenBalance, ethBalance);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("check account error : " , e);
        }
    }

    @Override
    public void collectionCoin(Account account) {
        try {
            BigDecimal minerFee = erc20Service.getMinerFee(contract.getGasLimit());
            BigDecimal tokenBalance = erc20Service.getBalance(account.getAddress());
            String hash = erc20Service.transfer(account.getAddress(), coin.getMasterAddress(), tokenBalance, minerFee);
            log.info("collection success : from[{}],to[{}],amount[{}],hash[{}]",account.getAccount(),coin.getMasterAddress(),tokenBalance,minerFee);
            //TODO归集记录

        } catch (Exception e) {
            log.error("collection error :" ,e);
        }


    }
}
