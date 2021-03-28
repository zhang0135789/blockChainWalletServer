package com.feel.modules.wallet.service;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:29 PM 3/15/21
 * @Modified By
 */
public interface Erc20Service extends WalletService{


    /**
     * 获取矿工费
     * @param gasLimit
     * @return
     */
    BigDecimal getMinerFee(BigInteger gasLimit) throws Exception;


    BigDecimal getEthBalance(String address) throws Exception;
}
