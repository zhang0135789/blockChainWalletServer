package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.Account;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:23 PM 3/28/21
 * @Modified By
 */
public interface AccountCollectionService {

    /**
     * 检查账户是否需要归集
     * 检查账户手续费是否充足
     * @param account
     */
    void checkAccount(Account account);

    /**
     * 归集
     * @param account
     */
    void collectionCoin(Account account);
}
