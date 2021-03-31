package com.feel.modules.wallet.job;



import com.feel.modules.wallet.service.AccountCollectionService;
import com.feel.modules.wallet.service.AccountService;
import com.feel.modules.wallet.utils.AccountCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



/**
 * @Author: zz
 * @Description:
 * @Date: 3:50 PM 3/28/21
 * @Modified By
 */
@Component
@PropertySource(value = "classpath:/application.yml")
@Slf4j
public class EusdtCollectionJob implements CollectionJob {

    @Autowired
    private AccountService accountService;


    @Autowired
    private AccountCollectionService accountCollectionService;

    @Override
    @Scheduled(cron = "${collection.checkAccount}")
    public void checkAccount() {
        log.info("======> 开始检查账户");
        try {
            AccountCollection accountCollection = new AccountCollection(accountService , 100);
            accountCollection.runCheckAccount(accountCollectionService);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    @Scheduled(cron = "${collection.collectionCoin}")
    public void collectionCoin() {
        log.info("======> 开始归集");
        try {
            AccountCollection accountCollection = new AccountCollection(accountService , 100);
            accountCollection.runCollectionCoin(accountCollectionService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
