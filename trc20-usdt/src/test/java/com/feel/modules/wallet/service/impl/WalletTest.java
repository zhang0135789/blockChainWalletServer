package com.feel.modules.wallet.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feel.common.utils.TrxUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.*;
import org.web3j.protocol.ObjectMapperFactory;
public class WalletTest {



    @Test
    public void testCreateAddress() throws Exception {

        String password = "psxxdasfsadf";
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createStandard(password, ecKeyPair);
        String keystore = objectMapper.writeValueAsString(walletFile);
        WalletFile walletFile2 = objectMapper.readValue(keystore, WalletFile.class);
        ECKeyPair ecKeyPair1 = Wallet.decrypt(password, walletFile2);

        String priKeyStr = ecKeyPair1.getPrivateKey().toString(16);
        String newAddress = TrxUtils.fromHexAddress("41"+walletFile.getAddress());
        String prvkey = TrxUtils.encrypt(newAddress + password, priKeyStr);
        System.out.println("withdraw-wallet-password: " + password);
        System.out.println("withdraw-wallet: " + prvkey);
        System.out.println("withdraw-address: " + newAddress);



    }
}
