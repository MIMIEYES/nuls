package io.nuls.consensus.handler;

import io.nuls.consensus.entity.tx.PocJoinConsensusTransaction;
import io.nuls.consensus.event.JoinConsensusEvent;
import io.nuls.core.context.NulsContext;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.log.Log;
import io.nuls.core.validate.ValidateResult;
import io.nuls.event.bus.handler.AbstractNetworkEventHandler;
import io.nuls.event.bus.service.intf.NetworkEventBroadcaster;
import io.nuls.ledger.service.intf.LedgerService;

/**
 * @author facjas
 * @date 2017/11/16
 */
public class JoinConsensusHandler extends AbstractNetworkEventHandler<JoinConsensusEvent> {

    private LedgerService ledgerService = NulsContext.getInstance().getService(LedgerService.class);
    private NetworkEventBroadcaster networkEventBroadcaster = NulsContext.getInstance().getService(NetworkEventBroadcaster.class);

    @Override
    public void onEvent(JoinConsensusEvent event, String fromId) {
        PocJoinConsensusTransaction tx = event.getEventBody();
        ValidateResult result;
        try {
            result = ledgerService.verifyAndCacheTx(tx);
        } catch (NulsException e) {
            Log.error(e);
            return;
        }
        if (null==result||result.isFailed()) {
            return;
        }
        this.networkEventBroadcaster.broadcastHashAndCache(event);
    }
}
