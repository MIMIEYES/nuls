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
package io.nuls.core.chain.entity;

import io.nuls.core.chain.intf.NulsCloneable;
import io.nuls.core.chain.manager.TransactionValidatorManager;
import io.nuls.core.constant.TxStatusEnum;
import io.nuls.core.crypto.VarInt;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.crypto.Utils;
import io.nuls.core.utils.date.TimeService;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.io.NulsOutputStreamBuffer;
import io.nuls.core.utils.log.Log;
import io.nuls.core.validate.NulsDataValidator;

import java.io.IOException;
import java.util.List;

/**
 * @author Niels
 * @date 2017/10/30
 */
public abstract class Transaction<T extends BaseNulsData> extends BaseNulsData implements NulsCloneable {

    protected NulsDigestData hash;

    protected int type;

    protected int index;

    protected long time;

    protected long blockHeight;

    protected Na fee;

    protected byte[] remark;

    protected NulsSignData sign;

    protected T txData;

    protected TxStatusEnum status;

    protected boolean localTx;

    public Transaction(int type) {
        this.dataType = NulsDataType.TRANSACTION;
        this.time = TimeService.currentTimeMillis();
        this.type = type;
        this.initValidators();
    }

    private void initValidators() {
        List<NulsDataValidator> list = TransactionValidatorManager.getValidators();
        for (NulsDataValidator<Transaction> validator : list) {
            this.registerValidator(validator);
        }
    }

    public abstract T parseTxData(NulsByteBuffer byteBuffer) throws NulsException;

    @Override
    public int size() {
        int size = 0;
        size += VarInt.sizeOf(type);
        size += VarInt.sizeOf(time);
        size += VarInt.sizeOf(fee.getValue());
        size += Utils.sizeOfSerialize(remark);
        size += Utils.sizeOfSerialize(txData);
        size += Utils.sizeOfSerialize(sign);
        return size;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeVarInt(type);
        stream.writeVarInt(time);
        stream.writeVarInt(fee.getValue());
        stream.writeBytesWithLength(remark);
        stream.writeNulsData(txData);
        stream.writeNulsData(sign);
    }

    @Override
    protected void parse(NulsByteBuffer byteBuffer) throws NulsException {
        type = (int) byteBuffer.readVarInt();
        time = byteBuffer.readVarInt();
        long feeValue = byteBuffer.readVarInt();
        this.fee = Na.valueOf(feeValue);
        this.remark = byteBuffer.readByLengthByte();
        txData = this.parseTxData(byteBuffer);
        try {
            hash = NulsDigestData.calcDigestData(this.serialize());
        } catch (IOException e) {
            Log.error(e);
        }
        sign = byteBuffer.readSign();
    }


    @Override
    public Object copy() {
        try {
            return this.clone();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public byte[] getRemark() {
        return remark;
    }

    public void setRemark(byte[] remark) {
        this.remark = remark;
    }

    public NulsDigestData getHash() {
        return hash;
    }

    public void setHash(NulsDigestData hash) {
        this.hash = hash;
    }

    public NulsSignData getSign() {
        return sign;
    }

    public void setSign(NulsSignData sign) {
        this.sign = sign;
    }

    public T getTxData() {
        return txData;
    }

    public void setTxData(T txData) {
        this.txData = txData;
    }

    public Na getFee() {
        return fee;
    }

    public void setFee(Na fee) {
        this.fee = fee;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TxStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TxStatusEnum status) {
        this.status = status;
    }

    public boolean isLocalTx() {
        return localTx;
    }

    public void setLocalTx(boolean localTx) {
        this.localTx = localTx;
    }
}
