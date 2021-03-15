package com.feel.common.utils;

import com.savl.bitcoin.rpc.client.BitcoinJSONRPCClient;

import java.net.MalformedURLException;

/**
 * @Author: zz
 * @Description: 比特币节点对接工具
 * @Date: 4:38 PM 3/15/21
 * @Modified By
 */
public class BitcoinRpcClient extends BitcoinJSONRPCClient {

    /**
     * 初始化BitcoinJsonRpcClient
     * @param url 节点url
     * @throws MalformedURLException
     */
    public BitcoinRpcClient(String url) throws MalformedURLException {
        super(url);
    }



}
