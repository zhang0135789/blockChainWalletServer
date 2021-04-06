package com.feel.modules.wallet.service.impl;

import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.Withdraw;
import com.feel.modules.wallet.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @Author: zz
 * @Description: 提现业务层
 * @Date: 2:00 PM 4/6/21
 * @Modified By
 */
@Service
public class WithdrawServiceImpl implements WithdrawService {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Coin coin;

    public String getCollectionName(){
        return coin.getUnit() + "_withdraw";
    }


    /**
     * 保存充值记录
     * @param withdraw
     * @return
     */
    @Override
    public Withdraw save(Withdraw withdraw) {
        Withdraw insert = mongoTemplate.insert(withdraw, getCollectionName());
        return insert;
    }

    /**
     * 根据接收地址和交易hash 查询提现记录是否存在
     * @param withdraw toAddress , txid
     * @return
     */
    @Override
    public boolean exists(Withdraw withdraw) {
        Query query = new Query();
        Criteria criteria = Criteria.where("toAddress").is(withdraw.getToAddress())
                .andOperator(Criteria.where("txid").is(withdraw.getTxid()));
        query.addCriteria(criteria);
        boolean exists = mongoTemplate.exists(query, getCollectionName());
        return exists;
    }

    /**
     * 查找最后一笔提现记录
     * @return
     */
    @Override
    public Withdraw findLatest() {
        Query query = new Query();
        Sort sort = Sort.by(Sort.Direction.DESC,"time");
        PageRequest page = PageRequest.of(0,1 , sort);
        query.with(page);
        Withdraw recharge = mongoTemplate.findOne(query, Withdraw.class, getCollectionName());
        return recharge;
    }

}
