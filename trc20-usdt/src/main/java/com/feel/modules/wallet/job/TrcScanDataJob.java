package com.feel.modules.wallet.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.RechargeService;
import com.feel.modules.wallet.service.Trc20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TrcScanDataJob extends ScanDataJob {
    @Resource
    private Trc20Service trc20Service;
    @Resource
    private AccountService accountService;
    @Resource
    private RechargeService rechargeService;


    @Override
    public List scanBlock(Long startBlockNumber, Long endBlockNumber) {
        List<Account> accounts = accountService.findAll();
        List<Recharge> recharges = new ArrayList<>();
        List<String> addressList = accounts.stream().map(Account::getAddress).collect(Collectors.toList());
        if(accounts.size() > 0){
            for(Long i = startBlockNumber; i <= endBlockNumber; i ++ ){
                String transactionInfoByBlockNum = trc20Service.getTransactionInfoByBlockNum(BigInteger.valueOf(i));
                JSONObject jsonObject = JSONObject.parseObject(transactionInfoByBlockNum);
//                JSONArray parseArray = JSON.parseArray(transactionInfoByBlockNum);
                JSONArray parseArray = jsonObject.getJSONArray("transactions");
                String blockHash = jsonObject.getString("blockID");
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                if (parseArray.size() > 0) {
                    parseArray.forEach(e -> {
                        try {
//                            String txId = JSON.parseObject(e.toString()).getString("id");
                            String txId = JSON.parseObject(e.toString()).getString("txID");
                            //判断 数据库 txId 有 就不用往下继续了
                            if(txId.equals("15b7a100216d040df5bcebff51b217b7c749db614f787f24dfc0a448eb13ab44")){
                                log.info("txid------------"+txId);
                                log.info(transactionInfoByBlockNum);
                            }

                        accounts.forEach( k -> {
                            Recharge recharge = Recharge.builder()
                                    .toAddress(k.getAddress())
                                    .txid(txId)
                                    .build();
                            boolean  flag = rechargeService.exists(recharge);
                            if(flag){
                                atomicBoolean.set(true);
                            }
                        });

                            if(atomicBoolean.get()){
                                return;
                            }

                            JSONObject parseObject = JSON.parseObject(trc20Service.getTransactionById(txId));
                            String contractRet = parseObject.getJSONArray("ret").getJSONObject(0).getString("contractRet");
                            //交易成功
                            if ("SUCCESS".equals(contractRet)) {
                                String type = parseObject.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getString("type");
                                if ("TriggerSmartContract".equals(type)) {
                                    //合约地址转账
                                    //triggerSmartContract(addressList, txId, parseObject);
                                    Recharge recharge1 = trc20Service.triggerSmartContract(addressList, txId, parseObject);
                                    if(recharge1 != null){
                                        recharge1.setBlockHash(blockHash);
                                        recharges.add(recharge1);
                                    }

                                } else if ("TransferContract".equals(type)) {
                                    //trx 转账
                                    //transferContract(parseObject);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
            }




        }
        return recharges;
    }

    @Override
    public Long getNetworkBlockHeight() {
        return trc20Service.height();
    }


}
