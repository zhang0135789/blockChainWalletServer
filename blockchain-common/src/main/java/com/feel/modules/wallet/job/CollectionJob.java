package com.feel.modules.wallet.job;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:33 PM 3/28/21
 * @Modified By
 */
public interface CollectionJob {


    /**
     * 归集
     */
    void collectionCoin();

    /**
     * 检查账户手续费
     */
    void checkAccount();
}
