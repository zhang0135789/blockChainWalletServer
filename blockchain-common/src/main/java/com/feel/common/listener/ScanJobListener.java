package com.feel.common.listener;

import cn.hutool.core.util.ObjectUtil;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.ScanLog;
import com.feel.modules.wallet.entity.ScanRule;
import com.feel.modules.wallet.event.RechargeEvent;
import com.feel.modules.wallet.job.ScanDataJob;
import com.feel.modules.wallet.service.ScanLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @Author: zz
 * @Description: 开启区块扫描任务
 *               监听COntexRefreshedEvent事件(spring容器初始化完成,会触发此事件)
 * @Date: 9:33 AM 3/25/21
 * @Modified By
 */
@Service
@Slf4j
public class ScanJobListener implements ApplicationListener<ContextRefreshedEvent> {


    @Autowired(required = false)
    private ScanDataJob scanDataJob;
    @Autowired
    private RechargeEvent rechargeEvent;
    @Autowired
    private ScanLogService scanLogService;
    @Autowired
    private Coin coin;
    @Autowired
    private ScanRule scanRule;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //当spring容器初始化完成后,启动扫块任务
        if(ObjectUtil.isNotNull(scanDataJob)) {
            log.info("====== Start Block Scan Job Now [coin: {}] ======",coin.getName());
            //获取扫描日志
            log.info("------获取扫描日志");
            ScanLog scanLog  = scanLogService.findOne(coin.getName());
            log.info("------scan log [{}]" , scanLog);
            log.info("------开始初始化扫块任务参数");
            if(ObjectUtil.isNotNull(scanLog)) {
                //设置扫块任务-起始区块高度:从历史记录获取
                scanDataJob.setCurrentBlockHeight(scanLog.getLastSyncHeight());
            } else if(scanRule.getInitBlockHeight().equalsIgnoreCase("latest")) {
                //设置扫块任务-起始区块高度:当前最新区块
                scanDataJob.setCurrentBlockHeight(scanDataJob.getCurrentBlockHeight());
            } else {
                //设置扫块任务-起始区块高度:
                scanDataJob.setCurrentBlockHeight(Long.valueOf(scanRule.getInitBlockHeight()));
            }
            //设置每次扫块个数
            scanDataJob.setStep(scanRule.getStep());
            //设置扫块间隔时间
            scanDataJob.setCheckInterval(scanRule.getInterval());
            //设置充值通知事件
            scanDataJob.setRechargeEvent(rechargeEvent);

            //设置币种信息
            scanDataJob.setCoin(coin);
            //设置扫块日志业务层
            scanDataJob.setScanLogService(scanLogService);
            //设置交易确认次数
            scanDataJob.setConfirmation(scanRule.getConfirmation());
            new Thread(scanDataJob).start();
            log.info("===== Start Block Scan Job Success ======");
        } else {
            log.info("===== Start Block Scan Job Error ======");
        }
    }
}
