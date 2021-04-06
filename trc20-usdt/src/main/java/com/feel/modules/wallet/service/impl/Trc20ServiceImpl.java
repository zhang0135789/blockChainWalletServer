package com.feel.modules.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feel.common.utils.TrxUtils;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.Trc20Service;
import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Auther: little liu
 * @Date: 2020/09/03/16:06
 * @Description:
 */
@Service
@Slf4j
public class Trc20ServiceImpl implements Trc20Service{



    @Autowired
    private Coin coin;
    @Autowired
    private Contract contract;


    @Autowired
    private AccountService accountService;



    /**
     * 创建用户钱包地址
     **/
    @Override
    public Account createNewAddress(String accountName) throws Exception {
//        String url = Constant.tronUrl + "/wallet/generateaddress";
        SignInterface sign = SignUtils.getGeneratedRandomSign(Utils.getRandom(), true);
        byte[] priKey = sign.getPrivateKey();
        byte[] address = sign.getAddress();
        String priKeyStr = Hex.encodeHexString(priKey);
        String newAddress = WalletApi.encode58Check(address);
        String hexString = ByteArray.toHexString(address);

//        JSONObject jsonAddress = new JSONObject();
//        jsonAddress.put("address", newAddress);
//        jsonAddress.put("hexAddress", hexString);
//        jsonAddress.put("privateKey", priKeyStr);
//        jsonAddress.put("account", accountName);
//        try{
//            jsonAddress.put("walletFile", TrxUtils.encrypt(newAddress+accountName,priKeyStr));
//        }catch (Exception e){}

        String walletFile = TrxUtils.encrypt(newAddress + accountName , priKeyStr);

        Account account = Account.builder()
                .account(accountName)
                .address(newAddress)
                .walletFile(walletFile)
                .createDate(new Date())
                .build();
        account = accountService.saveByName(account , "TRON");

        return account;
    }


