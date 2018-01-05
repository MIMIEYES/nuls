package io.nuls.core.mesasge;

import io.nuls.core.chain.entity.BaseNulsData;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.crypto.Hex;
import io.nuls.core.utils.crypto.Utils;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.io.NulsOutputStreamBuffer;

import java.io.IOException;

/**
 * @author vivi
 * @date 2017-11-10
 */
public class NulsMessageHeader extends BaseNulsData {

    public static final int MESSAGE_HEADER_SIZE = 10;

    private int magicNumber;

    // the messageBody length
    private int length;

    private byte xor;

    private byte arithmetic;

    public NulsMessageHeader() {
        this.magicNumber = 0;
        this.length = 0;
        this.xor = Hex.decode("00")[0];
        this.arithmetic = 0;
    }

    public NulsMessageHeader(NulsByteBuffer byteBuffer) throws NulsException {
        parse(byteBuffer);
    }

    public NulsMessageHeader(int magicNumber) {
        this();
        this.magicNumber = magicNumber;
    }

    public NulsMessageHeader(int magicNumber, int length) {
        this(magicNumber);
        this.length = length;
    }

    public NulsMessageHeader(int magicNumber, int length, byte xor) {
        this(magicNumber, length);
        this.xor = xor;
    }

    public NulsMessageHeader(int magicNumber, int length, byte xor, byte arithmetic) {
        this(magicNumber, length, xor);
        this.arithmetic = arithmetic;
    }


    @Override
    public int size() {
        return MESSAGE_HEADER_SIZE;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        byte[] header = new byte[MESSAGE_HEADER_SIZE];
        Utils.int32ToByteArrayLE(magicNumber, header, 0);
        Utils.int32ToByteArrayLE(length, header, 4);
        Utils.uint16ToByteArrayLE(xor, header, 8);
        header[9] = xor;
        header[10] = arithmetic;
        stream.write(header);
    }

    @Override
    protected void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.magicNumber = byteBuffer.readInt32LE();
        this.length = byteBuffer.readInt32LE();
        this.xor = byteBuffer.readByte();
        this.arithmetic = byteBuffer.readByte();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("messageHeader:{");
        buffer.append("magicNumber:" + magicNumber + ", ");
        buffer.append("length:" + length + ", ");
        buffer.append("xor:" + xor + ", ");
        buffer.append("arithmetic:" + arithmetic + "}");

        return buffer.toString();
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public byte getXor() {
        return xor;
    }

    public void setXor(byte xor) {
        this.xor = xor;
    }

    public void setArithmetic(byte arithmetic) {
        this.arithmetic = arithmetic;
    }

    public byte getArithmetic() {
        return arithmetic;
    }


}
