package io.nuls.consensus.handler;

import io.nuls.consensus.entity.tx.RegisterAgentTransaction;
import io.nuls.consensus.event.RegisterAgentEvent;
import io.nuls.core.context.NulsContext;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.log.Log;
import io.nuls.event.bus.handler.AbstractNetworkEventHandler;
import io.nuls.event.bus.service.intf.NetworkEventBroadcaster;
import io.nuls.ledger.service.intf.LedgerService;

/**
 *
 * @author facjas
 * @date 2017/11/16
 */
public class RegisterAgentHandler extends AbstractNetworkEventHandler<RegisterAgentEvent> {
    private LedgerService ledgerService = NulsContext.getInstance().getService(LedgerService.class);
    private NetworkEventBroadcaster networkEventBroadcaster = NulsContext.getInstance().getService(NetworkEventBroadcaster.class);

    @Override
    public void onEvent(RegisterAgentEvent event,String fromId)   {
        RegisterAgentTransaction tx = event.getEventBody();
        try {
            ledgerService.verifyAndCacheTx(tx);
        } catch (NulsException e) {
            Log.error(e);
            return;
        }
        this.networkEventBroadcaster.broadcastHashAndCache(event);
    }
}
