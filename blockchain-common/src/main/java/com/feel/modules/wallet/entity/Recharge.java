package com.feel.modules.wallet.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zz
 * @Description: 充值实体
 * @Date: 11:07 PM 3/19/21
 * @Modified By
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recharge implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "txid" , value = "交易hash")
    private String txid;

    @ApiModelProperty(name = "blockHash" , value = "区块hash")
    private String blockHash;

    @ApiModelProperty(name = "blockHeight" , value = "区块高度")
    private Long blockHeight;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "time" , value = "时间")
    private Date time;

    @ApiModelProperty(name = "amount" , value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(name = "fromAddress" , value = "发送地址")
    private String fromAddress;

    @ApiModelProperty(name = "toAddress" , value = "收款地址")
    private String toAddress;

    @ApiModelProperty(name = "status" , value = "状态 0:收到冲值 1")
    private int status = 0;

    @ApiModelProperty(name = "tpye" , value = "类型 1:trc20-usdt 2.erc20-usdt")
    private int type;
}
