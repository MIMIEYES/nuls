package io.nuls.event.bus.processor.service.impl;

import io.nuls.core.context.NulsContext;
import io.nuls.core.event.EventManager;
import io.nuls.core.event.BaseNulsEvent;
import io.nuls.core.event.NulsEventHeader;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.log.Log;
import io.nuls.event.bus.constant.EventBusConstant;
import io.nuls.event.bus.event.handler.AbstractNetworkNulsEventHandler;
import io.nuls.event.bus.processor.manager.ProcessData;
import io.nuls.event.bus.processor.manager.ProcessorManager;
import io.nuls.event.bus.processor.service.intf.NetworkProcessorService;
import io.nuls.event.bus.service.impl.EventCacheService;

/**
 *
 * @author Niels
 * @date 2017/11/3
 */
public class NetworkProcessorServiceImpl implements NetworkProcessorService {

    private static final NetworkProcessorServiceImpl INSTANCE = new NetworkProcessorServiceImpl();
    private EventCacheService eventCacheService = EventCacheService.getInstance();
    private final ProcessorManager processorManager;

    private NetworkProcessorServiceImpl() {
        this.processorManager = new ProcessorManager(EventBusConstant.DISRUPTOR_NAME_NETWORK);
    }

    public static NetworkProcessorServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void send(byte[] event,String peerId) {
        try {
            BaseNulsEvent eventObject = EventManager.getInstance(event);
            eventObject.parse(new NulsByteBuffer(event));
            boolean exist = eventCacheService.isKnown(eventObject.getHash().getDigestHex());
            if (exist) {
                return;
            }
            eventCacheService.cacheRecievedEventHash(eventObject.getHash().getDigestHex());
            processorManager.offer(new ProcessData(eventObject,peerId));
        } catch (IllegalAccessException e) {
            Log.error(e);
        } catch (InstantiationException e) {
            Log.error(e);
        }
    }

    @Override
    public String registerEventHandler(Class<? extends BaseNulsEvent> eventClass, AbstractNetworkNulsEventHandler<? extends BaseNulsEvent> handler) {
        return processorManager.registerEventHandler(eventClass, handler);
    }

    @Override
    public void removeEventHandler(String handlerId) {
        processorManager.removeEventHandler(handlerId);
    }

    @Override
    public void shutdown() {
        processorManager.shutdown();
    }
}
