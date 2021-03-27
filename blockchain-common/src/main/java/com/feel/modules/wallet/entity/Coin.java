package com.feel.modules.wallet.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ApiModelProperty(name = "withdrawAddress" , value = "")
    private String withdrawAddress;

    @ApiModelProperty(name = "withdrawWallet" , value = "")
    private String withdrawWallet;

    @ApiModelProperty(name = "withdrawWalletPassword" , value = "")
    private String withdrawWalletPassword;

    @ApiModelProperty(name = "minCollectAmount" , value = "")
    private BigDecimal minCollectAmount;

    @ApiModelProperty(name = "gasLimit" , value = "")
    private BigInteger gasLimit;

    @ApiModelProperty(name = "gasSpeedUp" , value = "")
    private BigDecimal gasSpeedUp = BigDecimal.ONE;

    @ApiModelProperty(name = "rechargeMinerFee" , value = "")
    private BigDecimal rechargeMinerFee;

    @ApiModelProperty(name = "ignoreFromAddress" , value = "")
    private String ignoreFromAddress;

    @ApiModelProperty(name = "masterAddress" , value = "")
    private String masterAddress;

}
