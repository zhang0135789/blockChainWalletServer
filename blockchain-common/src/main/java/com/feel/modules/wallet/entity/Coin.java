package com.feel.modules.wallet.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("币种信息")
public class Coin implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(name = "name" , value = "币种名称")
    private String name;

    @ApiModelProperty(name = "unit" , value = "代币名字(简称)")
    private String unit;

    @ApiModelProperty(name = "rpc" , value = "rpc节点url")
    private String rpc;

    @ApiModelProperty(name = "keystorePath" , value = "keystore存放位置")
    private String keystorePath;

    @ApiModelProperty(name = "defaultMinerFee" , value = "")
    private BigDecimal defaultMinerFee;

    @ApiModelProperty(name = "withdrawAddress" , value = "手续费地址")
    private String withdrawAddress;

    @ApiModelProperty(name = "withdrawWallet" , value = "手续费钱包")
    private String withdrawWallet;

    @ApiModelProperty(name = "withdrawWalletPassword" , value = "手续费钱包密码")
    private String withdrawWalletPassword;

    @ApiModelProperty(name = "minCollectAmount" , value = "最小归集金额")
    private BigDecimal minCollectAmount;

    @ApiModelProperty(name = "gasLimit" , value = "gas上限")
    private BigInteger gasLimit;

    @ApiModelProperty(name = "gasSpeedUp" , value = "")
    private BigDecimal gasSpeedUp = BigDecimal.ONE;

    @ApiModelProperty(name = "rechargeMinerFee" , value = "")
    private BigDecimal rechargeMinerFee;

    @ApiModelProperty(name = "ignoreFromAddress" , value = "")
    private String ignoreFromAddress;

    @ApiModelProperty(name = "masterAddress" , value = "归集主地址")
    private String masterAddress;

}
