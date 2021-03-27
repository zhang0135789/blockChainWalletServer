package com.feel.modules.wallet.entity;

import com.feel.modules.wallet.utils.EthConvert;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.math.BigInteger;

/**
 * @Author: zz
 * @Description: 合约实体
 * @Date: 10:03 PM 3/27/21
 * @Modified By
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "decimals" , value = "合约精度")
    private String decimals;

    @ApiModelProperty(name = "address" , value = "合约地址")
    private String address;

    @ApiModelProperty(name = "gasLimit" , value = "gas上限")
    private BigInteger gasLimit;

    @ApiModelProperty(name = "eventTopic0" , value = "eventTopic")
    private String eventTopic0;

    public EthConvert.Unit getUnit(){
        if(StringUtils.isEmpty(decimals))return EthConvert.Unit.ETHER;
        else return EthConvert.Unit.fromString(decimals);
    }
}
