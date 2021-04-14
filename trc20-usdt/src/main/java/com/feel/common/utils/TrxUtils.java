package com.feel.common.utils;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.service.AccountService;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.JsonFormat;
import org.tron.protos.Protocol;
import org.tron.protos.contract.*;
import org.tron.walletserver.WalletApi;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: little liu
 * @Date: 2020/09/03/16:03
 * @Description:
 */
public class TrxUtils {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";


    /**
     * 长度不够前面补0
     *
     * @param str
     * @param strLength
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    public static String delZeroForNum(String str) {
        return str.replaceAll("^(0+)", "");
    }

    public static String getSeqNumByLong(Long l, int bitCount) {
        return String.format("%0" + bitCount + "d", l);
    }


    /**
     * 字符串转换为16进制字符串
     *
     * @param s
     * @return
     */
    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * byte数组转16进制字符串
     *
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    /**
     * @param: [hex]
     * @return: int
     * @description: 按位计算，位值乘权重
     */
    public static int hexToDecimal(String hex) {
        int outcome = 0;
        for (int i = 0; i < hex.length(); i++) {
            char hexChar = hex.charAt(i);
            outcome = outcome * 16 + charToDecimal(hexChar);
        }
        return outcome;
    }

    /**
     * @param: [c]
     * @return: int
     * @description:将字符转化为数字
     */
    public static int charToDecimal(char c) {
        if (c >= 'A' && c <= 'F')
            return 10 + c - 'A';
        else
            return c - '0';
    }




    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString
     *            原始字符串
     * @param length
     *            指定长度
     * @return
     */
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString
     *            原始字符串
     * @param length
     *            指定长度
     * @param size
     *            指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length,
                                          int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str
     *            原始字符串
     * @param f
     *            开始位置
     * @param t
     *            结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }


    public static String encrypt(String passwd, String content) throws Exception {
        // 创建密码器
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

        byte[] byteContent = content.getBytes("utf-8");

        // 初始化为加密模式的密码器
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(passwd));

        // 加密
        byte[] result = cipher.doFinal(byteContent);

        //通过Base64转码返回
        return Base64.encodeBase64String(result);
    }

    public static String decrypt(String passwd, String encrypted) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(passwd));

        //执行操作
        byte[] result = cipher.doFinal(Base64.decodeBase64(encrypted));

        return new String(result, "utf-8");
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        // javax.crypto.BadPaddingException: Given final block not properly padded解决方案
        // https://www.cnblogs.com/zempty/p/4318902.html - 用此法解决的
        // https://www.cnblogs.com/digdeep/p/5580244.html - 留作参考吧
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        //AES 要求密钥长度为 128
        kg.init(128, random);

        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        // 转换为AES专用密钥
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

    /**
     * 报装成transaction
     *
     * @param strTransaction
     * @return
     */
    public static Protocol.Transaction packTransaction(String strTransaction) {
        JSONObject jsonTransaction = JSONObject.parseObject(strTransaction);
        JSONObject rawData = jsonTransaction.getJSONObject("raw_data");
        JSONArray contracts = new JSONArray();
        JSONArray rawContractArray = rawData.getJSONArray("contract");
        for (int i = 0; i < rawContractArray.size(); i++) {
            try {
                JSONObject contract = rawContractArray.getJSONObject(i);
                JSONObject parameter = contract.getJSONObject("parameter");
                String contractType = contract.getString("type");
                Any any = null;
                switch (contractType) {
                    case "AccountCreateContract":
                        AccountContract.AccountCreateContract.Builder accountCreateContractBuilder = AccountContract.AccountCreateContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                accountCreateContractBuilder);
                        any = Any.pack(accountCreateContractBuilder.build());
                        break;
                    case "TransferContract":
                        BalanceContract.TransferContract.Builder transferContractBuilder = BalanceContract.TransferContract.newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject("value").toString(), transferContractBuilder);
                        any = Any.pack(transferContractBuilder.build());
                        break;
                    case "TransferAssetContract":
                        AssetIssueContractOuterClass.TransferAssetContract.Builder transferAssetContractBuilder = AssetIssueContractOuterClass.TransferAssetContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                transferAssetContractBuilder);
                        any = Any.pack(transferAssetContractBuilder.build());
                        break;
                    case "VoteAssetContract":
                        VoteAssetContractOuterClass.VoteAssetContract.Builder voteAssetContractBuilder = VoteAssetContractOuterClass.VoteAssetContract.newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject("value").toString(), voteAssetContractBuilder);
                        any = Any.pack(voteAssetContractBuilder.build());
                        break;
                    case "VoteWitnessContract":
                        WitnessContract.VoteWitnessContract.Builder voteWitnessContractBuilder = WitnessContract.VoteWitnessContract
                                .newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject("value").toString(), voteWitnessContractBuilder);
                        any = Any.pack(voteWitnessContractBuilder.build());
                        break;
                    case "WitnessCreateContract":
                        WitnessContract.WitnessCreateContract.Builder witnessCreateContractBuilder = WitnessContract.WitnessCreateContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                witnessCreateContractBuilder);
                        any = Any.pack(witnessCreateContractBuilder.build());
                        break;
                    case "AssetIssueContract":
                        AssetIssueContractOuterClass.AssetIssueContract.Builder assetIssueContractBuilder = AssetIssueContractOuterClass.AssetIssueContract.newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject("value").toString(), assetIssueContractBuilder);
                        any = Any.pack(assetIssueContractBuilder.build());
                        break;
                    case "WitnessUpdateContract":
                        WitnessContract.WitnessUpdateContract.Builder witnessUpdateContractBuilder = WitnessContract.WitnessUpdateContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                witnessUpdateContractBuilder);
                        any = Any.pack(witnessUpdateContractBuilder.build());
                        break;
                    case "ParticipateAssetIssueContract":
                        AssetIssueContractOuterClass.ParticipateAssetIssueContract.Builder participateAssetIssueContractBuilder =
                                AssetIssueContractOuterClass.ParticipateAssetIssueContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                participateAssetIssueContractBuilder);
                        any = Any.pack(participateAssetIssueContractBuilder.build());
                        break;
                    case "AccountUpdateContract":
                        AccountContract.AccountUpdateContract.Builder accountUpdateContractBuilder = AccountContract.AccountUpdateContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                accountUpdateContractBuilder);
                        any = Any.pack(accountUpdateContractBuilder.build());
                        break;
                    case "FreezeBalanceContract":
                        BalanceContract.FreezeBalanceContract.Builder freezeBalanceContractBuilder = BalanceContract.FreezeBalanceContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                freezeBalanceContractBuilder);
                        any = Any.pack(freezeBalanceContractBuilder.build());
                        break;
                    case "UnfreezeBalanceContract":
                        BalanceContract.UnfreezeBalanceContract.Builder unfreezeBalanceContractBuilder = BalanceContract.UnfreezeBalanceContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                unfreezeBalanceContractBuilder);
                        any = Any.pack(unfreezeBalanceContractBuilder.build());
                        break;
                    case "UnfreezeAssetContract":
                        AssetIssueContractOuterClass.UnfreezeAssetContract.Builder unfreezeAssetContractBuilder = AssetIssueContractOuterClass.UnfreezeAssetContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                unfreezeAssetContractBuilder);
                        any = Any.pack(unfreezeAssetContractBuilder.build());
                        break;
                    case "WithdrawBalanceContract":
                        BalanceContract.WithdrawBalanceContract.Builder withdrawBalanceContractBuilder = BalanceContract.WithdrawBalanceContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toString(),
                                withdrawBalanceContractBuilder);
                        any = Any.pack(withdrawBalanceContractBuilder.build());
                        break;
                    case "UpdateAssetContract":
                        AssetIssueContractOuterClass.UpdateAssetContract.Builder updateAssetContractBuilder = AssetIssueContractOuterClass.UpdateAssetContract
                                .newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject("value").toString(), updateAssetContractBuilder);
                        any = Any.pack(updateAssetContractBuilder.build());
                        break;
                    case "SmartContract":
                        SmartContractOuterClass.SmartContract.Builder smartContractBuilder = SmartContractOuterClass.SmartContract.newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject("value").toString(), smartContractBuilder);
                        any = Any.pack(smartContractBuilder.build());
                        break;
                    case "TriggerSmartContract":
                        SmartContractOuterClass.TriggerSmartContract.Builder triggerSmartContractBuilder = SmartContractOuterClass.TriggerSmartContract
                                .newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject("value").toString(),
                                        triggerSmartContractBuilder);
                        any = Any.pack(triggerSmartContractBuilder.build());
                        break;
                    // todo add other contract
                    default:
                }
                if (any != null) {
                    String value = Hex.toHexString(any.getValue().toByteArray());
                    parameter.put("value", value);
                    contract.put("parameter", parameter);
                    contracts.add(contract);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
        }
        rawData.put("contract", contracts);
        jsonTransaction.put("raw_data", rawData);
        Protocol.Transaction.Builder transactionBuilder = Protocol.Transaction.newBuilder();
        try {
            JsonFormat.merge(jsonTransaction.toString(), transactionBuilder);
            return transactionBuilder.build();
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 签名交易
     * @param transaction
     * @param privateKey
     * @return
     * @throws InvalidProtocolBufferException
     * @throws NoSuchAlgorithmException
     */
    public static byte[] signTransactionByte(byte[] transaction, byte[] privateKey) throws InvalidProtocolBufferException, NoSuchAlgorithmException {
        ECKey ecKey = ECKey.fromPrivate(privateKey);
        Protocol.Transaction transaction1 = Protocol.Transaction.parseFrom(transaction);
        byte[] rawdata = transaction1.getRawData().toByteArray();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(rawdata,0,rawdata.length);
        byte[] hash= digest.digest();
        byte[] sign = ecKey.sign(hash).toByteArray();
        return transaction1.toBuilder().addSignature(ByteString.copyFrom(sign)).build().toByteArray();
    }

    /**
     *  T ---->  41
     * @param address
     * @return
     */
    public static String toHexAddress(String address){
        return  ByteArray.toHexString(WalletApi.decodeFromBase58Check(address));
    }
    /**
     * 41 ---- > T
     * @param address
     * @return
     */
    public static String fromHexAddress(String address){
        return WalletApi.encode58Check(ByteArray.fromHexString(address));
    }


    public static  String getToAddress(JSONObject json){

        String dataStr = getData(json).substring(8);
        List<String> strList = TrxUtils.getStrList(dataStr, 64);

        if (strList.size() != 2) {
            return null;
        }
        String to_address = TrxUtils.delZeroForNum(strList.get(0));
        if (!to_address.startsWith("41")) {
            to_address = "41" + to_address;
        }

        to_address = WalletApi.encode58Check(ByteArray.fromHexString(to_address));

        return to_address;
    }

    public  static  String getData(JSONObject json){
        String data = json.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getString("data");
            return data;
    }

    public static String getAmountStr(JSONObject json){
        String dataStr = getData(json).substring(8);
        List<String> strList = TrxUtils.getStrList(dataStr, 64);
        if (strList.size() != 2) {
            return null;
        }
        String amountStr = TrxUtils.delZeroForNum(strList.get(1));

        if (amountStr.length() > 0) {
            amountStr = new BigInteger(amountStr, 16).toString(10);
        }

   return amountStr;
    }

    public static  String getOwnerAddress(JSONObject json){
        String owner_address = json.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getString("owner_address");
        owner_address = WalletApi.encode58Check(ByteArray.fromHexString(owner_address));
        return owner_address;
    }

    public static  String getContractAddress(JSONObject json)
    {
        String contractAddress = json.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value").getString("contract_address");
        contractAddress = WalletApi.encode58Check(ByteArray.fromHexString(contractAddress));
        return  contractAddress;
    }
}


