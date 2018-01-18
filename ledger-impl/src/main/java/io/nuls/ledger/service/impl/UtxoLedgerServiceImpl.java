package io.nuls.ledger.service.impl;

import io.nuls.core.chain.entity.Na;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.chain.entity.Result;
import io.nuls.core.chain.entity.Transaction;
import io.nuls.core.chain.manager.TransactionManager;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.constant.TransactionConstant;
import io.nuls.core.context.NulsContext;
import io.nuls.core.exception.NulsException;
import io.nuls.core.tx.serivce.TransactionService;
import io.nuls.core.utils.log.Log;
import io.nuls.core.utils.param.AssertUtil;
import io.nuls.db.dao.UtxoInputDataService;
import io.nuls.db.dao.UtxoOutputDataService;
import io.nuls.db.dao.UtxoTransactionDataService;
import io.nuls.db.entity.TransactionLocalPo;
import io.nuls.db.entity.TransactionPo;
import io.nuls.db.entity.UtxoOutputPo;
import io.nuls.db.transactional.annotation.TransactionalAnnotation;
import io.nuls.db.util.TransactionPoTool;
import io.nuls.event.bus.service.intf.EventBroadcaster;
import io.nuls.ledger.entity.Balance;
import io.nuls.ledger.entity.UtxoBalance;
import io.nuls.ledger.entity.UtxoOutput;
import io.nuls.ledger.entity.params.Coin;
import io.nuls.ledger.entity.params.CoinTransferData;
import io.nuls.ledger.entity.tx.AbstractCoinTransaction;
import io.nuls.ledger.entity.tx.LockNulsTransaction;
import io.nuls.ledger.entity.tx.TransferTransaction;
import io.nuls.ledger.event.TransactionEvent;
import io.nuls.ledger.service.intf.LedgerService;
import io.nuls.ledger.util.UtxoTransactionTool;
import io.nuls.ledger.util.UtxoTransferTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Niels
 * @date 2017/11/13
 */
public class UtxoLedgerServiceImpl implements LedgerService {

    private static final LedgerService INSTANCE = new UtxoLedgerServiceImpl();

    private LedgerCacheService ledgerCacheService = LedgerCacheService.getInstance();

    private UtxoTransactionDataService txDao;

    private EventBroadcaster eventBroadcaster;

    private Lock lock = new ReentrantLock();

    private UtxoLedgerServiceImpl() {

    }

    public static LedgerService getInstance() {
        return INSTANCE;
    }

    public void init() {
        txDao = NulsContext.getInstance().getService(UtxoTransactionDataService.class);
        eventBroadcaster = NulsContext.getInstance().getService(EventBroadcaster.class);

        UtxoOutputDataService outputDataService = NulsContext.getInstance().getService(UtxoOutputDataService.class);
        UtxoInputDataService inputDataService = NulsContext.getInstance().getService(UtxoInputDataService.class);
        UtxoTransactionTool.getInstance().setInputDataService(inputDataService);
    }

    @Override
    public Transaction getTx(NulsDigestData hash) {
        TransactionPo po = txDao.gettx(hash.getDigestHex(), false);
        try {
            return TransactionPoTool.toTransaction(po);
        } catch (Exception e) {
            Log.error(e);
        }
        return null;
    }

    @Override
    public Transaction getLocalTx(NulsDigestData hash) {
        TransactionPo po = txDao.gettx(hash.getDigestHex(), true);
        try {
            return TransactionPoTool.toTransaction(po);
        } catch (Exception e) {
            Log.error(e);
        }
        return null;
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
        if (unSpendList == null || unSpendList.isEmpty()) {
            return null;
        }
        List<UtxoOutput> unSpends = new ArrayList<>();

        long useable = 0;
        long locked = 0;
        for (UtxoOutputPo po : unSpendList) {
            UtxoOutput output = UtxoTransferTool.toOutput(po);
            if (output.getStatus() == 0) {
                useable += output.getValue();
            } else {
                locked += output.getValue();
            }
            unSpends.add(output);
        }
        balance.setUseable(Na.valueOf(useable));
        balance.setLocked(Na.valueOf(locked));
        balance.setBalance(balance.getLocked().add(balance.getUseable()));
        balance.setUnSpends(unSpends);
        return balance;
    }

    @Override
    public Result transfer(String address, String password, String toAddress, Na amount, String remark) {
        TransferTransaction tx = null;
        try {
            CoinTransferData coinData = new CoinTransferData(amount, address, toAddress);
            tx = UtxoTransactionTool.getInstance().createTransferTx(coinData, password, remark);
            TransactionEvent event = new TransactionEvent();
            event.setEventBody(tx);
            eventBroadcaster.broadcastAndCacheAysn(event, true);
        } catch (Exception e) {
            Log.error(e);
            return new Result(false, e.getMessage());
        }

        return new Result(true, "OK", tx.getHash().getDigestHex());
    }

