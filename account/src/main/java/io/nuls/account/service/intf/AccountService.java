package io.nuls.account.service.intf;


import io.nuls.account.entity.Account;
import io.nuls.account.entity.Address;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.chain.entity.NulsSignData;
import io.nuls.core.chain.entity.Result;

import java.util.List;

/**
 * @author Niels
 */
public interface AccountService {

    Account createAccount();

    Result<List<String>> createAccount(int count);

    void resetKey(Account account);

    void resetKey(Account account, String password);

    void resetKeys();

    void resetKeys(String password);

    Account getLocalAccount();

    List<Account> getLocalAccountList();

    Account getAccount(String address);

    Address getAddress(String pubKey);

    byte[] getPriKey(String address);

    void switchAccount(String id);

    String getDefaultAccount();

    Result changePassword(String oldpw, String newpw);

    Result setPassword(String password);

    boolean isEncrypted();

    Result lockAccounts();

    Result unlockAccounts(String password, int seconds);

    NulsSignData signData(byte[] bytes);

    NulsSignData signData(NulsDigestData digestData);

    NulsSignData signData(byte[] bytes, String password);

    NulsSignData signData(NulsDigestData digestData, String password);

    NulsSignData signData(byte[] bytes, Account account, String password);

    NulsSignData signData(NulsDigestData digestData, Account account, String password);

    Result canSetAlias(String address, String alias);

    Result setAlias(String address, String alias);

    Result sendAliasTx(String address, String password, String alias);

    Result verifySign(byte[] bytes, NulsSignData data);

    Result exportAccount(String filePath);

    Result exportAccount(String address, String filePath);

    Result exportAccounts(String filePath);

    Result importAccountsFile(String walletFilePath);
}
