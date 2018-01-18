package io.nuls.ledger.module.impl;

import io.nuls.core.chain.manager.TransactionValidatorManager;
import io.nuls.core.context.NulsContext;
import io.nuls.core.thread.manager.TaskManager;
import io.nuls.db.dao.UtxoOutputDataService;
import io.nuls.ledger.constant.LedgerConstant;
import io.nuls.ledger.module.AbstractLedgerModule;
import io.nuls.ledger.service.impl.LedgerCacheService;
import io.nuls.ledger.service.impl.UtxoCoinDataProvider;
import io.nuls.ledger.service.impl.UtxoCoinManager;
import io.nuls.ledger.service.impl.UtxoLedgerServiceImpl;
import io.nuls.ledger.service.intf.CoinDataProvider;
import io.nuls.ledger.service.intf.LedgerService;
import io.nuls.ledger.thread.SmallChangeThread;
import io.nuls.ledger.validator.TxFieldValidator;
import io.nuls.ledger.validator.TxMaxSizeValidator;
import io.nuls.ledger.validator.TxRemarkValidator;
import io.nuls.ledger.validator.TxSignValidator;


/**
 * @author Niels
 * @date 2017/11/7
 */
public class UtxoLedgerModuleBootstrap extends AbstractLedgerModule {

    private LedgerCacheService cacheService;

    private LedgerService ledgerService;

    private UtxoCoinManager coinManager;

    @Override
    public void init() {
        cacheService = LedgerCacheService.getInstance();
        ledgerService = UtxoLedgerServiceImpl.getInstance();
        coinManager = UtxoCoinManager.getInstance();
        UtxoOutputDataService outputDataService = NulsContext.getInstance().getService(UtxoOutputDataService.class);
        coinManager.setOutputDataService(outputDataService);
        ledgerService.init();
        addNormalTxValidator();
        registerService();
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

    @Override
    public void start() {
        coinManager.cacheAllUnSpendOutPut();

        SmallChangeThread smallChangeThread = SmallChangeThread.getInstance();
        TaskManager.createAndRunThread(this.getModuleId(), SmallChangeThread.class.getSimpleName(), smallChangeThread);
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
