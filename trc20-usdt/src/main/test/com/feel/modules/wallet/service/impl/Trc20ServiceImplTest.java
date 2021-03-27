package com.feel.modules.wallet.service.impl;



import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.tron.utils.TronUtils;

import java.util.Map;

class Trc20ServiceImplTest {

    @Test
    void createNewAddress() {
        Map<String, String> map = TronUtils.createAddress();
        //{"privateKey":"7a2195d52c42c34a8de11633de7fdfbbf6883d2e95918ccd845230629fd95768",
        // "address":"TA1gLs6FS8eik5NJqjvm73L4qRqWDmLwmh",
        // "hexAddress":"410077a7caa7efe71a5e8ef1fe9ee697c8e755eff6"}
        System.out.println(JSONObject.toJSONString(map));
    }
}