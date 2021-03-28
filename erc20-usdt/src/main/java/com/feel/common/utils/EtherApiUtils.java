package com.feel.common.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: zz
 * @Description:
 * @Date: 12:18 PM 3/28/21
 * @Modified By
 */
@Slf4j
public class EtherApiUtils {

    private String token;

    public void sendRawTransaction(String hexValue){
        try {
            HttpResponse<String> response =  Unirest.post("https://api.etherscan.io/api")
                    .field("module","proxy")
                    .field("action","eth_sendRawTransaction")
                    .field("hex",hexValue)
                    .field("apikey",token)
                    .asString();
            log.info("sendRawTransaction result = {}",response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
            log.error("error" , e);
        }
    }

    public boolean checkEventLog(final Long blockHeight,String address,String topic0,String txid){
        try {
            HttpResponse<String> response = Unirest.post("https://api.etherscan.io/api")
                    .field("module", "logs")
                    .field("action", "getLogs")
                    .field("fromBlock", blockHeight)
                    .field("toBlock",blockHeight)
                    .field("address",address)
                    .field("topic0",topic0)
                    .field("apikey", token)
                    .asString();
            log.info("getLogs result = {}",response.getBody());
            JSONObject result = JSONUtil.parseObj(response.getBody());
            if(result.getInt("status")==0){
                return false;
            }
            else{
                JSONArray txs = result.getJSONArray("result");
                for(int i=0;i<txs.size();i++){
                    JSONObject item = txs.getJSONObject(i);
                    if(item.getStr("transactionHash").equalsIgnoreCase(txid))return true;
                }
                return false;
            }

        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        EtherApiUtils api = new EtherApiUtils();
        String txid = "0x4d95cdb7864f4aab4a349dbd2e3f8b9db1deb0f85f85d9a8c37a677958129c97";
        boolean ret = api.checkEventLog(6030689L,"0x0b42c73446e4090a7c1db8ac00ad46a38ccbc2ac","0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",txid);
        System.out.println(ret);
    }

}
