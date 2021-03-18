package com.feel.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.feel.common.constant.Constants;
import com.savl.bitcoin.rpc.client.BitcoinJSONRPCClient;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * @Author: zz
 * @Description: 比特币节点对接工具
 * @Date: 4:38 PM 3/15/21
 * @Modified By
 */
@Slf4j
public class BitcoinRpcClient extends BitcoinJSONRPCClient {



    /**
     * 初始化BitcoinJsonRpcClient
     * @param url 节点url
     * @throws MalformedURLException
     */
    public BitcoinRpcClient(String url) throws MalformedURLException {
        super(url);
    }


    /**
     * 获取omni-usdt资产
     * @param address
     * @return
     */
    public BigDecimal getOmniBalance(String address) {
        String balance = "0";
        Integer propertyid = Integer.valueOf(Constants.PROPERTYID_USDT);
        try {
            Map<String, Object> map = (Map<String, Object>) query(
                        Constants.METHOD_getOmniBalance,
                        new Object[] { address, propertyid }
                    );
            if (CollectionUtil.isNotEmpty(map)) {
                balance = map.get("balance").toString();
        }
        } catch (Exception e) {
            log.error("get Omni Balance error" , e);
        }
        return new BigDecimal(balance);
    }
}
