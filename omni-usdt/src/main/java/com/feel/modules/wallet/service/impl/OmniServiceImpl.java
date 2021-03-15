package com.feel.modules.wallet.service.impl;

import com.feel.common.utils.BitcoinRpcClient;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.service.OmniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:30 PM 3/15/21
 * @Modified By
 */
@Service
public class OmniServiceImpl implements OmniService  {


    @Autowired
    private BitcoinRpcClient bitcoinClient;

    @Autowired
    private Coin coin;


    /**
     * 创建地址
     * @param accountName
     * @return
     */
    @Override
    public String createNewAddress(String accountName) {

        return null;
    }
}
