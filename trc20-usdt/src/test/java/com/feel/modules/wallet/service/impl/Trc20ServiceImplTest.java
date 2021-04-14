package com.feel.modules.wallet.service.impl;



import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.feel.common.utils.TrxUtils;
import com.feel.modules.wallet.entity.Coin;

import com.feel.modules.wallet.service.Trc20Service;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.tomcat.util.descriptor.web.JspConfigDescriptorImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tron.common.utils.ByteArray;
import org.tron.walletserver.WalletApi;


import javax.annotation.Resource;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class Trc20ServiceImplTest {

    @Autowired
    private Coin coin;
    @Resource
    private Trc20Service trc20Service;

    @Test
    void createNewAddress() throws Exception {
      //  Map<String, String> map = TronUtils.createAddress();
        //{"privateKey":"7a2195d52c42c34a8de11633de7fdfbbf6883d2e95918ccd845230629fd95768",
        // "address":"TA1gLs6FS8eik5NJqjvm73L4qRqWDmLwmh",
        // "hexAddress":"410077a7caa7efe71a5e8ef1fe9ee697c8e755eff6"}
      //  System.out.println(JSONObject.toJSONString(map));
       BigDecimal str = trc20Service.getBalance("TVYAi4N2rt6V9RovxTcaF34o6fwTwqq2Nw");
        System.out.println(str);
    }

    @Test
    void height() throws IOException {
        String url =  coin.getRpc() + "/wallet/getnowblock";
        //String url = Constant.tronUrl + "/wallet/gettransactioninfobyblocknum";
        String res = HttpUtil.get(url);
        System.out.println(res);
        JSONObject jsonObject = JSON.parseObject(res);
        Integer i = jsonObject.getJSONObject("block_header").getJSONObject("raw_data").getInteger("number");
        System.out.println(i);



    }

    @Test
    void contractAddress() throws IOException {
//        String url = "Constant.tronUrl "+ "/v1/contracts/"+"Constant.contract"+"/transactions";
//        JSONObject param = new JSONObject();
//        param.put("only_confirmed",true);
//       // param.put("min_block_timestamp",1616901934440L);
//
//        String result =HttpUtil.get(url, param);
//        System.out.println(result);

    }

    @Test
    void format(){
        String res = WalletApi.encode58Check(ByteArray.fromHexString("62a1446c60b8cff32415aa1982c32ca88daff5bb"));
        System.out.println(res);
    }
    @Test
    void trx() throws Exception {
        String str ="f6YmQQvzMyGydgQ4PzXtoR8Dxzu5m12jWMlaqmIzu/BoNhGOCpQdiH6KpasgaUPo3B/tUyywLR5/1G3qNV3BrcunqQFYzedKXSxWxxsUjpw=";
        System.out.println(TrxUtils.decrypt("TVZUmXwRjJiRKvzuGVQVPzgYxXmWDZH5MXe121212",str));
    }


}
