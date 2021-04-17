package com.feel.modules.wallet.job;

import cn.hutool.core.util.ObjectUtil;
import com.feel.common.utils.EtherApiUtils;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.event.RechargeEvent;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.Erc20Service;
import com.feel.modules.wallet.utils.EthConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @Author: zz
 * @Description:
 * @Date: 12:44 PM 3/28/21
 * @Modified By
 */
@Component
@Slf4j
public class Erc20ScanJob extends ScanDataJob {

    @Autowired
    private Erc20Service erc20Service;
    @Autowired
    private Web3j web3j;
    @Autowired
    private Contract contract;
    @Autowired
    private AccountService accountService;

    @Autowired
    private EtherApiUtils etherApiUtils;

//    @Autowired(required = false)
//    private EtherApiUtils etherApiUtils;


    @Override
    public List scanBlock(Long startBlockNumber, Long endBlockNumber) {
        List<Recharge> rechargeList = new ArrayList<>();
        for(Long blockHeight = startBlockNumber; blockHeight <= endBlockNumber; blockHeight++  ) {
            EthBlock block = null;
            try {
                log.info("Start Scan Block: height[{}]" , blockHeight);
                block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockHeight),true).send();
            } catch (IOException e) {
                log.error("获取区块失败",e);
            }
            //获取交易集合
            List<EthBlock.TransactionResult> transactionResults = block.getBlock().getTransactions();
            log.info("Scan Block: Height({}) - Transactions count({})", blockHeight, transactionResults.size());
            for(EthBlock.TransactionResult transactionResult : transactionResults) {
                EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
                //获取最小交易单位
                Transaction transaction = transactionObject.get();
                //获取输入
                String input = transaction.getInput();
                //获取输出
                String contractAddress = transaction.getTo();
                if(ObjectUtil.isNotNull(input) && input.length() >= 138 && contract.getAddress().equalsIgnoreCase(contractAddress)) {
                    log.info("Scan transaction ; height[{}] ,contract[{}]",blockHeight,contract.getAddress());
                    String data = input.substring(0,9);
                    data = data + input.substring(17);
                    Function function = new Function("transfer",
                            Arrays.asList(),
                            Arrays.asList(
                                    new TypeReference<Address>() {},
                                    new TypeReference<Uint256>() {}
                            )
                    );
                    List<Type> params = FunctionReturnDecoder.decode(data,function.getOutputParameters());
                    //冲币地址
                    String toAddress = params.get(0).getValue().toString();
                    String amount = params.get(1).getValue().toString();
                    log.info("Scan Address [{}] , amount[{}]" , toAddress,amount);
                    //查看是否地址池存在
                    if(accountService.isAddressExist(toAddress)) {
                        log.info("======> Find a Recharge in [{}] , amount[{}]",toAddress , amount);
                        //当eventTopic0参数不为空时检查event_log结果，防止低版本的token假充值
                        if(ObjectUtil.isNotEmpty(contract.getEventTopic0()) && etherApiUtils != null){
                            boolean checkEvent = etherApiUtils.checkEventLog(blockHeight,contract.getAddress(),contract.getEventTopic0(),transaction.getHash());
                            if(!checkEvent) {
                                continue;
                            }
                        }
                        log.info("the transaction recharge is valid [{}]" , transaction.getHash());
                        //充值
                        if(ObjectUtil.isNotEmpty(amount)) {
                            Recharge recharge = Recharge.builder()
                                    .txid(transaction.getHash())
                                    .blockHash(transaction.getBlockHash())
                                    .amount(EthConvert.fromWei(amount,contract.getUnit()))
                                    .toAddress(toAddress)
                                    .fromAddress(transaction.getFrom())
                                    .time(Calendar.getInstance().getTime())
                                    .blockHeight(transaction.getBlockNumber().longValue())
                                    .type(2)
                                    .build();
                            log.info("receive {} {}",recharge.getAmount(),getCoin().getUnit());
                            rechargeList.add(recharge);

                        }


                    }


                }
            }
        }
        return rechargeList;
    }

    @Override
    public Long getNetworkBlockHeight() {
        return erc20Service.height();
    }
}
