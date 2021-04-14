package com.feel.modules.wallet.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feel.common.utils.TrxUtils;
import com.feel.modules.wallet.entity.*;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.RechargeService;
import com.feel.modules.wallet.service.Trc20Service;
import com.feel.modules.wallet.service.WithdrawService;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Type;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tron.api.GrpcAPI;
import org.tron.common.crypto.SignInterface;
import org.tron.common.crypto.SignUtils;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.protos.Protocol;
import org.tron.protos.contract.BalanceContract;
import org.tron.protos.contract.SmartContractOuterClass;
import org.tron.walletserver.WalletApi;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Auther: little liu
 * @Date: 2020/09/03/16:06
 * @Description:
 */
@Service
@Slf4j
public class Trc20ServiceImpl implements Trc20Service {

    @Autowired
    private Coin coin;
    @Autowired
    private Contract contract;
    @Resource
    private AccountService accountService;
    @Resource
    private WithdrawService withdrawService;
    @Resource
    private RechargeService rechargeService;

    /**
     * 创建用户钱包地址
     **/
//    @Override
//    public Account createNewAddress(String accountName) throws Exception {
////        String url = Constant.tronUrl + "/wallet/generateaddress";
//        SignInterface sign = SignUtils.getGeneratedRandomSign(Utils.getRandom(), true);
//        byte[] priKey = sign.getPrivateKey();
//        byte[] address = sign.getAddress();
//        String priKeyStr = TrxUtils.bytesToHexString(priKey);
//        String newAddress = WalletApi.encode58Check(address);
//        String hexString = ByteArray.toHexString(address);
//
////        JSONObject jsonAddress = new JSONObject();
////        jsonAddress.put("address", newAddress);
////        jsonAddress.put("hexAddress", hexString);
////        jsonAddress.put("privateKey", priKeyStr);
////        jsonAddress.put("account", accountName);
////        try{
////            jsonAddress.put("walletFile", TrxUtils.encrypt(newAddress+accountName,priKeyStr));
////        }catch (Exception e){}
//
//        String walletFile = TrxUtils.encrypt(newAddress + accountName, priKeyStr);
//
//        Account account = Account.builder()
//                .account(accountName)
//                .address(newAddress)
//                .walletFile(walletFile)
//                .createDate(new Date())
//                .balance(BigDecimal.ZERO)
//                .gas(BigDecimal.ZERO)
//                .status(0)
//                .build();
//        account = accountService.save(account);
//
//        return account;
//    }

    @Override
    public Account createNewAddress(String accountName) throws Exception {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createStandard(accountName, ecKeyPair);
        String keystore = objectMapper.writeValueAsString(walletFile);
        WalletFile walletFile2 = objectMapper.readValue(keystore, WalletFile.class);
        ECKeyPair ecKeyPair1 = Wallet.decrypt(accountName, walletFile2);

        String priKeyStr = ecKeyPair1.getPrivateKey().toString(16);
        String newAddress = TrxUtils.fromHexAddress("41"+walletFile.getAddress());
       // String hexString = ByteArray.toHexString(address);

//        JSONObject jsonAddress = new JSONObject();
//        jsonAddress.put("address", newAddress);
//        jsonAddress.put("hexAddress", hexString);
//        jsonAddress.put("privateKey", priKeyStr);
//        jsonAddress.put("account", accountName);
//        try{
//            jsonAddress.put("walletFile", TrxUtils.encrypt(newAddress+accountName,priKeyStr));
//        }catch (Exception e){}

        String walletFile3 = TrxUtils.encrypt(newAddress + accountName, priKeyStr);

        Account account = Account.builder()
                .account(accountName)
                .address(newAddress)
                .walletFile(walletFile3)
                .createDate(new Date())
                .balance(BigDecimal.ZERO)
                .gas(BigDecimal.ZERO)
                .status(0)
                .build();
        account = accountService.save(account);

        return account;
    }

