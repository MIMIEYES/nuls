package io.nuls.ledger.entity.tx;

import io.nuls.core.chain.entity.BaseNulsData;
import io.nuls.core.constant.TransactionConstant;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.ledger.entity.params.CoinTransferData;

/**
 * @author Niels
 * @date 2017/11/20
 */
public class SmallChangeTransaction<T extends BaseNulsData>  extends AbstractCoinTransaction<T> {

    public SmallChangeTransaction() {
        this(TransactionConstant.TX_TYPE_SMALL_CHANGE);
    }

    public SmallChangeTransaction(CoinTransferData params, String password) throws NulsException {
        this(TransactionConstant.TX_TYPE_SMALL_CHANGE, params, password);

    }

    protected SmallChangeTransaction(int type, CoinTransferData params, String password) throws NulsException {
        super(type,params,password);
    }

    protected SmallChangeTransaction(int type) {
        super(type);
    }


    @Override
    public T parseTxData(NulsByteBuffer byteBuffer) throws NulsException {
        return null;
    }

}
