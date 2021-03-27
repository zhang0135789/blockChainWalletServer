package com.feel.common.utils;

import java.math.BigDecimal;

public class Constant {

    public static final String shastaTestNetUrl = "https://api.shasta.trongrid.io";

    //主网
    public static final String mainNetUrl = "https://api.trongrid.io";

    public static String tronUrl = shastaTestNetUrl;

    /**
     * 合约精度
     */
    public static BigDecimal decimal = new BigDecimal("1000000");

    /**
     * trc20合约地址 这个是shasta测试网上面的一个trc20代币
     */
    public  static String contract = "TVfi96PXjv1RySUeZ39eSQqJmLnr9frroK";

    //主网？
   // private String contract = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";



}
