/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.event.bus.handler;

import io.nuls.event.bus.event.CommonDigestEvent;
import io.nuls.event.bus.event.GetEventBodyEvent;
import io.nuls.event.bus.service.intf.EventBroadcaster;
import io.nuls.event.bus.service.impl.EventCacheService;
import io.nuls.event.bus.service.impl.EventBroadcasterImpl;

/**
 * @author Niels
 * @date 2017/12/8
 */
public class CommonDigestHandler extends AbstractEventHandler<CommonDigestEvent> {

    private EventCacheService eventCacheService = EventCacheService.getInstance();
    private EventBroadcaster eventBroadcaster = EventBroadcasterImpl.getInstance();

    @Override
    public void onEvent(CommonDigestEvent event, String fromId) {
        boolean exist = eventCacheService.isKnown(event.getEventBody().getDigestHex());
        if (exist) {
            return;
        }
        GetEventBodyEvent getEventBodyEvent = new GetEventBodyEvent();
        getEventBodyEvent.setEventBody(event.getEventBody());
        eventBroadcaster.sendToNode(getEventBodyEvent, fromId);
    }
}
