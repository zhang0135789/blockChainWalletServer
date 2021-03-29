package com.feel.modules.wallet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.feel.modules.wallet.entity.ScanLog;
import com.feel.modules.wallet.service.ScanLogService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: zz
 * @Description: 扫描日志记录业务层
 * @Date: 4:36 PM 3/22/21
 * @Modified By
 */
@Service
public class ScanLogServiceImpl implements ScanLogService {

    @Autowired
    private MongoTemplate mongoTemplate;


    private String getCollectionName() {
        return "coin_scan_log";
    }

    @Override
    public void update(String coinName, Long blockHeight) {
        ScanLog scanLog = findOne(coinName);
        if(ObjectUtil.isNotEmpty(scanLog)) {
            Query query = new Query();
            Criteria criteria = Criteria.where("coinName").is(coinName);
            query.addCriteria(criteria);
            Update update = new Update();
            update.set("lastSyncHeight",blockHeight);
            update.set("updateTime",new Date());
            UpdateResult result = mongoTemplate.updateFirst(query, update, getCollectionName());
        }else {
            ScanLog scanLog1 = ScanLog.builder()
                    .coinName(coinName)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .lastSyncHeight(blockHeight)
                    .build();
//            scanLog.setCreateTime(new Date());
//            scanLog.setUpdateTime(new Date());
//            scanLog.setCoinName(coinName);
//            scanLog.setLastSyncHeight(blockHeight);
            ScanLog insert = mongoTemplate.insert(scanLog1, getCollectionName());
        }
    }


    @Override
    public ScanLog findOne(String coinName) {
        Query query = new Query();
        Criteria criteria = Criteria.where("coinName").is(coinName);
        query.addCriteria(criteria);
        ScanLog one = mongoTemplate.findOne(query, ScanLog.class, getCollectionName());
        return one;
    }


}
