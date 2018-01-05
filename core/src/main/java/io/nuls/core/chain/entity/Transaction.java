package io.nuls.core.chain.entity;

import io.nuls.core.chain.intf.NulsCloneable;
import io.nuls.core.chain.manager.TransactionValidatorManager;
import io.nuls.core.crypto.VarInt;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.crypto.Utils;
import io.nuls.core.utils.date.TimeService;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.io.NulsOutputStreamBuffer;
import io.nuls.core.utils.log.Log;
import io.nuls.core.validate.NulsDataValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels
 * @date 2017/10/30
 */
public abstract class Transaction<T extends BaseNulsData> extends BaseNulsData implements NulsCloneable {

    private List<TransactionListener> listenerList = new ArrayList<>();
    /**
     * tx type
     */
    private int type;
    private NulsDigestData hash;
    private NulsSignData sign;
    private T txData;
    private Na fee;

    private int blockHeight;
    private NulsDigestData blockHash;
    /**
     * current time (ms)
     *
     * @return
     */
    protected long time;
    protected byte[] remark;

    public final void onRollback() throws NulsException {
        for (TransactionListener listener : listenerList) {
            listener.onRollback(this);
        }
    }

    public final void onCommit() throws NulsException {
        for (TransactionListener listener : listenerList) {
            listener.onCommit(this);
        }
    }

    public final void onApproval() throws NulsException {
        for (TransactionListener listener : listenerList) {
            listener.onApproval(this);
        }
    }

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

    protected abstract T parseTxData(NulsByteBuffer byteBuffer) throws NulsException;

    @Override
    public int size() {
        int size = 0;
        size += VarInt.sizeOf(type);
        size += VarInt.sizeOf(time);
        size += Utils.sizeOfSerialize(remark);
        size += Utils.sizeOfSerialize(txData);
        size += Utils.sizeOfSerialize(sign);
        return size;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeVarInt(type);
        stream.writeVarInt(time);
        stream.writeBytesWithLength(remark);
        stream.writeNulsData(txData);
        stream.writeNulsData(sign);
    }

    @Override
    protected void parse(NulsByteBuffer byteBuffer) throws NulsException {
        type = (int) byteBuffer.readVarInt();
        time = byteBuffer.readVarInt();
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

    public void registerListener(TransactionListener listener) {
        this.listenerList.add(listener);
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

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public NulsDigestData getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(NulsDigestData blockHash) {
        this.blockHash = blockHash;
    }
}
