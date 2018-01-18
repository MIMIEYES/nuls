package io.nuls.event.bus.filter;

import io.nuls.core.event.BaseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels
 * @date 2017/11/7
 */
public class NulsEventFilterChain {

    private List<NulsEventFilter> list = new ArrayList<>();
    private ThreadLocal<Integer> index = new ThreadLocal<>();

    public boolean startDoFilter(BaseEvent event) {
        index.set(-1);
        doFilter(event);
        boolean result = index.get() == list.size();
        index.remove();
        return result;
    }

    public void doFilter(BaseEvent event) {
        index.set(1 + index.get());
        if (index.get() == list.size()) {
            return;
        }
        NulsEventFilter filter = list.get(index.get());
        filter.doFilter(event, this);
    }

    public void addFilter(NulsEventFilter<? extends BaseEvent> filter) {
        list.add(filter);
    }
}
