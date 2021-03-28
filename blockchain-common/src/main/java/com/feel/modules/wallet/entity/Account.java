package com.feel.modules.wallet.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zz
 * @Description: 地址账户
 * @Date: 2:54 PM 3/18/21
 * @Modified By
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("地址集合")
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(name = "account" , value = "账户号")
    private String account;

    @ApiModelProperty(name = "address" , value = "地址")
    private String address;

    @ApiModelProperty(name = "walletFile" , value = "私钥路径")
    private String walletFile;

    @ApiModelProperty(name = "balance" , value = "地址余额")
    private BigDecimal balance = BigDecimal.ZERO;

    @ApiModelProperty(name = "gas" , value = "地址燃料余额，对Token,USDT有用")
    private BigDecimal gas = BigDecimal.ZERO;

    @ApiModelProperty(name = "createDate" , value = "创建时间")
    private Date createDate;
}
