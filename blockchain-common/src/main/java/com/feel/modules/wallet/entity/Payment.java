package com.feel.modules.wallet.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web3j.crypto.Credentials;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author: zz
 * @Description:
 * @Date: 11:15 AM 3/28/21
 * @Modified By
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @ApiModelProperty(name = "txBizNumber" , value = "")
    private String txBizNumber;

    @ApiModelProperty(name = "txid" , value = "交易哈希")
    private String txid;

    @ApiModelProperty(name = "credentials" , value = "支付方钱包账户")
    private Credentials credentials;

    @ApiModelProperty(name = "to" , value = "接收地址")
    private String to;

    @ApiModelProperty(name = "amount" , value = "接收金额")
    private BigDecimal amount;

    @ApiModelProperty(name = "unit" , value = "单位")
    private String unit;

    @ApiModelProperty(name = "gasLimit" , value = "gas上限")
    private BigInteger gasLimit;

    @ApiModelProperty(name = "gasPrice" , value = "gas价格")
    private BigInteger gasPrice;
}
