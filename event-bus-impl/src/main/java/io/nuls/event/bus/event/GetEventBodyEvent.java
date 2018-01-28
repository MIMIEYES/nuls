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
package io.nuls.event.bus.event;

import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.constant.NulsConstant;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.event.bus.constant.EventConstant;

/**
 * @author Niels
 * @date 2017/12/8
 */
public class GetEventBodyEvent extends io.nuls.core.event.BaseEvent<NulsDigestData> {

    public GetEventBodyEvent() {
        super(NulsConstant.MODULE_ID_EVENT_BUS, EventConstant.EVENT_TYPE_GET_EVENT_BODY_EVENT);
    }

    @Override
    protected NulsDigestData parseEventBody(NulsByteBuffer byteBuffer) throws NulsException {
        return byteBuffer.readHash();
    }

    @Override
    public Object copy() {
        GetEventBodyEvent event = new GetEventBodyEvent();
        NulsDigestData data = new NulsDigestData(this.getEventBody().getWholeBytes());
        event.setEventBody(data);
        return event;
    }
}
