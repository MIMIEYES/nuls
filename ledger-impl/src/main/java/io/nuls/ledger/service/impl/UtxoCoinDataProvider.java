package io.nuls.ledger.service.impl;

import io.nuls.core.chain.entity.Na;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.ledger.entity.CoinData;
import io.nuls.ledger.entity.UtxoData;
import io.nuls.ledger.entity.UtxoInput;
import io.nuls.ledger.entity.UtxoOutput;
import io.nuls.ledger.entity.params.CoinTransferData;
import io.nuls.ledger.service.intf.CoinDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Niels
 * @date 2017/12/21
 */
public class UtxoCoinDataProvider implements CoinDataProvider {
    private static final UtxoCoinDataProvider INSTANCE = new UtxoCoinDataProvider();

    private UtxoCoinDataProvider() {

    }

    public static UtxoCoinDataProvider getInstance() {
        return INSTANCE;
    }


    @Override
    public CoinData parse(NulsByteBuffer byteBuffer) throws NulsException {

        return byteBuffer.readNulsData(new UtxoData());
    }

    @Override
    public CoinTransferData getTransferData(CoinData coinData) {
        // todo auto-generated method stub(niels)
        return null;
    }

    @Override
    public void approve(CoinData coinData, String txHash) {
        // todo auto-generated method stub(niels)

    }

    @Override
    public void save(CoinData coinData, String txHash) {
        // todo auto-generated method stub(niels)

    }

    @Override
    public void rollback(CoinData coinData, String txHash) {
        // todo auto-generated method stub(niels)

    }

    @Override
    public CoinData createTransferData(CoinTransferData coinParam, String password) {
        UtxoData utxoData = new UtxoData();
        List<UtxoInput> inputs = new ArrayList<>();
        List<UtxoOutput> outputs = new ArrayList<>();

        for (Map.Entry<String, Na> entry : coinParam.getFromMap().entrySet()) {

        }
            return null;
    }

}
