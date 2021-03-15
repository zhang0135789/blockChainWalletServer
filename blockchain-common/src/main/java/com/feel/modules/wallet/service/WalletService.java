package com.feel.modules.wallet.service;

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
    String createNewAddress(String accountName);



}
