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
package io.nuls.ledger.service.intf;

import io.nuls.core.chain.entity.Na;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.chain.entity.Result;
import io.nuls.core.chain.entity.Transaction;
import io.nuls.core.exception.NulsException;
import io.nuls.ledger.entity.Balance;

import java.io.IOException;
import java.util.List;

/**
 * @author Niels
 * @date 2017/11/9
 */
public interface LedgerService {

    void init();

    /**
     * @param hash
     * @return
     */
    Transaction getTx(NulsDigestData hash);

    /**
     * @param hash
     * @return
     */
    Transaction getLocalTx(NulsDigestData hash);

    /**
     * @param address
     * @return
     */
    Balance getBalance(String address);

    /**
     * @param address
     * @param password
     * @param toAddress
     * @param amount
     * @param remark
     * @return
     */
    Result transfer(String address, String password, String toAddress, Na amount, String remark);

    Result transfer(List<String> addressList, String password, String toAddress, Na amount, String remark);

    /**
     * unlockTime < 100,000,000,000  means the blockHeight
     * unlockTime > 100,000,000,000  means the timestamp
     *
     * @param address
     * @param password
     * @param amount
     * @param unlockTime
     * @return
     */
    Result lock(String address, String password, Na amount, long unlockTime,String remark);

    /**
     * @param txList
     * @return
     */
    boolean saveTxList(List<Transaction> txList) throws IOException;

    /**
     * @param address
     * @param txType
     * @param pageNumber
     * @param pageSize
     * @return
     */
    List<Transaction> getTxList(String address, int txType, int pageNumber, int pageSize) throws Exception;

    List<Transaction> getTxList(String address, int txType) throws Exception;

    /**
     * @param blockHash
     * @return
     */
    List<Transaction> getTxList(String blockHash) throws Exception;

    List<Transaction> getTxList(long startHeight, long endHeight) throws Exception;

    List<Transaction> getTxList(long height) throws Exception;

    void rollbackTx(Transaction tx) throws NulsException;

    void commitTx(Transaction tx) throws NulsException;

    void approvalTx(Transaction tx) throws NulsException;

    void deleteTx(Transaction tx);
    void deleteTx(long blockHeight);
}
