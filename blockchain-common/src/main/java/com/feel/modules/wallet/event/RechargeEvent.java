package com.feel.modules.wallet.event;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.RechargeService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
    @Resource
    private AccountService accountService;

    /**
     * 提交充值记录
     * @param recharge
     */
    public synchronized void onConfirmed(Recharge recharge) {
        if(ObjectUtil.isNotEmpty(recharge)) {
            log.info("confirmed transaction txid[{}] from[{}] to[{}] amount[{}]",
                    recharge.getTxid(),recharge.getFromAddress(),recharge.getToAddress(),recharge.getAmount());
            Recharge result = rechargeService.save(recharge);
            //查找賬戶餘額
            Account account1 = accountService.findByAddress(recharge.getToAddress());
            //更新賬戶餘額
            accountService.updateBalance(recharge.getToAddress(),account1.getBalance().add(recharge.getAmount()));

            //TODO 通知业务系统充值记录
            log.info("#######################充值[{}]:{}",recharge.getToAddress(),recharge.getAmount());
            String url = "http://121.5.12.126:8000/api/usdtDeposit/bookedNotice";
            JSONObject body = new JSONObject();
            body.put("personCode",account1.getAccount());
            body.put("amount",recharge.getAmount());
            body.put("address",recharge.getToAddress());
            body.put("type",recharge.getType());
            String post = HttpUtil.post(url, body.toString());
            log.info("#######################充值结果:{}",post);
        }

    }


    public static void main(String[] args) {
        String url = "http://121.5.12.126:8000/api/usdtDeposit/bookedNotice";
        JSONObject body = new JSONObject();
        body.put("personCode","123");
        body.put("amount",123);
        body.put("address","1234545");
        body.put("type",1);
        String post = HttpUtil.post(url, body.toString());
        System.out.println(post);
    }
}
