package com.feel.modules.wallet.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feel.common.utils.TrxUtils;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.RechargeService;
import com.feel.modules.wallet.service.Trc20Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.common.utils.ByteArray;
import org.tron.walletserver.WalletApi;

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
    @Autowired
    private Contract contract;

    @Override
    public List scanBlock(Long startBlockNumber, Long endBlockNumber) {
        List<Recharge> recharges = new ArrayList<>();
            for(Long i = startBlockNumber; i <= endBlockNumber; i ++ ){
//                String transactionInfoByBlockNum = trc20Service.getTransactionInfoByBlockNum(BigInteger.valueOf(i));
                String transactionInfoByBlockNum2 = trc20Service.getTransactionByBlockNum(i);
                JSONObject jsonObject2 = JSONObject.parseObject(transactionInfoByBlockNum2);
//                JSONArray parseArray = JSON.parseArray(transactionInfoByBlockNum);
                JSONArray parseArray = jsonObject2.getJSONArray("transactions");
//                String blockHash = jsonObject.getString("blockID");
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                if (parseArray != null && parseArray.size() > 0) {
                    Long finalI = i;
                    parseArray.forEach(e -> {
                        try {
                            String txId = JSON.parseObject(e.toString()).getString("txID");
                            JSONObject json = JSON.parseObject(e.toString());

                            // 合约地址
                            String contractAddress = TrxUtils.getContractAddress(json);
                            //相匹配的合约地址
                            if (!contract.getAddress().equals(contractAddress)) {
                                return ;
                            }
                            String toAddress = TrxUtils.getToAddress(json);

                           boolean isExist = accountService.isAddressExist(toAddress);
                           if(!isExist){
                               return;
                           }
                            Recharge recharge2 = Recharge.builder()
                                    .toAddress(toAddress)
                                    .txid(txId)
                                    .build();
                            boolean  flag = rechargeService.exists(recharge2);
                            if(flag){
                                return ;
                            }
                            String contractRet = json.getJSONArray("ret").getJSONObject(0).getString("contractRet");
                            //交易成功
                            if ("SUCCESS".equals(contractRet)) {
                                String type = json.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getString("type");
                                if ("TriggerSmartContract".equals(type)) {
                                    //合约地址转账
                                    //triggerSmartContract(addressList, txId, parseObject);
                                    Recharge recharge1 = trc20Service.triggerSmartContract(txId, json);
                                    if(recharge1 != null){
                                        String res = trc20Service.getTransactionByBlockNum(finalI);
                                        JSONObject jsonObject = JSONObject.parseObject(res);
                                        String blockHash = jsonObject.getString("blockID");
                                        recharge1.setBlockHash(blockHash);
                                        recharge1.setBlockHeight(finalI);
                                        recharge1.setType(1);
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




        return recharges;
    }

    @Override
    public Long getNetworkBlockHeight() {
        return trc20Service.height();
    }


}
