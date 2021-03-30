package com.feel.modules.wallet.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.service.Trc20Service;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trc20CollectionJob implements CollectionJob {
    @Resource
    private Trc20Service trc20Service;
    @Resource
    private Contract contract;
    @Resource
    private Coin coin;
    @Override
    public void collectionCoin() {

    }

    @Override
    public void checkAccount() {
        try {
            //获取需要汇集用户地址
            Map<String, String> addressMap = new HashMap<>();
            addressMap.put("xxx", "xxxx");
            //汇集到的地址
            String toAddress = coin.getCollectionAddress();
            String fromAddress = null;
            String privateKey = null;
            Map<String, String> symbolMap = new HashMap<>();
            for (String symbol : symbolMap.keySet()) {
                for (String key : addressMap.keySet()) {
                    fromAddress = key;
                    privateKey = addressMap.get(key);
                    String trc20Account = trc20Service.getTrc20Account(contract.getAddress(), fromAddress);
                    JSONObject jsonObject = JSON.parseObject(trc20Account);
                    String constant_result = jsonObject.getString("constant_result");

                    if (StringUtils.isEmpty(constant_result)) {
                        continue;
                    }

                    List<String> strings = JSON.parseArray(constant_result.toString(), String.class);

                    String data = strings.get(0).replaceAll("^(0+)", "");
                    if (data.length() == 0) {
                        continue;
                    }

                    String amountStr = new BigInteger(data, 16).toString();
                    BigDecimal amount = new BigDecimal(amountStr).divide(contract.getDecimal());

                    if (amount.compareTo(BigDecimal.ONE) < 0) {
                        continue;
                    }

                    String account = trc20Service.getAccount(fromAddress);
                    String accountBalance = JSON.parseObject(account).getString("balance");
                    BigDecimal balance = BigDecimal.ZERO;

                    if (StringUtils.isNotEmpty(accountBalance)) {
                        balance = new BigDecimal(accountBalance).divide(contract.getDecimal());
                    }

                    if (balance.compareTo(new BigDecimal("0.5")) < 0) {
                        // 充值手续费
                        String transaction = trc20Service.transaction(fromAddress, new BigDecimal("0.5"));
                        continue;
                    }

                    // 汇集 转账
                    String transaction = trc20Service.trc20Transaction(contract.getAddress(), toAddress, amount);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