    /**
     * 获取TRX地址余额
     *
     * @param address
     * @return
     */
    public  String getAccount(String address) {
        String url = coin.getRpc() + "/wallet/getaccount";
        Map<String, Object> map = new HashMap<>();
        map.put("address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(address)));
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }

    /**
     * 获取合约地址余额
     *
     * @param contractAddress  币种
     * @param address 地址
     * @return
     */
    public  String getTrc20Account(String contractAddress, String address) {
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
     * trc20 转账
     *
     * @param contractAddress    代币地址
     * @param toAddress 地址
     * @param amount    数量
     * @return
     */
    public  String trc20Transaction(String contractAddress,String fromAddress, String toAddress, BigDecimal amount) {
        //发起交易
        String url = coin.getRpc() + "/wallet/triggersmartcontract";

        Map<String, Object> map = new HashMap<>();

        String to_address = ByteArray.toHexString(WalletApi.decodeFromBase58Check(toAddress));
        to_address = TrxUtils.addZeroForNum(to_address, 64);
        //amount = amount.multiply(new BigDecimal(1 + TrxUtils.getSeqNumByLong(0L, weiMap.get(symbol))));
        amount = amount.multiply(contract.getDecimal());
        String uint256 = TrxUtils.addZeroForNum(amount.toBigInteger().toString(16), 64);

        map.put("owner_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(fromAddress)));
        map.put("contract_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(contract.getAddress())));
        map.put("function_selector", "transfer(address,uint256)");
        map.put("parameter", to_address + uint256);
        map.put("call_value", 0);
        //map.put("fee_limit", coin.getGasLimit());
        String param = JSON.toJSONString(map);
        ResponseEntity<String> stringResponseEntity = postForEntity(url, param);
        return signAndBroadcast(JSON.parseObject(stringResponseEntity.getBody()).getString("transaction"), getPrivateKey(fromAddress));
    }



    /**
     * 签名广播
     *
     * @param transaction 交易对象
     * @return
     */
    private  String signAndBroadcast(String transaction, String privateKey) {

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


        return stringResponseEntity.getBody();
    }


    /**
     * trx 转账
     *
     * @param toAddress 地址
     * @param amount    数量
     */
    @Override
    public String transfer(String from, String toAddress, BigDecimal amount, BigDecimal fee) throws Exception {
        String url = coin.getRpc() + "/wallet/easytransferbyprivate";
        Map<String, Object> map = new HashMap<>();
        map.put("privateKey", getPrivateKey(from));
        map.put("toAddress", ByteArray.toHexString(WalletApi.decodeFromBase58Check(toAddress)));
        amount = amount.multiply(contract.getDecimal());
        map.put("amount", amount.toBigInteger());
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }

    /**
    * @Description: trc体现
    * @Param:
    * @return:
    * @Author: lhp
    * @Date: 2021-04-06 14:04
    **/
    @Override
    public String withdrawTransfer(String to, BigDecimal amount, BigDecimal fee) {
        String fromAddress = coin.getWithdrawAddress();
        String contractAddress = contract.getAddress();
        return this.trc20Transaction(contractAddress,fromAddress,to,amount);
    }


    /**
     * https://cn.developers.tron.network/docs/%E4%BA%A4%E6%98%9311#%E4%BA%A4%E6%98%93%E7%A1%AE%E8%AE%A4%E6%96%B9%E6%B3%95
     * 按交易哈希查询交易
     *
     * @param txId 交易id
     * @return
     */
    public  String getTransactionById(String txId) {
        String url = coin.getRpc() + "/walletsolidity/gettransactionbyid";
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
    public  String getTransactionInfoByBlockNum(BigInteger num) {
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
    public  String getNowBlock() {
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


    public Recharge triggerSmartContract(List<String> addressList, String txId, JSONObject parseObject) {
        //log.info(parseObject.toJSONString());
        //方法参数
        String data = parseObject.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getString("data");
        // 调用者地址
        String owner_address = parseObject.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getString("owner_address");
        owner_address = WalletApi.encode58Check(ByteArray.fromHexString(owner_address));
        // 合约地址
        String contract_address = parseObject.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getString("contract_address");
        contract_address = WalletApi.encode58Check(ByteArray.fromHexString(contract_address));
        Long timestamp =  parseObject.getJSONObject("raw_data").getLong("expiration");
        Long blockHeight = parseObject.getLong("blockNumber");
        String dataStr = data.substring(8);
        List<String> strList = TrxUtils.getStrList(dataStr, 64);

        if (strList.size() != 2) {
            return null;
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
        if (! contract.getAddress().equals(contract_address)) {
            return null;
        }

        //币种
       // String symbol = contractMap.get(contract_address);
        if (StringUtils.isNotEmpty(amountStr)) {
           // amount = new BigDecimal(amountStr).divide(new BigDecimal(1 + TrxUtils.getSeqNumByLong(0L, weiMap.get(symbol))));
            amount = new BigDecimal(amountStr).divide(contract.getDecimal());
        }

        for (String address : addressList) {
            if (address.equals(to_address)) {
                System.out.println("===to_address:" + to_address + "===amount:" + amount);
                Recharge recharge = Recharge.builder()
                        .txid(txId)
                        .toAddress(to_address)
                        .fromAddress(owner_address)
                        .status(1)
                        .time(new Date(timestamp))
                        .amount(amount)
                        .blockHeight(blockHeight)
                        .build();
                return  recharge;
            }

        }

        return  null;
    }


    @Override
    public Long height() {
        JSONObject jsonObject = JSON.parseObject(getNowBlock());
        Long r = jsonObject.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");
        System.out.println(r);
        return jsonObject.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");
    }

    @Override
    public BigDecimal getTrcBalance(String address) throws IOException {
        String result = getTrc20Account( contract.getAddress(),address);
        BigDecimal amount = BigDecimal.ZERO;
        if(StringUtils.isNotEmpty(result)){
            JSONObject obj = JSONObject.parseObject(result);
            JSONArray results = obj.getJSONArray("constant_result");
            if(results != null && results.size() > 0){
                BigInteger _amount = new BigInteger(results.getString(0),16);
                amount = new BigDecimal(_amount).divide(contract.getDecimal(),6, RoundingMode.FLOOR);
            }
        }
        log.info(String.format("账号%s的balance=%s",address,amount.toString()));
        return new BigDecimal(amount.toString());
    }


    @Override
    public BigDecimal getBalance(String address) throws IOException {
       String result = getAccount(address);
        BigInteger balance = BigInteger.ZERO;
        if (!StringUtils.isEmpty(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            BigInteger b = obj.getBigInteger("balance");
            if(b != null){
                balance = b;
            }
        }
        return new BigDecimal(balance).divide(contract.getDecimal(),6, RoundingMode.FLOOR);
    }

    public   String getPrivateKey(String address){
        Account account = accountService.findByAddress(address);
        String privateKey = "";
        try{
            privateKey =  TrxUtils.decrypt(account.getAddress()+account.getAccount(),account.getWalletFile());
        }catch (Exception e){
            log.error(e.toString());
        }

        return privateKey;
    }
}


