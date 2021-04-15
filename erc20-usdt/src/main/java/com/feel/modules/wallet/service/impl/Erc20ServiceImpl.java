package com.feel.modules.wallet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.feel.common.utils.EtherApiUtils;
import com.feel.modules.wallet.entity.*;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.service.Erc20Service;
import com.feel.modules.wallet.service.WithdrawService;
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
import java.util.*;

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

    @Autowired
    private WithdrawService withdrawService;

    @Autowired(required = false)
    private EtherApiUtils etherApiUtils;

    /**
     * 创建地址
     * @param accountName
     * @return
     */
    @Override
    public Account createNewAddress(String accountName) throws Exception {
        String fileName = WalletUtils.generateNewWalletFile("", new File(coin.getKeystorePath()), true);
        Credentials credentials = WalletUtils.loadCredentials("", coin.getKeystorePath() + "/" + fileName);
        String newAddress = credentials.getAddress();
        log.info("new address [{}]" , newAddress);
        Account account = Account.builder()
                .account(accountName)
                .address(newAddress)
                .walletFile(fileName)
                .createDate(new Date())
                .status(0)
                .balance(BigDecimal.ZERO)
                .gas(BigDecimal.ZERO)
                .build();
        account = accountService.save(account);
        return account;
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
     * 获取地址总资产-eth
     * @param address
     * @return
     */
    @Override
    public BigDecimal getBalance(String address) throws IOException {
        EthGetBalance getBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        return Convert.fromWei(getBalance.getBalance().toString(), Convert.Unit.ETHER);
    }

    /**
     * 获取地址总资产-usdt
     * @param address
     * @return
     * @throws Exception
     */
    @Override
    public BigDecimal getTokenBalance(String address) throws Exception {
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

        BigDecimal amount = EthConvert.fromWei(new BigDecimal(balance), contract.getUnit());
        log.info("erc20-usdt balance : address[{}],balance[{}]" , address , amount);

        return amount;
    }

    /**
     * 交易 eth
     * @param from
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    @Override
    public String transfer(String from, String to, BigDecimal amount, BigDecimal fee) throws Exception {
        return transferEth(coin.getKeystorePath() + "/" + coin.getWithdrawWallet(), coin.getWithdrawWalletPassword(), to, amount, true,"");
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
    public String transferToken(String from, String to, BigDecimal amount, BigDecimal fee) throws Exception {
        log.info("transfer From Address:from={},to={},amount={},fee={}",from,to, amount, fee);
        if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
            fee = getMinerFee(coin.getGasLimit());
        }
        if(getBalance(from).compareTo(fee) < 0){
            log.info("地址[{}]手续费不足，最低为[{}ETH]",from,fee);
            throw new RuntimeException("手续费不足");
        }

        String txid = transferToken(from,to,amount,true);

        log.info("erc20-usdt transfer : txid[{}]" , txid);
        return txid;
    }

    /**
     * 提现 -usdt
     * @param to
     * @param amount
     * @param fee
     * @return
     */
    @Override
    public String withdrawTransfer(String to, BigDecimal amount, BigDecimal fee) {
        log.info("提现: from[{}],to[{}],amount[{}]" , coin.getWithdrawAddress() , to , amount);

        Credentials credentials;
        try {
            credentials = WalletUtils.loadCredentials(
                    coin.getWithdrawWalletPassword(),
                    coin.getKeystorePath() + "/" + coin.getWithdrawWallet());
        }catch (IOException e) {
            log.info("私钥文件不存在",e);
            throw new RuntimeException("私钥文件不存在");
        }catch (CipherException e) {
            log.info("解密失败，密码不正确",e);
            throw new RuntimeException("解密失败，密码不正确");
        } catch (Exception e) {
            log.info("获取token余额失败");
            throw new RuntimeException("获取token余额失败");
        }


//        String txid = transferToken(coin.getWithdrawAddress(), to, amount, true);
        String txid = handleTransferToken(credentials, to , amount);
        Withdraw withdraw = Withdraw.builder()
                .fromAddress(coin.getWithdrawAddress())
                .toAddress(to)
                .time(new Date())
                .txid(txid)
                .build();
        withdrawService.save(withdraw);
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
        return handlePaymentToken(payment);
    }

    /**
     * 执行 payment 交易  usdt
     * @param payment
     * @return
     */
    private String handlePaymentToken(Payment payment) {
        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(payment.getCredentials().getAddress(), DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger gasPrice = getGasPrice();
            BigInteger value = EthConvert.toWei(payment.getAmount(), contract.getUnit()).toBigInteger();
            Function fn = new Function("transfer",
                    Arrays.asList(
                            new Address(payment.getTo()),
                            new Uint256(value)),
                    Collections.<TypeReference<?>> emptyList()
            );
            String data = FunctionEncoder.encode(fn);
            BigInteger maxGas = coin.getGasLimit();
            log.info("from = {}, value = {}, gasPrice = {}, gasLimit = {}, nonce = {}, address = {}",payment.getCredentials().getAddress(), value, gasPrice, maxGas, nonce,payment.getTo());
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce, gasPrice, maxGas, contract.getAddress() , data);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, payment.getCredentials());
            String hexValue = Numeric.toHexString(signedMessage);
            log.info("hexRawValue={}",hexValue);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String txid = ethSendTransaction.getTransactionHash();
            log.info("txid:" + txid);
            if (StringUtils.isEmpty(txid)) {
                log.error(ethSendTransaction.getError().getMessage());
                throw new RuntimeException("发送交易失败");
            } else {
//                if(ObjectUtil.isNotNull(etherApiUtils)){
//                    log.info("=====发送Etherscan广播交易======");
//                    etherApiUtils.sendRawTransaction(hexValue);
//                }
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
     * 发送eth
     * @param toAddress
     * @param amount
     * @param sync
     * @param withdrawId
     * @return
     */
    private String transferFromEthWithdrawWallet(String toAddress, BigDecimal amount, boolean sync, String withdrawId) {
        return transferEth(coin.getKeystorePath() + "/" + coin.getWithdrawWallet(), coin.getWithdrawWalletPassword(), toAddress, amount, sync,withdrawId);
    }

    /**
     *
     * @param walletFile
     * @param password
     * @param toAddress
     * @param amount
     * @param sync
     * @param withdrawId
     * @return
     */
    private String transferEth(String walletFile, String password, String toAddress, BigDecimal amount, boolean sync, String withdrawId) {
        Credentials credentials;
        try {
            credentials = WalletUtils.loadCredentials(password, walletFile);
        } catch (IOException e) {
            log.error("钱包文件不存在",e);
            return null;
        } catch (CipherException e) {
            log.error("解密失败，密码不正确",e);
            return null;
        }
        return handletransferEth(credentials, toAddress, amount);
    }

    /**
     *
     * @param credentials
     * @param toAddress
     * @param amount
     * @return
     */
    private String handletransferEth(Credentials credentials, String toAddress, BigDecimal amount) {
        Payment payment = Payment.builder()
                .credentials(credentials)
                .amount(amount)
                .to(toAddress)
                .unit("ETH")
                .build();
        return handlePaymentEth(payment);
    }

    private String handlePaymentEth(Payment payment) {
        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(payment.getCredentials().getAddress(), DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger gasPrice = getGasPrice();
            BigInteger value = Convert.toWei(payment.getAmount(), Convert.Unit.ETHER).toBigInteger();

            BigInteger maxGas = coin.getGasLimit();
            log.info("value={},gasPrice={},gasLimit={},nonce={},address={}", value, gasPrice, maxGas, nonce, payment.getTo());
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce, gasPrice, maxGas, payment.getTo(), value);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, payment.getCredentials());
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String txid = ethSendTransaction.getTransactionHash();
            log.info("txid = {}", txid);
            if (StringUtils.isEmpty(txid)) {
                log.error("发送交易失败");
                return null;
            } else {
//                if(etherApiUtils != null){
//                    log.info("=====发送Etherscan广播交易======");
//                    etherApiUtils.sendRawTransaction(hexValue);
//                }
                return txid;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("eth 交易失败,error",e);
            return null;
        }
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
