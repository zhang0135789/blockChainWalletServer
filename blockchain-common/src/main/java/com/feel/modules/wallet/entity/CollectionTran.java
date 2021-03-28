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
 * @Description: 归集记录
 * @Date: 7:44 PM 3/28/21
 * @Modified By
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionTran implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "txid" , value = "交易hash")
    private String txid;

    @ApiModelProperty(name = "amount" , value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(name = "fromAddress" , value = "发送地址")
    private String fromAddress;

    @ApiModelProperty(name = "toAddress" , value = "收款地址")
    private String toAddress;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "time" , value = "时间")
    private Date time;


}
