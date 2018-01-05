package io.nuls.consensus.event;

import io.nuls.consensus.constant.ConsensusEventType;
import io.nuls.core.chain.entity.BasicTypeData;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.io.NulsByteBuffer;

/**
 * @author Niels
 * @date 2017/12/11
 */
public class GetBlockHeaderEvent extends BaseConsensusEvent<BasicTypeData<Long>> {
    public GetBlockHeaderEvent() {
        super(ConsensusEventType.GET_BLOCK_HEADER);
    }

    public GetBlockHeaderEvent(long height) {
        this();
        this.setEventBody(new BasicTypeData<>(height));
    }

    @Override
    protected BasicTypeData<Long> parseEventBody(NulsByteBuffer byteBuffer) throws NulsException {
        if (byteBuffer.isFinished()) {
            return null;
        }
        return new BasicTypeData(byteBuffer);
    }

}
