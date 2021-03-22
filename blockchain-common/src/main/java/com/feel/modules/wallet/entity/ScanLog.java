package com.feel.modules.wallet.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:42 PM 3/22/21
 * @Modified By
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanLog {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "coinName" , value = "币种名称")
    private String coinName;

    @ApiModelProperty(name = "lastSyncHeight" , value = "最终同步高度")
    private Long lastSyncHeight;

    @ApiModelProperty(name = "updateTime" , value = "最终同步时间")
    private Date updateTime;

    @ApiModelProperty(name = "createTime" , value = "创建时间")
    private Date createTime;
}
