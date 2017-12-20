package io.nuls.ledger.entity.tx;

import io.nuls.core.constant.TransactionConstant;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.io.NulsOutputStreamBuffer;
import io.nuls.ledger.entity.tx.AbstractCoinTransaction;
import io.nuls.ledger.entity.UtxoData;
import io.nuls.ledger.entity.validator.UtxoTxInputsValidator;
import io.nuls.ledger.entity.validator.UtxoTxOutputsValidator;

import java.io.IOException;

/**
 * @author Niels
 * @date 2017/11/14
 */
public class UtxoSmallChangeTransaction extends AbstractCoinTransaction<UtxoData> {

    public UtxoSmallChangeTransaction() {
        super(TransactionConstant.TX_TYPE_SMALL_CHANGE);
        this.registerValidator(UtxoTxInputsValidator.getInstance());
        this.registerValidator(UtxoTxOutputsValidator.getInstance());
    }

    @Override
    public int size() {
        //todo
        return 0;
    }

    @Override
    public void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        //todo
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) {
        //todo

    }


}
