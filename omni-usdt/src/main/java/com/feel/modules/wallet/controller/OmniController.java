package com.feel.modules.wallet.controller;

import com.feel.modules.wallet.service.OmniService;
import com.feel.modules.wallet.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zz
 * @Description:
 * @Date: 3:24 PM 3/15/21
 * @Modified By
 */
@RestController
@RequestMapping("/rpc")
public class OmniController extends WalletController<OmniService>{


    @Override
    @GetMapping("/getNewAddress")
    @ApiOperation("获取新地址")
    public R<String> getNewAddress(String accountName) {
        String newAddress = walletService.createNewAddress(accountName);
        return R.ok(newAddress);
    }

    @Override
    @GetMapping("/height")
    @ApiOperation("获取区块高度")
    public R<Integer> getBlockHeight() {
        Integer height = walletService.height();
        return R.ok(height);
    }
}
