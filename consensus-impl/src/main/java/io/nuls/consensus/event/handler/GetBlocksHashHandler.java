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
package io.nuls.consensus.event.handler;

import io.nuls.consensus.entity.BlockHashResponse;
import io.nuls.consensus.event.BlocksHashEvent;
import io.nuls.consensus.event.GetBlocksHashRequest;
import io.nuls.consensus.service.intf.BlockService;
import io.nuls.core.chain.entity.Block;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.context.NulsContext;
import io.nuls.event.bus.handler.AbstractEventHandler;
import io.nuls.event.bus.service.intf.EventBroadcaster;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels
 * @date 2018/1/16
 */
public class GetBlocksHashHandler extends AbstractEventHandler<GetBlocksHashRequest> {

    private BlockService blockService = NulsContext.getInstance().getService(BlockService.class);
    private EventBroadcaster eventBroadcaster = NulsContext.getInstance().getService(EventBroadcaster.class);

    @Override
    public void onEvent(GetBlocksHashRequest event, String fromId) {

        boolean b = event.getStart() == event.getEnd();
        if (b) {
            BlockHashResponse response = new BlockHashResponse();
            Block block;
            if (event.getEnd() == 0) {
                block = blockService.getLocalBestBlock();
            } else {
                block = blockService.getBlock(event.getEnd());
            }
            if (null == block) {
                return;
            }
            response.put(block.getHeader().getHeight(), block.getHeader().getHash());
            sendResponse(response,fromId);
        } else {
            List<NulsDigestData> list = this.blockService.getBlockHashList(event.getStart(), event.getEnd(), event.getSplit());
            List<Long> resultHeightList = new ArrayList<>();
            List<NulsDigestData> resultHashList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                long height = i + event.getStart();
                if (i % event.getSplit() == 0) {
                    resultHeightList.add(height);
                    resultHashList.add(list.get(i));
                }
            }
            final int size = 50000;
            for (int i = 0; i < resultHashList.size(); i += size) {
                BlockHashResponse response = new BlockHashResponse();
                int end = i + size;
                if (end > resultHeightList.size()) {
                    end = resultHeightList.size();
                }
                response.setHeightList(resultHeightList.subList(i, end));
                response.setHashList(resultHashList.subList(i, end));
                sendResponse(response,fromId);
            }
        }
    }

    private void sendResponse(BlockHashResponse response, String fromId) {
        BlocksHashEvent event = new BlocksHashEvent();
        event.setEventBody(response);
        eventBroadcaster.sendToNode(event,fromId);
    }
}