    /**
     * 激活地址
     *
     * @param address
     * @return
     */
    public  String createAccount(String address) throws InvalidProtocolBufferException, NoSuchAlgorithmException {
        //String url = coin.getRpc() + "/wallet/createaccount";
        String url = coin.getRpc()+" /wallet/createaccount";
        String res = "TBNKgZWMX2sWrPNfRF73fNBeHvrUHv7yRU";
        Map<String, Object> map = new HashMap<>();
        map.put("owner_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(res)));
        map.put("account_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(address)));
        String param = JSON.toJSONString(map);
        return signAndBroadcast(postForEntity(url, param).getBody(), getPrivateKey(res));
    }


    /**
     * 获取TRX地址余额
     *
     * @param address
     * @return
     */
    public String getAccount(String address) {
        String url = coin.getRpc() + "/wallet/getaccount";
        Map<String, Object> map = new HashMap<>();
        map.put("address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(address)));
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }

    /**
     * 获取合约地址余额
     *
     * @param contractAddress 币种
     * @param address         地址
     * @return
     */
    public String getTrc20Account(String contractAddress, String address) {
        String url = coin.getRpc() + "/wallet/triggerconstantcontract";
        Map<String, Object> map = new HashMap<>();
        address = TrxUtils.addZeroForNum(ByteArray.toHexString(WalletApi.decodeFromBase58Check(address)), 64);
        map.put("contract_address", contractAddress);
        map.put("function_selector", "balanceOf(address)");
        map.put("parameter", address);
        map.put("owner_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(address)));
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }


    /**
     * 签名广播
     *
     * @param transaction 交易对象
     * @return
     */
    private String signAndBroadcast(String transaction, String privateKey) throws InvalidProtocolBufferException, NoSuchAlgorithmException {

        //签名
        String url = coin.getRpc() + "/wallet/gettransactionsign";
        Map<String, Object> map = new HashMap<>();
        map.put("transaction", transaction);
        map.put("privateKey", privateKey);
        String param = JSON.toJSONString(map);
        ResponseEntity<String> stringResponseEntity = postForEntity(url, param);

        //广播
        url = coin.getRpc() + "/wallet/broadcasttransaction";
        stringResponseEntity = postForEntity(url, stringResponseEntity.getBody());
        JSONObject transationCompelet = JSONObject.parseObject(stringResponseEntity.getBody());
        if (transationCompelet.getBoolean("result")) {
            return transationCompelet.getString("txid");
        } else {
            log.error(String.format("签名交易失败:%s", stringResponseEntity));
            return null;
        }


    }

//    private String signAndBroadcast(String transaction, String privateKey) throws InvalidProtocolBufferException, NoSuchAlgorithmException {
//
//        //签名
//        String url = coin.getRpc() + "/wallet/gettransactionsign";
////        Map<String, Object> map = new HashMap<>();
////        map.put("transaction", transaction);
////        map.put("privateKey", privateKey);
////        String param = JSON.toJSONString(map);
////        ResponseEntity<String> stringResponseEntity = postForEntity(url, param);
////
////        //广播
////        url = coin.getRpc() + "/wallet/broadcasttransaction";
////        stringResponseEntity = postForEntity(url, stringResponseEntity.getBody());
////
////
////        return stringResponseEntity.getBody();
//        Protocol.Transaction tx = TrxUtils.packTransaction(transaction);
//
//        byte[] bytes = TrxUtils.signTransactionByte(tx.toByteArray(), ByteArray.fromHexString(privateKey));
//        String signTransation = Hex.toHexString(bytes);
//        JSONObject jsonObjectGB = new JSONObject();
//        jsonObjectGB.put("transaction", signTransation);
//        // String transationCompelet1 = HttpUtil.post(url, jsonObjectGB.toString());
//        ResponseEntity<String> stringResponseEntity = postForEntity(url, JSONObject.toJSONString(jsonObjectGB));
//        JSONObject transationCompelet = JSONObject.parseObject(stringResponseEntity.getBody());
//        if (transationCompelet.getBoolean("result")) {
//            return transationCompelet.getString("txid");
//        } else {
//            log.error(String.format("签名交易失败:%s", stringResponseEntity));
//            return null;
//        }
//
//    }


    /**
     * trx 转账
     *
     * @param toAddress 地址
     * @param amount    数量
     */
    @Override
    public String transfer(String from, String toAddress, BigDecimal amount, BigDecimal fee) throws Exception {
//        String url = coin.getRpc() + "/wallet/easytransferbyprivate";
//        Map<String, Object> map = new HashMap<>();
//        map.put("privateKey", getPrivateKey(from));
//        map.put("toAddress", ByteArray.toHexString(WalletApi.decodeFromBase58Check(toAddress)));
//        map.put("amount", amount.multiply(new BigDecimal("1000000")));
//        String param = JSON.toJSONString(map);
//        return postForEntity(url, param).getBody();
        String url = coin.getRpc() + "/wallet/createtransaction";
        JSONObject param = new JSONObject();
        param.put("owner_address", from);
        param.put("to_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(toAddress)));
        param.put("amount", amount.multiply(new BigDecimal(contract.getDecimals())));
        String txid =   signAndBroadcast(postForEntity(url, JSONObject.toJSONString(param)).getBody(), getPrivateKey(from));
//        String  txid = signAndBroadcast(JSONObject.toJSONString(_result.), getPrivateKey(from));
            // transaction.getJSONObject("raw_data").put("data", Hex.toHexString("这里是备注信息".getBytes()));


        return txid;
    }

    @Override
    public String transferToken(String from, String to, BigDecimal amount, BigDecimal fee) throws Exception {
//        //发起交易
        String url = coin.getRpc() + "/wallet/triggersmartcontract";
//
//        Map<String, Object> map = new HashMap<>();
//
//        String to_address = ByteArray.toHexString(WalletApi.decodeFromBase58Check(to));
//        to_address = TrxUtils.addZeroForNum(to_address, 64);
//        //amount = amount.multiply(new BigDecimal(1 + TrxUtils.getSeqNumByLong(0L, weiMap.get(symbol))));
//        amount = amount.multiply(contract.getDecimal());

//
//        map.put("owner_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(from)));
//        map.put("contract_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(contract.getAddress())));
//        map.put("function_selector", "transfer(address,uint256)");
//        map.put("parameter", to_address + uint256);
//        map.put("call_value", 0);
//        map.put("fee_limit", coin.getGasLimit());
//        String param = JSON.toJSONString(map);
//        ResponseEntity<String> stringResponseEntity = postForEntity(url, param);
//        return signAndBroadcast(JSON.parseObject(stringResponseEntity.getBody()).getString("transaction"), getPrivateKey(from));
        JSONObject param = new JSONObject();
        String uint256 = TrxUtils.addZeroForNum(amount.toBigInteger().toString(16), 64);
        String to_address = ByteArray.toHexString(WalletApi.decodeFromBase58Check(to));
        to_address = TrxUtils.addZeroForNum(to_address, 64);
        param.put("contract_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(contract.getAddress())));
        param.put("function_selector", "transfer(address,uint256)");


        param.put("parameter", to_address + uint256);
        param.put("owner_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(from)));
        param.put("call_value", 0);
        param.put("fee_limit", contract.getGasLimit());
        String txid =   signAndBroadcast(postForEntity(url, JSONObject.toJSONString(param)).getBody(), getPrivateKey(from));

        return txid;


    }

    @Override
    public String withdrawTransfer(String to, BigDecimal amount, BigDecimal fee) {
        String from = coin.getWithdrawAddress();
        String txid = "";
        try {
            txid = this.transferToken(from, to, amount, BigDecimal.ZERO);
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
        Withdraw withdraw = Withdraw.builder()
                .fromAddress(from)
                .toAddress(to)
                .time(new Date())
                .txid(txid)
                .amount(amount)
                .build();
        withdrawService.save(withdraw);
        return txid;

    }


    /**
     * https://cn.developers.tron.network/docs/%E4%BA%A4%E6%98%9311#%E4%BA%A4%E6%98%93%E7%A1%AE%E8%AE%A4%E6%96%B9%E6%B3%95
     * 按交易哈希查询交易
     *
     * @param txId 交易id
     * @return
     */
    public String getTransactionById(String txId) {
//        String url = coin.getRpc() + "/walletsolidity/gettransactionbyid";
        String url = coin.getRpc() + "/wallet/gettransactionbyid";
        Map<String, Object> map = new HashMap<>();
        map.put("value", txId);
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }


    /**
     * 获取特定区块的所有交易 Info 信息
     *
     * @param num 区块
     * @return
     */
    public String getTransactionInfoByBlockNum(BigInteger num) {
        String url = coin.getRpc() + "/wallet/gettransactioninfobyblocknum";
//        String url = coin.getRpc() + "/wallet/getblockbynum";
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }


    /**
     * 获取特定区块的所有交易 Info 信息
     *
     * @param num 区块
     * @return
     */
    public String getTransactionByBlockNum(Long num) {
//        String url = coin.getRpc() + "/wallet/gettransactioninfobyblocknum";
        String url = coin.getRpc() + "/wallet/getblockbynum";
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }


    /**
     * 查询最新区块
     *
     * @return
     */
    public String getNowBlock() {
        String url = coin.getRpc() + "/wallet/getnowblock";
        return getForEntity(url);
    }


    /**
     * 执行 post 请求
     *
     * @param url   url
     * @param param 请求参数
     * @return
     */
    private static ResponseEntity<String> postForEntity(String url, String param) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<>(param, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);
//        System.out.println("url:" + url + ",param:" + param + ",result:" + result.getBody());
        return result;
    }

    /**
     * 执行 get 请求
     *
     * @param url url
     * @return
     */
    private static String getForEntity(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
//        System.out.println("url:" + url + ",result:" + result.getBody());
        return result.getBody();
    }

    /**
     * 根据 txId 查询交易是否成功
     *
     * @param txId 交易id
     * @return
     */
    private boolean transactionStatus(String txId) {
        JSONObject parseObject = JSON.parseObject(getTransactionById(txId));
        if (StringUtils.isEmpty(parseObject.toJSONString())) {
            return false;
        }
        String contractRet = parseObject.getJSONArray("ret").getJSONObject(0).getString("contractRet");
        return "SUCCESS".equals(contractRet);
    }


    private void httpTransactionInfo(List<String> addressList, Long num) {
        String transactionInfoByBlockNum = getTransactionInfoByBlockNum(BigInteger.valueOf(num));
        JSONArray parseArray = JSON.parseArray(transactionInfoByBlockNum);
        if (parseArray.size() > 0) {
            parseArray.forEach(e -> {
                try {
                    String txId = JSON.parseObject(e.toString()).getString("id");
                    //判断 数据库 txId 有 就不用往下继续了

                    JSONObject parseObject = JSON.parseObject(getTransactionById(txId));
                    String contractRet = parseObject.getJSONArray("ret").getJSONObject(0).getString("contractRet");
                    //交易成功
                    if ("SUCCESS".equals(contractRet)) {
                        String type = parseObject.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getString("type");
                        if ("TriggerSmartContract".equals(type)) {
                            //合约地址转账
                            triggerSmartContract(addressList, txId, parseObject);

                        } else if ("TransferContract".equals(type)) {
                            //trx 转账
                            transferContract(parseObject);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
        }
    }


    private void rpcTransactionInfo(List<String> addressList, Long num) {
        try {
            Optional<GrpcAPI.TransactionInfoList> optional = WalletApi.getTransactionInfoByBlockNum(num);
            if (!optional.isPresent()) {
                return;
            }

            List<Protocol.TransactionInfo> transactionInfoList = optional.get().getTransactionInfoList();
            for (Protocol.TransactionInfo transactionInfo : transactionInfoList) {
                String txId = ByteArray.toHexString(transactionInfo.getId().toByteArray());
                //判断 数据库 txId 有 就不用往下继续了

                Optional<Protocol.Transaction> transaction = WalletApi.getTransactionById(txId);
                if (!transaction.isPresent()) {
                    continue;
                }

                List<Protocol.Transaction.Result> retList = transaction.get().getRetList();
                Protocol.Transaction.Result.contractResult contractRet = retList.get(0).getContractRet();
                if (!Protocol.Transaction.Result.contractResult.SUCCESS.name().equals(contractRet.name())) {
                    continue;
                }

                Protocol.Transaction.Contract.ContractType type = transaction.get().getRawData().getContract(0).getType();
                Any contractParameter = transaction.get().getRawData().getContract(0).getParameter();

                if (Protocol.Transaction.Contract.ContractType.TriggerSmartContract.name().equals(type.name())) {
                    // trc20 充值

                    SmartContractOuterClass.TriggerSmartContract deployContract = contractParameter
                            .unpack(SmartContractOuterClass.TriggerSmartContract.class);

                    String owner_address = WalletApi.encode58Check(ByteArray.fromHexString(ByteArray.toHexString(deployContract.getOwnerAddress().toByteArray())));
                    String contract_address = WalletApi.encode58Check(ByteArray.fromHexString(ByteArray.toHexString(deployContract.getContractAddress().toByteArray())));

                    String dataStr = ByteArray.toHexString(deployContract.getData().toByteArray()).substring(8);
                    List<String> strList = TrxUtils.getStrList(dataStr, 64);
                    if (strList.size() != 2) {
                        continue;
                    }

                    String to_address = TrxUtils.delZeroForNum(strList.get(0));
                    if (!to_address.startsWith("41")) {
                        to_address = "41" + to_address;
                    }

                    to_address = WalletApi.encode58Check(ByteArray.fromHexString(to_address));

                    String amountStr = TrxUtils.delZeroForNum(strList.get(1));
                    if (amountStr.length() > 0) {
                        amountStr = new BigInteger(amountStr, 16).toString(10);
                    }

                    BigDecimal amount = BigDecimal.ZERO;
                    //相匹配的合约地址
//                    if (!contractMap.containsKey(contract_address)) {
//                        continue;
//                    }
                    //合约币种
                    //  String symbol = contractMap.get(contract_address);
                    if (StringUtils.isNotEmpty(amountStr)) {
                        amount = new BigDecimal(amountStr).divide(contract.getDecimal());
                    }
                    for (String address : addressList) {
                        if (address.equals(to_address)) {
                            System.out.println("===to_address:" + to_address + "===amount:" + amount);
                        }
                    }

                } else if (Protocol.Transaction.Contract.ContractType.TransferContract.name().equals(type.name())) {
                    // trx 充值
                    BalanceContract.TransferContract deployContract = contractParameter
                            .unpack(BalanceContract.TransferContract.class);
                    String owner_address = WalletApi.encode58Check(ByteArray.fromHexString(ByteArray.toHexString(deployContract.getOwnerAddress().toByteArray())));
                    String to_address = WalletApi.encode58Check(ByteArray.fromHexString(ByteArray.toHexString(deployContract.getToAddress().toByteArray())));
                    BigDecimal amount = new BigDecimal(deployContract.getAmount());
                    amount = amount.divide(contract.getDecimal());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void transferContract(JSONObject parseObject) {
        //数量
        BigDecimal amount = parseObject.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getBigDecimal("amount");

        //调用者地址
        String owner_address = parseObject.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getString("owner_address");
        owner_address = WalletApi.encode58Check(ByteArray.fromHexString(owner_address));

        //转入地址
        String to_address = parseObject.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getString("to_address");
        to_address = WalletApi.encode58Check(ByteArray.fromHexString(to_address));

        amount = amount.divide(contract.getDecimal());


    }

    public Recharge triggerSmartContract(List<String> addressList, String txId, JSONObject parseObject) {
        String ownerAddress = TrxUtils.getOwnerAddress(parseObject);
        Long timestamp = parseObject.getJSONObject("raw_data").getLong("expiration");
        Long blockHeight = parseObject.getLong("blockNumber");
       String to_address = TrxUtils.getToAddress(parseObject);
       BigDecimal amount = BigDecimal.ZERO;
        String amountStr = TrxUtils.getAmountStr(parseObject);
        if (StringUtils.isNotEmpty(amountStr)) {
            amount = new BigDecimal(amountStr).divide(contract.getDecimal(),6,RoundingMode.HALF_UP);
        }

        for (String address : addressList) {
            if (address.equals(to_address)) {
                log.info("===to_address:" + to_address + "===amount:" + amount);
                Recharge recharge = Recharge.builder()
                        .txid(txId)
                        .toAddress(to_address)
                        .fromAddress(ownerAddress)
                        .status(1)
                        .time(new Date(timestamp))
                        .amount(amount)
                        .blockHeight(blockHeight)
                        .build();
                return recharge;
            }

        }

        return null;
    }


    @Override
    public Long height() {
        JSONObject jsonObject = JSON.parseObject(getNowBlock());
        Long r = jsonObject.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");
        System.out.println(r);
        return jsonObject.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");
    }


    @Override
    public BigDecimal getBalance(String address) throws IOException {
        String result = getAccount(address);
        BigInteger balance = BigInteger.ZERO;
        if (!StringUtils.isEmpty(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            BigInteger b = obj.getBigInteger("balance");
            if (b != null) {
                balance = b;
            }
        }
        return new BigDecimal(balance).divide(contract.getDecimal(), 6, RoundingMode.FLOOR);
    }

    @Override
    public BigDecimal getTokenBalance(String address) throws Exception {
        String result = getTrc20Account(contract.getAddress(), address);
        BigDecimal amount = BigDecimal.ZERO;
        if (StringUtils.isNotEmpty(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            JSONArray results = obj.getJSONArray("constant_result");
            if (results != null && results.size() > 0) {
                BigInteger _amount = new BigInteger(results.getString(0), 16);
                amount = new BigDecimal(_amount).divide(contract.getDecimal(), 6, RoundingMode.FLOOR);
            }
        }
        log.info(String.format("账号%s的balance=%s", address, amount.toString()));
        return new BigDecimal(amount.toString());
    }

    public String getPrivateKey(String address) {
        Account account = accountService.findByAddress(address);
        String privateKey = "";
        try {
            privateKey = TrxUtils.decrypt(account.getAddress() + account.getAccount(), account.getWalletFile());
        } catch (Exception e) {
            log.error(e.toString());
        }

        return privateKey;
    }
}


