package com.feel.modules.wallet.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coin {

    private String name;

    private String unit;

    private String rpc;

    private String keystorePath;

    private BigDecimal defaultMinerFee;

    private String withdrawAddress;

    private String withdrawWallet;

    private String withdrawWalletPassword;

    private BigDecimal minCollectAmount;

    private BigInteger gasLimit;

    private BigDecimal gasSpeedUp = BigDecimal.ONE;

    private BigDecimal rechargeMinerFee;

    private String ignoreFromAddress;

    private String masterAddress;

}
