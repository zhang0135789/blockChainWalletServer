package com.feel.modules.wallet.service.impl;

import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.entity.CollectionTran;
import com.feel.modules.wallet.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @Author: zz
 * @Description:
 * @Date: 7:40 PM 3/28/21
 * @Modified By
 */
@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Coin coin;

    public String getCollectionName(){
        return coin.getUnit() + "_collection";
    }


    /**
     * 保存归集记录
     * @param collectionTran
     * @return
     */
    @Override
    public CollectionTran save(CollectionTran collectionTran) {
        CollectionTran insert = mongoTemplate.save(collectionTran,getCollectionName());
        return insert;
    }

    /**
     * 归集交易是否存在, 传入txid -交易哈希
     * @param collectionTran
     * @return
     */
    @Override
    public boolean exists(CollectionTran collectionTran) {
        Query query = new Query();
        Criteria criteria = Criteria.where("txid").is(collectionTran.getTxid());
        query.addCriteria(criteria);
        return mongoTemplate.exists(query,getCollectionName());
    }
}
