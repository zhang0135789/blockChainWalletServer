package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.CollectionTran;

/**
 * @Author: zz
 * @Description:
 * @Date: 7:39 PM 3/28/21
 * @Modified By
 */
public interface CollectionService {

    CollectionTran save(CollectionTran collectionTran);

    boolean exists(CollectionTran collectionTran);
}
