package com.feel.modules.wallet.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @Author: zz
 * @Description: 默认扫快规则
 * @Date: 9:51 AM 3/25/21
 * @Modified By
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanRule implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "initBlockHeight" , value = "开始扫快高度,latest:从最新区块开始" )
    private String initBlockHeight = "latest";

    @ApiModelProperty(name = "interval" , value = "扫快间隔时间" )
    private Long interval = 5000L;

    @ApiModelProperty(name = "step" , value = "每次扫描区块数量" )
    private int step = 5;

    @ApiModelProperty(name = "confirmation" , value = "区块确认次数" )
    private int confirmation = 3;
}
