package com.feel.modules.wallet.job;

import cn.hutool.core.util.ObjectUtil;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.event.RechargeEvent;
import com.feel.modules.wallet.service.ScanLogService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: zz
 * @Description: 扫描区块数据
 * @Date: 1:49 PM 3/19/21
 * @Modified By
 */
@Slf4j
@Data
public abstract class ScanDataJob implements Runnable {

    @ApiModelProperty(name = "stop" , value = "")
    private boolean stop = false;

    @ApiModelProperty(name = "checkInterval" , value = "扫描间隔时间")
    private Long checkInterval = 2000L;
    @ApiModelProperty(name = "currentBlockHeight" , value = "当前扫描区块高度")
    private Long currentBlockHeight = 0L;

    @ApiModelProperty(name = "step" , value = "每次扫描区块的个数")
    private int step = 5;

    @ApiModelProperty(name = "confirmation" , value = "区块确认提交次数")
    private int confirmation = 3;

    @ApiModelProperty(name = "transactionEvent" , value = "交易记录事件")
    private RechargeEvent rechargeEvent;

    @ApiModelProperty(name = "coin" , value = "币种信息")
    private Coin coin;

    @ApiModelProperty(name = "scanLogService" , value = "扫描日志记录")
    private ScanLogService scanLogService;


    public void check(){
        //需要扫描的区块总数量
        Long networkBlockNumber = getNetworkBlockHeight() - confirmation + 1;
        if(currentBlockHeight < networkBlockNumber) {
            long startBlockNumber = currentBlockHeight + 1;
            currentBlockHeight = (networkBlockNumber - currentBlockHeight > step) ? currentBlockHeight + step : networkBlockNumber;
            log.info("start scan block fromBlock[{}] toBlock[{}]",startBlockNumber, currentBlockHeight);
            List<Recharge> rechargelist = scanBlock(startBlockNumber, currentBlockHeight);
            if(ObjectUtil.isNotEmpty(rechargelist)) {
                rechargelist.stream().forEach(recharge ->{
                    rechargeEvent.onConfirmed(recharge);
                });

            }else {
                log.info("scan error~!");
                currentBlockHeight = currentBlockHeight - 1;
            }
            //记录日志
            scanLogService.update(coin.getName(), currentBlockHeight);
        } else {
            log.info("Already last block height: {}, networkBlockHeight: {},nothing to do",currentBlockHeight, networkBlockNumber);
        }

    }


    /**
     * 扫描区块
     * @param startBlockNumber
     * @param endBlockNumber
     * @return
     */
    public abstract List scanBlock(Long startBlockNumber , Long endBlockNumber);

    /**
     * 获取当前网络区块高度
     * @return
     */
    public abstract Long getNetworkBlockHeight();


    /**
     * 开启任务
     */
    @Override
    public void run() {
        stop = false;
        long nextCheck = 0;
        while(!(Thread.interrupted() || stop)) {
            if(nextCheck <= System.currentTimeMillis()) {
                try {
                    nextCheck = System.currentTimeMillis() + checkInterval;
                    log.info("checked...");
                    check();
                } catch (Exception e) {
                    log.error(e.getMessage() , e);
                }
            }else {
                try {
                    Thread.sleep(Math.max(nextCheck - System.currentTimeMillis(), 100));
                } catch (InterruptedException ex) {
                    log.info(ex.getMessage());
                }
            }
        }

    }

}
