package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.Withdraw;

/**
 * @Author: zz
 * @Description: 提现记录
 * @Date: 1:59 PM 4/6/21
 * @Modified By
 */
public interface WithdrawService {

    /**
     * 保存
     * @param withdraw
     * @return
     */
    Withdraw save(Withdraw withdraw);

    /**
     * 是否存在
     * @param withdraw
     * @return
     */
    boolean exists(Withdraw withdraw);

    /**
     * 查询最后一笔提现记录
     * @return
     */
    Withdraw findLatest();
}
