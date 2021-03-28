package com.feel.modules.wallet.service;

import com.feel.modules.wallet.entity.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: zz
 * @Description:
 * @Date: 2:40 PM 3/18/21
 * @Modified By
 */
public interface AccountService {

    /**
     * 根据username 和 代币合约 查询账户信息
     * @param coinUnit
     * @param username
     * @return
     */
    Account findByName(String coinUnit, String username);

    /**
     * 根据username 和 代币合约 查询账户信息
     * @param username
     * @return
     */
    Account findByName(String username);

    /**
     * 根据username查询账户信息
     * @param address
     * @return
     */
    Account findByAddress(String address);

    /**
     * 根据账户名字移除账户
     * @param name
     */
    long removeByName(String name);

    /**
     * 查询地址是否存在
     * @param address
     * @return
     */
    boolean isAddressExist(String address);

    /**
     *
     * @param username
     * @param fileName
     * @param address
     * @return
     */
    Account saveOne(String username, String fileName, String address);

    /**
     * 保存账户
     * @param username
     * @param address
     * @return
     */
    Account saveOne(String username, String address);

    /**
     * 查询所有账户
     * @return
     */
    List<Account> findAll();

    Account save(Account account);

    /**
     * 查询所有账户数量
     * @return
     */
    long count();

    /**
     * 分页查询账户列表
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<Account> find(int pageNo,int pageSize);

    /**
     * 根据余额查询账户列表
     * @param minAmount
     * @return
     */
    List<Account> findByBalance(BigDecimal minAmount);

    /**
     *
     * @param minAmount
     * @param gasLimit
     * @return
     */
    List<Account> findByBalanceAndGas(BigDecimal minAmount,BigDecimal gasLimit);

    /**
     * 查询所有账户总金额
     * @return
     */
    BigDecimal findBalanceSum();

    /**
     * 更新账户余额
     * @param address
     * @param balance
     */
    long updateBalance(String address, BigDecimal balance);

    /**
     * 更新账户gas量
     * @param address
     * @param balance
     * @param gas
     */
    long updateBalanceAndGas(String address, BigDecimal balance,BigDecimal gas);


}

