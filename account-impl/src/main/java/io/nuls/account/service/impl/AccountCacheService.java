/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.account.service.impl;

import io.nuls.account.constant.AccountConstant;
import io.nuls.account.entity.Account;
import io.nuls.cache.util.CacheMap;

import java.util.List;

/**
 * @author Niels
 * @date 2017/11/15
 */
public class AccountCacheService {
    private static final AccountCacheService INSTANCE = new AccountCacheService();

    private CacheMap<String, Account> cacheMap;

    private AccountCacheService() {
        this.cacheMap = new CacheMap<>(AccountConstant.ACCOUNT_LIST_CACHE, 32);
    }


    public static AccountCacheService getInstance() {
        return INSTANCE;
    }

    public void putAccount(Account account) {

        this.cacheMap.put(account.getAddress().getBase58(), account);
    }

    public Account getAccountById(String id) {
        return this.cacheMap.get(id);
    }

    public Account getAccountByAddress(String address) {
        List<Account> list = this.getAccountList();
        for (Account account : list) {
            if (account.getAddress().toString().equalsIgnoreCase(address)) {
                return account;
            }
        }
        return null;
    }

    public boolean contains(String address) {
        return this.cacheMap.containsKey(address);
    }

    public List<Account> getAccountList() {
        return this.cacheMap.values();
    }

    public void removeAccount(Account account) {
        this.cacheMap.remove(account.getAddress().getBase58());
    }

    public void removeAccount(String address) {
        this.cacheMap.remove(address);
    }

    public boolean accountExist(String address) {
        return cacheMap.containsKey(address);
    }

    public void clear() {
        this.cacheMap.clear();
    }

    public void destroy() {
        this.cacheMap.destroy();
    }

    public void putAccountList(List<Account> list) {
        if (null != list) {
            for (Account account : list) {
                this.putAccount(account);
            }
        }
    }
}
