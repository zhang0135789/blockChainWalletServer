package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.ScanLog;

/**
 * @Author: zz
 * @Description:
 * @Date: 11:07 PM 3/19/21
 * @Modified By
 */
public interface ScanLogService {

    void update(String coinName, Long blockHeight);

    ScanLog findOne(String coinName);
}
