package io.nuls.event.bus.processor.thread;

import io.nuls.core.utils.log.Log;
import io.nuls.event.bus.handler.intf.NulsEventHandler;
import io.nuls.event.bus.processor.manager.ProcessData;

/**
 * @author Niels
 * @date 2017/11/6
 */
public class NulsEventCall<T extends io.nuls.core.event.BaseEvent> implements Runnable {
    private final ProcessData<T> data;
    private final NulsEventHandler<T> handler;

    public NulsEventCall(ProcessData<T> data, NulsEventHandler<T> handler) {
        this.data = data;
        this.handler = handler;
    }

    @Override
    public void run() {
        if (null == data || null == handler) {
            return;
        }
        try {
            //todo filter&handler is the same level
            boolean ok = handler.getFilterChain().startDoFilter(data.getData());
            if (ok) {
                handler.onEvent(data.getData(), data.getNodeId());
            }
        } catch (Exception e) {
            Log.error(e);
        }
        return;
    }
}
