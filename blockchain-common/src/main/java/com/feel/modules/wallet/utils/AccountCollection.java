package com.feel.modules.wallet.utils;

import com.feel.modules.wallet.entity.Account;
import com.feel.modules.wallet.service.AccountCollectionService;
import com.feel.modules.wallet.service.AccountService;
import lombok.Data;

import java.util.List;

/**
 * @Author: zz
 * @Description:
 * @Date: 4:22 PM 3/28/21
 * @Modified By
 */
@Data
public class AccountCollection {

    private AccountService accountService;

    private int pageSize;

    public AccountCollection(AccountService accountService , int pageSize){
        this.accountService = accountService;
        this.pageSize = pageSize;
    }

    /**
     * 检查手续费
     */
    public void runCheckAccount(AccountCollectionService accountCollectionService) {
        long count = accountService.count();
        long totalPage = count / pageSize;
        if(count%pageSize != 0){
            totalPage += 1;
        }

        for(int page = 0;page<=totalPage;page++){
            List<Account> accounts = accountService.find(page,pageSize);
            accounts.forEach(account->{
                accountCollectionService.checkAccount(account);
            });
        }
    }


    /**
     * 归集
     * @param accountCollectionService
     */
    public void runCollectionCoin(AccountCollectionService accountCollectionService) {
        long count = accountService.count();
        long totalPage = count / pageSize;
        if(count%pageSize != 0){
            totalPage += 1;
        }

        for(int page = 0;page<=totalPage;page++){
            List<Account> accounts = accountService.findCollections(page,pageSize);
            accounts.forEach(account->{
                accountCollectionService.collectionCoin(account);
            });
        }

    }


}
