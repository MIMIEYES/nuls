package io.nuls.network.message.entity;

import io.nuls.core.chain.entity.BaseNulsData;
import io.nuls.core.chain.entity.NulsVersion;
import io.nuls.core.context.NulsContext;
import io.nuls.core.crypto.VarInt;
import io.nuls.core.event.BaseNetworkEvent;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.io.NulsOutputStreamBuffer;
import io.nuls.core.utils.log.Log;
import io.nuls.core.utils.str.StringUtils;
import io.nuls.network.constant.NetworkConstant;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author vivi
 * @Date 2017.11.01
 */
public class VersionData extends BaseNetworkEvent {

    public static final short OWN_MAIN_VERSION = 1;

    public static final short OWN_SUB_VERSION = 1001;

    private long bestBlockHeight;

    private String bestBlockHash;

    private String nulsVersion;

    public VersionData(short moduleId, short eventType) {
        super(moduleId, eventType);
    }


    @Override
    protected BaseNulsData parseEventBody(NulsByteBuffer byteBuffer) throws NulsException {
        return null;
    }

    @Override
    public Object copy() {
        return null;
    }
}
