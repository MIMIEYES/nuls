package io.nuls.consensus.handler.filter;

import io.nuls.consensus.event.BlockEvent;
import io.nuls.event.bus.filter.NulsEventFilter;
import io.nuls.event.bus.filter.NulsEventFilterChain;

/**
 * @author Niels
 * @date 2017/12/6
 */
public class BlockEventFilter implements NulsEventFilter<BlockEvent> {

    public BlockEventFilter() {

    }

    @Override
    public void doFilter(BlockEvent event, NulsEventFilterChain chain) {
        //todo
        chain.doFilter(event);
    }
}
