package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.Recharge;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:38 PM 3/22/21
 * @Modified By
 */
public interface RechargeService {

    Recharge save(Recharge recharge);

    boolean exists(Recharge recharge);

    Recharge findLatest();
}
