package com.feel.modules.wallet.event;

import cn.hutool.core.util.ObjectUtil;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.service.RechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zz
 * @Description: 充值事件,保存并提交充值记录
 * @Date: 10:09 PM 3/19/21
 * @Modified By
 */
@Component
@Slf4j
public class RechargeEvent {

    @Autowired
    private RechargeService rechargeService;

    /**
     * 提交充值记录
     * @param recharge
     */
    public synchronized void onConfirmed(Recharge recharge) {
        if(ObjectUtil.isNotEmpty(recharge)) {
            log.info("confirmed transaction txid[{}] from[{}] to[{}] amount[{}]",
                    recharge.getTxid(),recharge.getFromAddress(),recharge.getToAddress(),recharge.getAmount());
            boolean result = rechargeService.save(recharge);

        }

    }
}