    @Override
    public Result transfer(List<String> addressList, String password, String toAddress, Na amount, String remark) {
        TransferTransaction tx = null;
        try {
            CoinTransferData coinData = new CoinTransferData(amount, addressList, toAddress);
            tx = UtxoTransactionTool.getInstance().createTransferTx(coinData, password, remark);
            TransactionEvent event = new TransactionEvent();
            event.setEventBody(tx);
            eventBroadcaster.broadcastAndCacheAysn(event, true);
        } catch (Exception e) {
            Log.error(e);
            return new Result(false, e.getMessage());
        }

        return new Result(true, "OK", tx.getHash().getDigestHex());
    }


    @Override
    public Result lock(String address, String password, Na amount, long unlockTime) {
        LockNulsTransaction tx = null;
        try {
            CoinTransferData coinData = new CoinTransferData(amount, address);
            coinData.addTo(address, new Coin(amount, unlockTime));
            tx = UtxoTransactionTool.getInstance().createLockNulsTx(coinData, password);
            TransactionEvent event = new TransactionEvent();
            event.setEventBody(tx);
            eventBroadcaster.broadcastAndCacheAysn(event, true);

        } catch (Exception e) {
            return new Result(false, e.getMessage());
        }

        return new Result(true, "OK", tx.getHash().getDigestHex());
    }

    @Override
    @TransactionalAnnotation
    public boolean saveTxList(List<Transaction> txList) throws IOException {
        lock.lock();
        try {
            List<TransactionPo> poList = new ArrayList<>();
            List<TransactionLocalPo> localPoList = new ArrayList<>();
            for (int i = 0; i < txList.size(); i++) {
                Transaction tx = txList.get(i);
                TransactionPo po = TransactionPoTool.toPojo(tx);
                poList.add(po);
                if (tx instanceof AbstractCoinTransaction) {
                    if (UtxoTransactionTool.getInstance().isMine((AbstractCoinTransaction) tx)) {
                        TransactionLocalPo localPo = TransactionPoTool.toPojoLocal(tx);
                        localPoList.add(localPo);
                    }
                }
            }
            txDao.saveTxList(poList);
            if (localPoList.size() > 0) {
                txDao.saveLocalList(localPoList);
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public List<Transaction> getListByAddress(String address, int txType, int pageNumber, int pageSize) throws Exception {
        List<Transaction> txList = new ArrayList<>();
        List<TransactionPo> poList = txDao.getTxs(address, txType, pageNumber, pageSize, true);

        for (TransactionPo po : poList) {
            txList.add(TransactionPoTool.toTransaction(po));
        }
        return txList;
    }

    @Override
    public List<Transaction> getListByBlockHash(String blockHash) throws Exception {
        List<Transaction> txList = new ArrayList<>();
        List<TransactionPo> poList = txDao.getTxs(blockHash, true);
        for (TransactionPo po : poList) {
            txList.add(TransactionPoTool.toTransaction(po));
        }
        return txList;
    }

    @Override
    public List<Transaction> getListByHeight(long startHeight, long endHeight) throws Exception {
        List<Transaction> txList = new ArrayList<>();
        List<TransactionPo> poList = txDao.getTxs(startHeight, endHeight, true);
        for (TransactionPo po : poList) {
            txList.add(TransactionPoTool.toTransaction(po));
        }
        return txList;
    }

    @Override
    public List<Transaction> getListByHeight(long height) throws Exception {
        List<Transaction> txList = new ArrayList<>();
        List<TransactionPo> poList = txDao.getTxs(height, true);
        for (TransactionPo po : poList) {
            txList.add(TransactionPoTool.toTransaction(po));
        }
        return txList;
    }

    @Override
    public void rollbackTx(Transaction tx) throws NulsException {
        AssertUtil.canNotEmpty(tx, ErrorCode.NULL_PARAMETER);
        List<TransactionService> serviceList = getServiceList(tx.getClass());
        for (TransactionService service : serviceList) {
            service.onRollback(tx);
        }
    }

    @Override
    public void commitTx(Transaction tx) throws NulsException {
        AssertUtil.canNotEmpty(tx, ErrorCode.NULL_PARAMETER);
        List<TransactionService> serviceList = getServiceList(tx.getClass());
        for (TransactionService service : serviceList) {
            service.onCommit(tx);
        }
    }

    @Override
    public void approvalTx(Transaction tx) throws NulsException {
        AssertUtil.canNotEmpty(tx, ErrorCode.NULL_PARAMETER);
        List<TransactionService> serviceList = getServiceList(tx.getClass());
        for (TransactionService service : serviceList) {
            service.onApproval(tx);
        }
    }

    public List<TransactionService> getServiceList(Class<? extends Transaction> txClass) {
        List<TransactionService> list = new ArrayList<>();
        Class clazz = txClass;
        while (!clazz.equals(Transaction.class)) {
            TransactionService txService = TransactionManager.getService(clazz);
            if (null != txService) {
                list.add(0, txService);
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }


}
