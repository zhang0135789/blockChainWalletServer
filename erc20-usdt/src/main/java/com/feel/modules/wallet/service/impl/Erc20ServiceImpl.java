package com.feel.modules.wallet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.feel.common.utils.EtherApiUtils;
import com.feel.modules.wallet.entiry.Payment;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Contract;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.Erc20Service;
import com.feel.modules.wallet.utils.EthConvert;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:30 PM 3/15/21
 * @Modified By
 */
@Service
@Slf4j
public class Erc20ServiceImpl implements Erc20Service {


    @Autowired
    private Coin coin;
    @Autowired
    private Web3j web3j;
    @Autowired
    private Contract contract;
    @Autowired
    private JsonRpcHttpClient jsonRpcHttpClient;
    @Autowired
    private AccountService accountService;
    @Autowired(required = false)
    private EtherApiUtils etherApiUtils;

    /**
     * 创建地址
     * @param accountName
     * @return
     */
    @Override
    public String createNewAddress(String accountName) throws Exception {

        String fileName = WalletUtils.generateNewWalletFile("", new File(coin.getKeystorePath()), true);
        Credentials credentials = WalletUtils.loadCredentials("", coin.getKeystorePath() + "/" + fileName);
        String newAddress = credentials.getAddress();
        log.info("new address [{}]" , newAddress);
        return newAddress;
    }

    /**
     * 区块高度
     * @return
     */
    @Override
    public Long height() {
        Long height;
        try {
            EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
            height = blockNumber.getBlockNumber().longValue();
        } catch (IOException e) {
            height = 0l;
        }
        log.info("block height [{}]" , height);
        return height;
    }

    /**
     * 获取erc20-usdt 地址总资产
     * @param address
     * @return
     */
    @Override
    public BigDecimal getBalance(String address) {
        BigInteger balance = BigInteger.ZERO;
        Function fn = new Function(
                "balanceOf",
                Arrays.<Type>asList(new Address(address)),
                Collections.<TypeReference<?>>emptyList()
        );
        String data = FunctionEncoder.encode(fn);
        Map<String, String> map = new HashMap<String, String>();
        map.put("to", contract.getAddress());
        map.put("data", data);

        try {
            String methodName = "eth_call";
            Object[] params = new Object[]{map, "latest"};
            String result = jsonRpcHttpClient.invoke(methodName, params, Object.class).toString();
            if (StringUtils.isNotEmpty(result)) {
                if ("0x".equalsIgnoreCase(result) || result.length() == 2) {
                    result = "0x0";
                }
                balance = Numeric.decodeQuantity(result);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log.info("查询接口ERROR");
        }

        log.info("erc20-usdt balance : address[{}],balance[{}]" , address , balance);
        return new BigDecimal(balance);
    }

    /**
     * 交易 erc20-usdt
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    @Override
    public String transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Exception {
        log.info("transfer From Address:from={},to={},amount={},fee={}",from,to, amount, fee);
        if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
            fee = getMinerFee(contract.getGasLimit());
        }
        if(getEthBalance(from).compareTo(fee) < 0){
            log.info("地址[{}]手续费不足，最低为[{}ETH]",from,fee);
            throw new RuntimeException("手续费不足");
        }

        String txid = transferToken(from,to,amount,true);




        log.info("erc20-usdt transfer : txid[{}]" , txid);
        return txid;
    }

    /**
     * erc20-usdt交易
     * @param from
     * @param to
     * @param amount
     * @param sync
     * @return
     */
    private String transferToken(String from, String to, BigDecimal amount, boolean sync) {
        Account account = accountService.findByAddress(from);
        Credentials credentials;
        try {
            credentials = WalletUtils.loadCredentials("", coin.getKeystorePath() + "/" + account.getWalletFile());
        }catch (IOException e) {
            log.info("私钥文件不存在",e);
            throw new RuntimeException("私钥文件不存在");
        }catch (CipherException e) {
            log.info("解密失败，密码不正确",e);
            throw new RuntimeException("解密失败，密码不正确");
        }

        return handleTransferToken(credentials, to , amount);
    }

    /**
     * 执行usdt交易
     * @param credentials
     * @param to
     * @param amount
     * @return
     */
    private String handleTransferToken(Credentials credentials, String to, BigDecimal amount) {
        Payment payment = Payment.builder()
                .credentials(credentials)
                .amount(amount)
                .to(to)
                .unit(coin.getUnit())
                .build();
        return handlePayment(payment);
    }

    /**
     * 执行 payment交易
     * @param payment
     * @return
     */
    private String handlePayment(Payment payment) {
        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(payment.getCredentials().getAddress(), DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger gasPrice = getGasPrice();
            BigInteger value = EthConvert.toWei(payment.getAmount(), contract.getUnit()).toBigInteger();
            Function fn = new Function("transfer", Arrays.asList(new Address(payment.getTo()), new Uint256(value)), Collections.<TypeReference<?>> emptyList());
            String data = FunctionEncoder.encode(fn);
            BigInteger maxGas = contract.getGasLimit();
            log.info("from={},value={},gasPrice={},gasLimit={},nonce={},address={}",payment.getCredentials().getAddress(), value, gasPrice, maxGas, nonce,payment.getTo());
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce, gasPrice, maxGas, contract.getAddress(), data);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, payment.getCredentials());
            String hexValue = Numeric.toHexString(signedMessage);
            log.info("hexRawValue={}",hexValue);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String txid = ethSendTransaction.getTransactionHash();
            log.info("txid:" + txid);
            if (StringUtils.isEmpty(txid)) {
                throw new RuntimeException("发送交易失败");
            } else {
                if(ObjectUtil.isNotNull(etherApiUtils)){
                    log.info("=====发送Etherscan广播交易======");
                    etherApiUtils.sendRawTransaction(hexValue);
                }
                return txid;
            }
        } catch (Exception e) {
            log.error("交易失败" , e);
            throw new RuntimeException("交易失败,error");
        }
    }


    @Override
    public BigDecimal getMinerFee(BigInteger gasLimit) throws IOException {
        BigDecimal fee = new BigDecimal(getGasPrice().multiply(gasLimit));
        return Convert.fromWei(fee, Convert.Unit.ETHER);
    }


    /**
     * 获取账户以太坊金额
     * @param address
     * @return
     * @throws Exception
     */
    @Override
    public BigDecimal getEthBalance(String address) throws Exception {
        EthGetBalance getBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        return Convert.fromWei(getBalance.getBalance().toString(), Convert.Unit.ETHER);
    }


    /**
     * 获取gas费
     * @return
     * @throws IOException
     */
    public BigInteger getGasPrice() throws IOException {
        EthGasPrice gasPrice = web3j.ethGasPrice().send();
        BigInteger baseGasPrice =  gasPrice.getGasPrice();
        return new BigDecimal(baseGasPrice).multiply(coin.getGasSpeedUp()).toBigInteger();
    }

}
