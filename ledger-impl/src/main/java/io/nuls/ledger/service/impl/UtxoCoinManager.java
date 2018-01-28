/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.ledger.service.impl;

import io.nuls.account.entity.Address;
import io.nuls.core.chain.entity.Na;
import io.nuls.core.context.NulsContext;
import io.nuls.core.utils.log.Log;
import io.nuls.db.dao.UtxoOutputDataService;
import io.nuls.db.entity.UtxoOutputPo;
import io.nuls.ledger.entity.UtxoBalance;
import io.nuls.ledger.entity.UtxoOutput;
import io.nuls.ledger.util.UtxoTransferTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * cache and operate all unspend utxo
 */
public class UtxoCoinManager {

    private static UtxoCoinManager instance = new UtxoCoinManager();

    private UtxoCoinManager() {
        cacheService = LedgerCacheService.getInstance();
    }

    public static UtxoCoinManager getInstance() {
        return instance;
    }

    private LedgerCacheService cacheService = LedgerCacheService.getInstance();

    private UtxoOutputDataService outputDataService;

    private Lock lock = new ReentrantLock();

    public void cacheAllUnSpendOutPut() {
        List<UtxoOutputPo> utxoOutputPos = outputDataService.getAllUnSpend();
        String address = null;
        long useable = 0;
        long lock = 0;
        List<UtxoOutput> list = new ArrayList<>();

        for (int i = 0; i < utxoOutputPos.size(); i++) {
            UtxoOutputPo po = utxoOutputPos.get(i);
            UtxoOutput output = UtxoTransferTool.toOutput(po);
            cacheService.putUtxo(output.getKey(), output);
            //todo address format
            if (i == 0) {
                address = new Address(NulsContext.getInstance().getChainId(NulsContext.CHAIN_ID), po.getAddress().getBytes()).getBase58();
            } else if (!address.equals(po.getAddress())) {
                UtxoBalance balance = new UtxoBalance();
                balance.setUseable(Na.valueOf(useable));
                balance.setLocked(Na.valueOf(lock));
                balance.setBalance(balance.getUseable().add(balance.getLocked()));
                balance.setUnSpends(list);
                cacheService.putBalance(address, balance);

                useable = 0;
                lock = 0;
                list = new ArrayList<>();
            }
            if (po.getStatus() == UtxoOutput.USEABLE) {
                useable += po.getValue();
            } else if (po.getStatus() == UtxoOutput.LOCKED) {
                lock += po.getValue();
            }
            list.add(output);

            if (i == utxoOutputPos.size() - 1) {
                UtxoBalance balance = new UtxoBalance();
                balance.setUseable(Na.valueOf(useable));
                balance.setLocked(Na.valueOf(lock));
                balance.setBalance(balance.getUseable().add(balance.getLocked()));
                balance.setUnSpends(list);
                cacheService.putBalance(address, balance);
            }
        }
    }

    /**
     * Utxo is used to ensure that each transaction will not double
     *
     * @param address
     * @param value
     * @return
     */
    public List<UtxoOutput> getAccountUnSpend(String address, Na value) {
        lock.lock();
        List<UtxoOutput> unSpends = new ArrayList<>();
        try {
            //todo address format
            UtxoBalance balance = (UtxoBalance) cacheService.getBalance(address);
            if (balance == null || balance.getUseable().isLessThan(value)) {
                return unSpends;
            }

            boolean enough = false;
            Na amount = Na.ZERO;
            for (int i = 0; i < balance.getUnSpends().size(); i++) {
                UtxoOutput output = balance.getUnSpends().get(i);
                boolean update = cacheService.updateUtxoStatus(output.getKey(), UtxoOutput.LOCKED, UtxoOutput.USEABLE);
                if (!update) {
                    continue;
                }
                unSpends.add(output);
                amount = amount.add(Na.valueOf(output.getValue()));
                if (amount.isGreaterThan(value)) {
                    enough = true;
                    break;
                }
            }
            if (!enough) {
                for (UtxoOutput output : unSpends) {
                    cacheService.updateUtxoStatus(output.getKey(), UtxoOutput.USEABLE, UtxoOutput.LOCKED);
                }
                unSpends = new ArrayList<>();
            }
        } catch (Exception e) {
            Log.error(e);
            for (UtxoOutput output : unSpends) {
                cacheService.updateUtxoStatus(output.getKey(), UtxoOutput.USEABLE, UtxoOutput.LOCKED);
            }
        } finally {
            lock.unlock();
        }
        return unSpends;
    }

    public List<UtxoOutput> getAccountsUnSpend(List<String> addressList, Na value) {
        lock.lock();
        //todo address format
        List<UtxoOutput> unSpends = new ArrayList<>();
        try {
            //check use-able is enough , find unSpend utxo
            Na amount = Na.ZERO;
            boolean enough = false;
            for (String address : addressList) {
                UtxoBalance balance = (UtxoBalance) cacheService.getBalance(address);
                for (int i = 0; i < balance.getUnSpends().size(); i++) {
                    UtxoOutput output = balance.getUnSpends().get(i);
                    boolean update = cacheService.updateUtxoStatus(output.getKey(), UtxoOutput.LOCKED, UtxoOutput.USEABLE);
                    if (!update) {
                        continue;
                    }
                    unSpends.add(output);
                    amount = amount.add(Na.valueOf(output.getValue()));
                    if (amount.isGreaterThan(value)) {
                        enough = true;
                        break;
                    }
                }
                if (enough) {
                    break;
                }
            }
            if (!enough) {
                for (UtxoOutput output : unSpends) {
                    cacheService.updateUtxoStatus(output.getKey(), UtxoOutput.USEABLE, UtxoOutput.LOCKED);
                }
                unSpends = new ArrayList<>();
            }
        } catch (Exception e) {
            Log.error(e);
            for (UtxoOutput output : unSpends) {
                cacheService.updateUtxoStatus(output.getKey(), UtxoOutput.USEABLE, UtxoOutput.LOCKED);
            }
            unSpends = new ArrayList<>();
        } finally {
            lock.unlock();
        }
        return unSpends;
    }

    public void setOutputDataService(UtxoOutputDataService outputDataService) {
        this.outputDataService = outputDataService;
    }
}
