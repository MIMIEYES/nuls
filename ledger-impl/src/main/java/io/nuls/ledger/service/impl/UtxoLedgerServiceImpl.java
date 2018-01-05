package io.nuls.ledger.service.impl;

import io.nuls.core.chain.entity.Na;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.chain.entity.Result;
import io.nuls.core.chain.entity.Transaction;
import io.nuls.core.constant.TransactionConstant;
import io.nuls.core.context.NulsContext;
import io.nuls.core.exception.NulsException;
import io.nuls.core.exception.NulsVerificationException;
import io.nuls.core.utils.crypto.Hex;
import io.nuls.core.utils.log.Log;
import io.nuls.core.validate.ValidateResult;
import io.nuls.db.dao.UtxoTransactionDao;
import io.nuls.db.entity.TransactionPo;
import io.nuls.db.entity.UtxoOutputPo;
import io.nuls.db.util.TransactionPoTool;
import io.nuls.event.bus.service.intf.NetworkEventBroadcaster;
import io.nuls.ledger.entity.Balance;
import io.nuls.ledger.entity.UtxoBalance;
import io.nuls.ledger.entity.UtxoData;
import io.nuls.ledger.entity.UtxoOutput;
import io.nuls.ledger.entity.tx.TransferTransaction;
import io.nuls.ledger.event.TransferCoinEvent;
import io.nuls.ledger.service.intf.LedgerService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels
 * @date 2017/11/13
 */
public class UtxoLedgerServiceImpl implements LedgerService {

    private static final LedgerService INSTANCE = new UtxoLedgerServiceImpl();

    private LedgerCacheService ledgerCacheService = LedgerCacheService.getInstance();

    private UtxoTransactionDao txDao = NulsContext.getInstance().getService(UtxoTransactionDao.class);

    private NetworkEventBroadcaster networkEventBroadcaster = NulsContext.getInstance().getService(NetworkEventBroadcaster.class);

    private UtxoLedgerServiceImpl() {

    }

    public static LedgerService getInstance() {
        return INSTANCE;
    }

    @Override
    public ValidateResult verifyAndCacheTx(Transaction tx) throws NulsException {
        ValidateResult result = tx.verify();
        if (null == result || result.isFailed()) {
            return result;
        }
        tx.onApproval();
        ledgerCacheService.putTx(tx);
        return result;
    }

    @Override
    public Transaction getTxFromCache(String hash) {
        Transaction tx = ledgerCacheService.getTx(hash);
        return tx;
    }

    @Override
    public Transaction getTx(byte[] txid, boolean isMine) {
        String hash = Hex.encode(txid);
        return getTx(hash, isMine);
    }

    @Override
    public Transaction getTx(String hash, boolean isMine) {
        Transaction tx = getTxFromCache(hash);
        if (tx == null) {
            TransactionPo po = txDao.gettx(hash, isMine);
            try {
                tx = TransactionPoTool.toTransaction(po);
            } catch (Exception e) {
                Log.error(e);
            }
        }
        return tx;
    }

    @Override
    public boolean txExist(String hash) {
        Transaction tx = getTxFromCache(hash);
        if (tx != null) {
            return true;
        }
        TransactionPo po = txDao.gettx(hash, false);
        return po != null;
    }

    @Override
    public Balance getBalance(String address) {
        Balance balance = ledgerCacheService.getBalance(address);
        if (null == balance) {
            balance = calcBalance(address);
            ledgerCacheService.putBalance(address, balance);
        }
        return balance;
    }

    private Balance calcBalance(String address) {
        UtxoBalance balance = new UtxoBalance();
        List<UtxoOutputPo> unSpendList = txDao.getAccountOutputs(address, TransactionConstant.TX_OUTPUT_UNSPEND);
        List<UtxoOutput> unSpends = new ArrayList<>();

        long value = 0;
        for (UtxoOutputPo output : unSpendList) {
            value += output.getValue();

            //todo
        }
        balance.setUseable(Na.valueOf(value));

        List<UtxoOutputPo> lockedList = txDao.getAccountOutputs(address, TransactionConstant.TX_OUTPUT_LOCKED);
        value = 0;
        for (UtxoOutputPo output : lockedList) {
            value += output.getValue();
        }
        balance.setLocked(Na.valueOf(value));
        balance.setBalance(balance.getLocked().add(balance.getUseable()));
//        balance.setUnSpends(unSpendList);
        return balance;
    }

    @Override
    public Result transfer(TransferTransaction tx) {
        try {
            verifyAndCacheTx(tx);
            TransferCoinEvent event = new TransferCoinEvent();
            event.setEventBody(tx);
            networkEventBroadcaster.broadcastAndCache(event);
        } catch (Exception e) {
            Log.error(e);
            return new Result(false, e.getMessage());
        }
        return new Result(true, "OK");
    }

//    @Override
//    public Result transfer(Account account, String password, Address toAddress, Na amount, String remark) {
//        if (account == null) {
//            return new Result(false, "account not found");
//        }
//        if (!account.validatePassword(password)) {
//            return new Result(false, "password error");
//        }
//        Balance balance = getBalance(account.getAddress().getBase58());
//        if (balance.getUseable().isLessThan(amount)) {
//            return new Result(false, "balance is not enough");
//        }
//        try {
//            CoinTransferData coinData = new CoinTransferData(amount, account.getAddress().getBase58(), toAddress.getBase58());
//            TransferTransaction tx = new TransferTransaction(coinData, password);
//            tx.setHash(NulsDigestData.calcDigestData(tx.serialize()));
//            aliasTx.setSign(signData(aliasTx.getHash(), account, password));
////            tx.setSign();
////            TransferCoinEvent
//        } catch (IOException e) {
//            Log.error(e);
//            return new Result(false, "transaction failed");
//        } catch (Exception e) {
//            Log.error(e);
//            return new Result(false, e.getMessage());
//        }
//
//        return null;
//    }

    @Override
    public boolean saveTransaction(Transaction tx) {
        boolean result = false;
        do {
            if (null == tx) {
                break;
            }
            try {
                tx.verify();
            } catch (NulsVerificationException e) {
                break;
            }
            //todo save to db
        } while (false);
        return result;
    }

    @Override
    public List<Transaction> queryListByAccount(String address, int txType, long beginTime) {
        //todo
        return null;
    }

    @Override
    public List<TransactionPo> queryPoListByAccount(String address, int txType, long beginTime) {

        return null;
    }


    @Override
    public Transaction getTransaction(NulsDigestData txHash) {
        // todo auto-generated method stub(niels)
        return null;
    }

    @Override
    public void removeFromCache(List<NulsDigestData> txHashList) {
        // todo auto-generated method stub(niels)

    }

    @Override
    public List<Transaction> getTxListFromCache() {
        // todo auto-generated method stub(niels)
        return null;
    }

    private UtxoData getUtxoData(Na na) {
        //todo
        return null;
    }
}
