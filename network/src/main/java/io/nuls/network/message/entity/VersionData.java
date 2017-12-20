package io.nuls.network.message.entity;

import io.nuls.core.chain.entity.NulsVersion;
import io.nuls.core.context.NulsContext;
import io.nuls.core.crypto.VarInt;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.io.NulsOutputStreamBuffer;
import io.nuls.core.utils.log.Log;
import io.nuls.core.utils.str.StringUtils;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.message.BaseNetworkData;
import io.nuls.network.message.NetworkDataHeader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author vivi
 * @Date 2017.11.01
 */
public class VersionData extends BaseNetworkData {

    public static final short OWN_MAIN_VERSION = 1;

    public static final short OWN_SUB_VERSION = 1001;

    private long bestBlockHeight;

    private String bestBlockHash;

    private String nulsVersion;

    public VersionData() {
        super(OWN_MAIN_VERSION, OWN_SUB_VERSION, NetworkConstant.NETWORK_VERSION_MESSAGE);
        this.nulsVersion = NulsContext.nulsVersion;
    }

    public VersionData(NulsByteBuffer buffer) throws NulsException {
        super(buffer);
    }

    public VersionData(long bestBlockHeight, String bestBlockHash) {
        this();
        this.bestBlockHash = bestBlockHash;
        this.bestBlockHeight = bestBlockHeight;
    }

    @Override
    public int size() {
        int s = 0;
        s += NetworkDataHeader.NETWORK_HEADER_SIZE;

        s += 2;    // version.length
        s += VarInt.sizeOf(bestBlockHeight);
        // put the bestBlockHash.length
        s += 1;
        if (!StringUtils.isBlank(bestBlockHash)) {
            try {
                s += bestBlockHash.getBytes(NulsContext.DEFAULT_ENCODING).length;
            } catch (UnsupportedEncodingException e) {
                Log.error(e);
            }
        }
        s += 1;
        if (!StringUtils.isBlank(nulsVersion)) {
            try {
                s += nulsVersion.getBytes(NulsContext.DEFAULT_ENCODING).length;
            } catch (UnsupportedEncodingException e) {
                Log.error(e);
            }
        }
        return s;
    }

    @Override
    public void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        networkHeader.serializeToStream(stream);
        stream.writeShort(version.getVersion());
        stream.write(new VarInt(bestBlockHeight).encode());
        stream.writeString( bestBlockHash);
        stream.writeString( nulsVersion);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) {
        this.networkHeader = new NetworkDataHeader(byteBuffer);
        version = new NulsVersion(byteBuffer.readShort());
        bestBlockHeight = byteBuffer.readVarInt();
        bestBlockHash = new String(byteBuffer.readByLengthByte());
        nulsVersion = new String(byteBuffer.readByLengthByte());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("versionData:{");
        buffer.append(networkHeader.toString());
        buffer.append("version:" + version.getStringVersion() + ", ");
        buffer.append("bestBlockHeight:" + bestBlockHeight + ", ");
        buffer.append("bestBlockHash:" + bestBlockHash + ", ");
        buffer.append("nulsVersion:" + nulsVersion + "}");

        return buffer.toString();
    }

    public long getBestBlockHeight() {
        return bestBlockHeight;
    }

    public void setBestBlockHeight(long bestBlockHeight) {
        this.bestBlockHeight = bestBlockHeight;
    }

    public String getBestBlockHash() {
        return bestBlockHash;
    }

    public void setBestBlockHash(String bestBlockHash) {
        this.bestBlockHash = bestBlockHash;
    }


    public String getNulsVersion() {
        return nulsVersion;
    }

    public void setNulsVersion(String nulsVersion) {
        this.nulsVersion = nulsVersion;
    }


}
