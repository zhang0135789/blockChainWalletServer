package com.feel.modules.wallet.service.impl;

import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Recharge;
import com.feel.modules.wallet.service.RechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;



/**
 * @Author: zz
 * @Description: 充值业务层
 * @Date: 3:39 PM 3/22/21
 * @Modified By
 */
@Service
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Coin coin;

    public String getCollectionName(){
        return coin.getUnit() + "_deposit";
    }

    /**
     * 保存充值记录
     * @param recharge
     * @return
     */
    @Override
    public Recharge save(Recharge recharge) {
        Recharge insert = mongoTemplate.insert(recharge, getCollectionName());
        return insert;
    }

    /**
     * 根据接收地址和交易hash 查询充值记录是否存在
     * @param recharge toAddress , txid
     * @return
     */
    @Override
    public boolean exists(Recharge recharge) {
        Query query = new Query();
        Criteria criteria = Criteria.where("toAddress").is(recharge.getToAddress())
                .andOperator(Criteria.where("txid").is(recharge.getTxid()));
        query.addCriteria(criteria);
        boolean exists = mongoTemplate.exists(query, getCollectionName());
        return exists;
    }

    /**
     * 查找最后一笔充值记录
     * @return
     */
    @Override
    public Recharge findLatest() {
        Query query = new Query();
        Sort sort = Sort.by(Sort.Direction.DESC,"blockHeight");
        PageRequest page = PageRequest.of(0,1 , sort);
        query.with(page);
        Recharge recharge = mongoTemplate.findOne(query, Recharge.class, getCollectionName());
        return recharge;
    }
}
