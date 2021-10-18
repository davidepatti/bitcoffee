import java.awt.image.AreaAveragingScaleFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class Block {

    final private int version;
    final private byte[] prev_block;
    final private byte[] merkle_root;
    final private int timestamp;
    final private byte[] bits;
    final private byte[] nonce;

    /********************************************************************************/
    public Block(int version, byte[] prev_block, byte[] merkle_root, int timestamp, byte[] bits, byte[] nonce) {
        this.version = version;
        this.prev_block = prev_block;
        this.merkle_root = merkle_root;
        this.timestamp = timestamp;
        this.bits = bits;
        this.nonce = nonce;
    }

    /********************************************************************************/
    public static Block parseSerial(byte[] serial)  {
        try (var bis = new ByteArrayInputStream(serial)) {
            var version = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4));
            var prev_block = CryptoKit.reverseBytes(bis.readNBytes(32));
            var merkle_root = CryptoKit.reverseBytes(bis.readNBytes(32));
            var timestamp = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
            var bits = bis.readNBytes(4);
            var nonce = bis.readNBytes(4);
            return new Block(version.intValue(),prev_block,merkle_root,timestamp,bits,nonce);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /********************************************************************************/
    public byte[] serialize() {
        var bos = new ByteArrayOutputStream();

        var n = CryptoKit.intToLittleEndianBytes(this.version);
        var nb = Arrays.copyOfRange(n,0,4);
        try {
            bos.write(nb);
            bos.write(CryptoKit.reverseBytes(this.prev_block));
            bos.write(CryptoKit.reverseBytes(merkle_root));
            bos.write(Arrays.copyOfRange(CryptoKit.intToLittleEndianBytes(timestamp),0,4));
            bos.write(bits);
            bos.write(nonce);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bos.toByteArray().length!=80) {
            System.out.println("FATAL: Wrong size in block header serialization ");
            System.out.println("Expected: 80, Computed: "+bos.toByteArray().length);
            System.exit(-1);
        }

        return bos.toByteArray();
    }

    /********************************************************************************/
    public byte[] hash256() {
        var s = this.serialize();
        return CryptoKit.reverseBytes(CryptoKit.hash256(s));
    }
    /********************************************************************************/

    public boolean checkBIP9() {
        // the first 3 bit of the 4 bytes header in little endian must be 001
        int v = version >> 29;
        return (v==1);
    }
    /********************************************************************************/

    public boolean checkBIP91(){
        // 5th bit from right must be 1
        int v =version>>4;
        return ((v & 1) == 1);
    }
    /********************************************************************************/

    public boolean checkBIP141(){
        // bit 1 must be 1

        return ((version>>1 & 1)==1);
    }
    /********************************************************************************/

    public static BigInteger bitsToTarget(byte[] bits) {
        BigInteger target;

        var exp = bits[3];
        var coeff = CryptoKit.litteEndianBytesToInt(Arrays.copyOfRange(bits,0,3));
        target = coeff.multiply(BigInteger.valueOf(256).pow(exp-3));

        return target;
    }

    public BigInteger getTarget() {
        return Block.bitsToTarget(this.bits);
    }

    public String getTargetHexString() {
        var ba = getTarget().toByteArray();
        ba = CryptoKit.to32bytes(ba);
        return CryptoKit.bytesToHexString(ba);
    }
    /********************************************************************************/
    public String getHashHexString() {
        var ba = this.hash256();
        return CryptoKit.bytesToHexString(ba);
    }

    /********************************************************************************/
    public double difficulty() {
        var f = new BigInteger(1,CryptoKit.hexStringToByteArray("ffff"));

        var n = f.multiply(BigInteger.valueOf(256).pow(0x1d-3));

        return n.divide(this.getTarget()).doubleValue();
    }
    /********************************************************************************/

    public boolean checkPoW() {

        var hash = this.hash256();
        var proof = CryptoKit.litteEndianBytesToInt(hash);

        return proof.compareTo(this.getTarget())<0;
    }
    /********************************************************************************/

    public static byte[] computeNewBits(byte[] prev_bits, int time_diff) {
        final int TWO_WEEKS = 60*60*24*14;

        if (time_diff>TWO_WEEKS*4) time_diff = TWO_WEEKS*4;
        if (time_diff<TWO_WEEKS/4) time_diff = TWO_WEEKS/4;

        var prev_target = Block.bitsToTarget(prev_bits);
        var new_target = prev_target.multiply(BigInteger.valueOf(time_diff)).divide(BigInteger.valueOf(TWO_WEEKS));
        var new_bits = Block.targetToBits(new_target);

        return new_bits;
    }
    /********************************************************************************/
    public static byte[] targetToBits(BigInteger target) {
        var raw_bytes = target.toByteArray();

        var bos = new ByteArrayOutputStream();

        if (raw_bytes[0]>0x7f)
            bos.write(0);
        try {
            bos.write(raw_bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        raw_bytes = bos.toByteArray();

        var exponent = raw_bytes.length;
        var coeff = CryptoKit.reverseBytes(Arrays.copyOfRange(raw_bytes,0,3));

        bos = new ByteArrayOutputStream();
        try {
            bos.write(coeff);
            bos.write((byte)exponent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        raw_bytes = Arrays.copyOfRange(bos.toByteArray(),0,4);

        return raw_bytes;
    }
    /********************************************************************************/

    public int getVersion() {
        return version;
    }

    public byte[] getPrev_block() {
        return prev_block;
    }

    public byte[] getMerkle_root() {
        return merkle_root;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public byte[] getBits() {
        return bits;
    }

    public byte[] getNonce() {
        return nonce;
    }

    @Override
    public String toString() {
        return "\nBlock{" +
                "\nversion=" + version +
                ",\nprev_block=" + CryptoKit.bytesToHexString(prev_block) +
                ",\nmerkle_root=" + CryptoKit.bytesToHexString(merkle_root) +
                ",\ntimestamp=" + timestamp +
                ",\nbits=" + CryptoKit.bytesToHexString(bits) +
                ",\nnonce=" + CryptoKit.bytesToHexString(nonce) +
                '}';
    }
}