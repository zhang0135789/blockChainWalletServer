package com.feel.modules.wallet.service.impl;

import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.entity.Coin;
import com.feel.modules.wallet.service.AccountService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: zz
 * @Description: 账户管理
 * @Date: 2:42 PM 3/18/21
 * @Modified By
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Coin coin;

    public String getCollectionName() {
        return coin.getUnit() + "_address_book";
    }

    public String getCollectionName(String name) {
        return name + "_address_book";
    }


    /**
     *  根据账户名和代币名查询账户信息
     * @param coinUnit
     * @param username
     * @return
     */
    @Override
    public Account findByName(String coinUnit, String username) {
        Query query = new Query();
        Criteria criteria = Criteria.where("account").is(username);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query , Account.class , getCollectionName(coinUnit));
    }



    /**
     *  根据账户名和代币名查询账户信息
     * @param username
     * @return
     */
    @Override
    public Account findByName(String username) {
        return findByName(coin.getUnit() , username);
    }

    /**
     * 根据账户名查询账户信息
     * @param address
     * @return
     */
    @Override
    public Account findByAddress(String address) {
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query,Account.class,getCollectionName());
    }

    /**
     * 根据账户名删除账户信息
     * @param username
     * @return
     */
    @Override
    public long removeByName(String username) {
        Query query = new Query();
        Criteria criteria = Criteria.where("account").is(username);
        query.addCriteria(criteria);
        DeleteResult remove = mongoTemplate.remove(query, getCollectionName());
        return remove.getDeletedCount();
    }

    /**
     * 查询系统中是否存在地址
     * @param address
     * @return
     */
    @Override
    public boolean isAddressExist(String address) {
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address);
        query.addCriteria(criteria);
        return  mongoTemplate.exists(query,getCollectionName());
    }

    /**
     * 保存新的账户信息
     * @param username
     * @param fileName
     * @param address
     */
    @Override
    public Account saveOne(String username, String fileName, String address) {
        removeByName(username);
        Account account = new Account();
        account.setAccount(username);
        account.setAddress(address.toLowerCase());
        account.setWalletFile(fileName);
        return save(account);
    }

    /**
     * 保存新的账户信息
     * @param username
     * @param address
     */
    @Override
    public Account saveOne(String username, String address) {
//        removeByName(username);
        Account account = new Account();
        account.setAccount(username);
        account.setAddress(address.toLowerCase());
        return save(account);
    }

    @Override
    public Account saveByName(Account account, String collectionName) {
        return mongoTemplate.insert(account,getCollectionName(collectionName));
    }

    /**
     * 保存账户信息
     * @param account
     */
    public Account save(Account account){
        return mongoTemplate.insert(account,getCollectionName());
    }

    /**
     * 查询所有账户列表
     * @return
     */
    @Override
    public List<Account> findAll() {
        return mongoTemplate.findAll(Account.class,getCollectionName());
    }




    /**
     * 统计所有账户数量
     * @return
     */
    @Override
    public long count() {
        Query query = new Query();
        Sort.Order order = new Sort.Order(Sort.Direction.ASC,"id");
        Sort sort = new Sort(order);
        query.with(sort);
        return mongoTemplate.count(query , getCollectionName());
    }

    /**
     * 分页查询账户
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Account> find(int pageNo, int pageSize) {
        Query query = new Query();
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "_id");
        Sort sort = Sort.by(order);
        PageRequest page = PageRequest.of(pageNo, pageSize, sort);
        query.with(page);
        return mongoTemplate.find(query , Account.class , getCollectionName());
    }

    /**
     * 查找所有需要归集账户
     * @return
     */
    @Override
    public List<Account> findCollections(int pageNo, int pageSize) {
        Query query = new Query();
        Criteria criteria = Criteria.where("status").is(1);
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "_id");
        Sort sort = Sort.by(order);
        PageRequest page = PageRequest.of(pageNo, pageSize, sort);
        query.with(page);
        query.addCriteria(criteria);
        return mongoTemplate.find(query,Account.class,getCollectionName());
    }


    /**
     * 根据余额查询账户信息
     * @param minAmount 大于等于的金额
     * @return
     */
    @Override
    public List<Account> findByBalance(BigDecimal minAmount) {
        Query query = new Query();
        Criteria criteria = Criteria.where("amount").gte(minAmount);
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "balance");
        Sort sort = Sort.by(order);
        query.addCriteria(criteria);
        query.with(sort);
        return mongoTemplate.find(query, Account.class, getCollectionName());
    }

    /**
     * 根据余额和燃气查询账户信息
     * @param minAmount
     * @param gasLimit
     * @return
     */
    @Override
    public List<Account> findByBalanceAndGas(BigDecimal minAmount, BigDecimal gasLimit) {
        Query query = new Query();
        Criteria criteria = Criteria.where("balance").gte(minAmount);
        criteria.andOperator(Criteria.where("gas").gte(gasLimit));
        query.addCriteria(criteria);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "balance"));
        query.with(sort);
        return mongoTemplate.find(query, Account.class, getCollectionName());
    }

    /**
     * 查询钱包总余额
     * @return
     */
    @Override
    public BigDecimal findBalanceSum() {

        return null;
    }

    /**
     * 更新余额
     * @param address
     * @param balance
     */
    @Override
    public long updateBalance(String address, BigDecimal balance) {
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address);
        query.addCriteria(criteria);
        Update update = Update.update("balance",balance.setScale(8, BigDecimal.ROUND_DOWN));
        UpdateResult result = mongoTemplate.updateFirst(query, update, getCollectionName());
        return result.getModifiedCount();
    }

    /**
    * @Description: 更新地址状态
    * @Param:
    * @return:
    * @Author: lhp
    * @Date: 2021-03-31 21:03
    **/
    @Override
    public void updateStatus(String address, Integer status) {
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address);
        query.addCriteria(criteria);
        Update update = Update.update("status",1);
        mongoTemplate.updateFirst(query, update, getCollectionName());
    }

    /**
     * 更新余额
     * @param address
     * @param balance
     * @param gas
     */
    @Override
    public long updateBalanceAndGas(String address, BigDecimal balance, BigDecimal gas) {
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address);
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("balance" , balance.setScale(8 , BigDecimal.ROUND_DOWN));
        update.set("gas" , gas);
        UpdateResult result = mongoTemplate.updateFirst(query, update, getCollectionName());
        return result.getModifiedCount();
    }
}
