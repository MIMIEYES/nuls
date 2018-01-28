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
package io.nuls.db.dao;

import io.nuls.db.entity.TransactionLocalPo;

import java.util.List;

/**
 *
 * @author Niels
 * @date 2017/11/20
 */
public interface TransactionLocalDataService extends BaseDataService<String, TransactionLocalPo> {

    List<TransactionLocalPo> getTxs(Long blockHeight);

    List<TransactionLocalPo> getTxs(Long startHeight, Long endHeight);

    List<TransactionLocalPo> getTxs(String blockHash);

    List<TransactionLocalPo> getTxs(byte[] blockHash);

    List<TransactionLocalPo> getTxs(String address, int type, int pageNum, int pageSize);

    List<TransactionLocalPo> getTxs(String address, int type);

    List<TransactionLocalPo> listTranscation(int limit, String address);

    List<TransactionLocalPo> listTransaction(long blockHeight, String address);
}
