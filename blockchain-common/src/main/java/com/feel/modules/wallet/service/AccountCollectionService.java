package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.Account;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:23 PM 3/28/21
 * @Modified By
 */
public interface AccountCollectionService {


    void checkAccount(Account account);

    void collectionCoin(Account account);
}
