package io.nuls.ledger.module.impl;

import io.nuls.account.entity.Account;
import io.nuls.account.service.intf.AccountService;
import io.nuls.core.chain.manager.TransactionValidatorManager;
import io.nuls.core.context.NulsContext;
import io.nuls.core.thread.manager.ThreadManager;
import io.nuls.event.bus.processor.service.intf.NetworkEventProcessorService;
import io.nuls.ledger.constant.LedgerConstant;
import io.nuls.ledger.event.*;
import io.nuls.ledger.handler.CoinTransactionEventHandler;
import io.nuls.ledger.handler.SmallChangeEventHandler;
import io.nuls.ledger.handler.UnlockCoinEventHandler;
import io.nuls.ledger.handler.LockCoinEventHandler;
import io.nuls.ledger.module.AbstractLedgerModule;
import io.nuls.ledger.service.impl.LedgerCacheService;
import io.nuls.ledger.service.impl.UtxoCoinDataProvider;
import io.nuls.ledger.service.impl.UtxoLedgerServiceImpl;
import io.nuls.ledger.service.intf.CoinDataProvider;
import io.nuls.ledger.service.intf.LedgerService;
import io.nuls.ledger.thread.SmallChangeThread;
import io.nuls.ledger.validator.TxFieldValidator;
import io.nuls.ledger.validator.TxMaxSizeValidator;
import io.nuls.ledger.validator.TxRemarkValidator;
import io.nuls.ledger.validator.TxSignValidator;

import java.util.List;

/**
 * @author Niels
 * @date 2017/11/7
 */
public class UtxoLedgerModuleImpl extends AbstractLedgerModule {


    private LedgerCacheService cacheService = LedgerCacheService.getInstance();

    private LedgerService ledgerService = UtxoLedgerServiceImpl.getInstance();

    private NetworkEventProcessorService processorService = NulsContext.getInstance().getService(NetworkEventProcessorService.class);

    @Override
    public void init() {
        addNormalTxValidator();
        registerService();
        pulishEvent();
    }

    /**
     * there validators any kind of transaction will be used
     */
    private void addNormalTxValidator() {
        TransactionValidatorManager.addTxDefValidator(TxMaxSizeValidator.getInstance());
        TransactionValidatorManager.addTxDefValidator(TxRemarkValidator.getInstance());
        TransactionValidatorManager.addTxDefValidator(TxFieldValidator.getInstance());
        TransactionValidatorManager.addTxDefValidator(TxSignValidator.getInstance());
    }

    private void registerService() {
        this.registerService(ledgerService);
        this.registerService(CoinDataProvider.class, UtxoCoinDataProvider.getInstance());
    }

    private void pulishEvent() {
        this.publish(LedgerConstant.EVENT_TYPE_TRANSFER, TransferCoinEvent.class);
        this.publish(LedgerConstant.EVENT_TYPE_LOCK_NULS, LockCoinEvent.class);
        this.publish(LedgerConstant.EVENT_TYPE_UNLOCK_NULS, UnlockCoinEvent.class);
        this.publish(LedgerConstant.EVENT_TYPE_SMALL_CHANGE, SmallChangeEvent.class);

        this.processorService.registerEventHandler(TransferCoinEvent.class, new CoinTransactionEventHandler());
        this.processorService.registerEventHandler(LockCoinEvent.class, new LockCoinEventHandler());
        this.processorService.registerEventHandler(UnlockCoinEvent.class, new UnlockCoinEventHandler());
        this.processorService.registerEventHandler(SmallChangeEvent.class, new SmallChangeEventHandler());
    }

    @Override
    public void start() {
        cacheAccountAndBalance();

        SmallChangeThread smallChangeThread = SmallChangeThread.getInstance();
        ThreadManager.createSingleThreadAndRun(this.getModuleId(), SmallChangeThread.class.getSimpleName(), smallChangeThread);
    }

    private void cacheAccountAndBalance() {
        //load account
        List<Account> accounts = NulsContext.getInstance().getService(AccountService.class).getLocalAccountList();
        if (null == accounts) {
            return;
        }
        //calc balance
        //put standing book to cache
        for (Account account : accounts) {
            this.ledgerService.getBalance(account.getAddress().toString());
        }
    }

    @Override
    public void shutdown() {
        cacheService.clear();
    }

    @Override
    public void destroy() {
        this.cacheService.destroy();
    }

    @Override
    public String getInfo() {
        //todo
        return null;
    }

    @Override
    public int getVersion() {
        return LedgerConstant.LEDGER_MODULE_VERSION;
    }
}
