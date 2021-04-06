package com.feel.modules.wallet.entity;

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
 * @Description: 提现
 * @Date: 2:24 PM 4/6/21
 * @Modified By
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Withdraw implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "txid" , value = "交易hash")
    private String txid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "time" , value = "时间")
    private Date time;

    @ApiModelProperty(name = "amount" , value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(name = "fromAddress" , value = "发送地址")
    private String fromAddress;

    @ApiModelProperty(name = "toAddress" , value = "收款地址")
    private String toAddress;

    @ApiModelProperty(name = "status" , value = "状态 0:提现成功 1 提现失败")
    private int status = 0;
}
