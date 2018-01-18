package io.nuls.db.util;

import io.nuls.core.chain.entity.Na;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.chain.entity.Transaction;
import io.nuls.core.chain.manager.TransactionManager;
import io.nuls.core.context.NulsContext;
import io.nuls.core.utils.crypto.Hex;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.db.entity.TransactionLocalPo;
import io.nuls.db.entity.TransactionPo;

import java.io.IOException;

/**
 * @author vivi
 * @date 2017/12/23.
 */
public class TransactionPoTool {

    public static TransactionPo toPojo(Transaction tx) throws IOException {
        TransactionPo po = new TransactionPo();
        if (tx.getHash() != null) {
            po.setHash(tx.getHash().getDigestHex());
        }
        po.setType(tx.getType());
        po.setCreateTime(tx.getTime());
        po.setBlockHeight(tx.getBlockHeight());
        po.setIndex(tx.getIndex());

        if (null != tx.getTxData()) {
            po.setTxData(tx.getTxData().serialize());
        }
        if (null != tx.getRemark()) {
            po.setRemark(new String(tx.getRemark(), NulsContext.DEFAULT_ENCODING));
        }
        if (null != tx.getFee()) {
            po.setFee(tx.getFee().getValue());
        }

        return po;
    }

    public static TransactionLocalPo toPojoLocal(Transaction tx) throws IOException {
        TransactionLocalPo po = new TransactionLocalPo();
        if (tx.getHash() != null) {
            po.setHash(tx.getHash().getDigestHex());
        }
        po.setType(tx.getType());
        po.setCreateTime(tx.getTime());
        po.setBlockHeight(tx.getBlockHeight());
        po.setIndex(tx.getIndex());

        if (null != tx.getTxData()) {
            po.setTxData(tx.getTxData().serialize());
        }
        if (null != tx.getRemark()) {
            po.setRemark(new String(tx.getRemark(), NulsContext.DEFAULT_ENCODING));
        }
        if (null != tx.getFee()) {
            po.setFee(tx.getFee().getValue());
        }

        return po;
    }

    public static Transaction toTransaction(TransactionPo po) throws Exception {
        Transaction tx = TransactionManager.getInstanceByType(po.getType());
        tx.setHash(new NulsDigestData(Hex.decode(po.getHash())));
        tx.setTime(po.getCreateTime());
        tx.setBlockHeight(po.getBlockHeight());
        tx.setFee(Na.valueOf(po.getFee()));
        tx.setIndex(po.getIndex());
        tx.setRemark(po.getRemark().getBytes(NulsContext.DEFAULT_ENCODING));
        tx.parseTxData(new NulsByteBuffer(po.getTxData()));
        return tx;
    }

    public static Transaction toTransaction(TransactionLocalPo po) throws Exception {
        Transaction tx = TransactionManager.getInstanceByType(po.getType());
        tx.setHash(new NulsDigestData(Hex.decode(po.getHash())));
        tx.setTime(po.getCreateTime());
        tx.setBlockHeight(po.getBlockHeight());
        tx.setFee(Na.valueOf(po.getFee()));
        tx.setIndex(po.getIndex());
        tx.setRemark(po.getRemark().getBytes(NulsContext.DEFAULT_ENCODING));
        tx.parseTxData(new NulsByteBuffer(po.getTxData()));
        return tx;
    }

    public static TransactionLocalPo toLocal(TransactionPo po) {
        TransactionLocalPo localPo = new TransactionLocalPo();
        localPo.setHash(po.getHash());
        localPo.setType(po.getType());
        localPo.setCreateTime(po.getCreateTime());
        localPo.setBlockHeight(po.getBlockHeight());
        localPo.setFee(po.getFee());
        localPo.setIndex(po.getIndex());
        localPo.setRemark(po.getRemark());
        localPo.setTxData(po.getTxData());
        return localPo;
    }

    public static TransactionPo toTx(TransactionLocalPo localPo) {
        TransactionPo po = new TransactionPo();
        po.setHash(localPo.getHash());
        po.setType(localPo.getType());
        po.setCreateTime(localPo.getCreateTime());
        po.setBlockHeight(localPo.getBlockHeight());
        po.setFee(localPo.getFee());
        po.setIndex(localPo.getIndex());
        po.setRemark(localPo.getRemark());
        po.setTxData(localPo.getTxData());

        return po;
    }

}
