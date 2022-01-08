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
            var version = Kit.litteEndianBytesToInt(bis.readNBytes(4));
            var prev_block = Kit.reverseBytes(bis.readNBytes(32));
            var merkle_root = Kit.reverseBytes(bis.readNBytes(32));
            var timestamp = Kit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
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

        var n = Kit.intToLittleEndianBytes(this.version);
        var nb = Arrays.copyOfRange(n,0,4);
        try {
            bos.write(nb);
            bos.write(Kit.reverseBytes(this.prev_block));
            bos.write(Kit.reverseBytes(merkle_root));
            bos.write(Arrays.copyOfRange(Kit.intToLittleEndianBytes(timestamp),0,4));
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

    public String getSerialString() {
        return Kit.bytesToHexString(this.serialize());
    }

    /********************************************************************************/
    public byte[] hash256() {
        var s = this.serialize();
        return Kit.reverseBytes(Kit.hash256(s));
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
        var coeff = Kit.litteEndianBytesToInt(Arrays.copyOfRange(bits,0,3));
        target = coeff.multiply(BigInteger.valueOf(256).pow(exp-3));

        return target;
    }

    public BigInteger getTarget() {
        return Block.bitsToTarget(this.bits);
    }

    public String getTargetHexString() {
        var ba = getTarget().toByteArray();
        ba = Kit.to32bytes(ba);
        return Kit.bytesToHexString(ba);
    }
    /********************************************************************************/
    public String getHashHexString() {
        var ba = this.hash256();
        return Kit.bytesToHexString(ba);
    }

    /********************************************************************************/
    public double difficulty() {
        var f = new BigInteger(1, Kit.hexStringToByteArray("ffff"));

        var n = f.multiply(BigInteger.valueOf(256).pow(0x1d-3));

        return n.divide(this.getTarget()).doubleValue();
    }
    /********************************************************************************/

    public boolean checkPoW() {

        var hash = this.hash256();
        var proof = Kit.litteEndianBytesToInt(hash);

        return proof.compareTo(this.getTarget())<0;
    }
    /********************************************************************************/

    public static byte[] computeNewBits(byte[] prev_bits, int time_diff) {
        final int TWO_WEEKS = 60*60*24*14;

        if (time_diff>TWO_WEEKS*4) time_diff = TWO_WEEKS*4;
        if (time_diff<TWO_WEEKS/4) time_diff = TWO_WEEKS/4;

        var prev_target = Block.bitsToTarget(prev_bits);
        var new_target = prev_target.multiply(BigInteger.valueOf(time_diff)).divide(BigInteger.valueOf(TWO_WEEKS));

        return Block.targetToBits(new_target);
    }
    /********************************************************************************/
    /*  Converts target int bits format
     */
    public static byte[] targetToBits(BigInteger target) {
        var raw_bytes = target.toByteArray();

        var bos = new ByteArrayOutputStream();

        // target is always positive, so if the first bit is 1
        // we shift so it's not considered as sign
        if ((raw_bytes[0] & 0x80) == 0x80)
            bos.write(0);
        try {
            bos.write(raw_bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        raw_bytes = bos.toByteArray();

        var exponent = raw_bytes.length;
        // the first three digits of the number in 256 base
        var coeff = Arrays.copyOfRange(raw_bytes,0,3);

        bos = new ByteArrayOutputStream();
        try {
            // coeff is in little endian
            bos.write(Kit.reverseBytes(coeff));
            bos.write((byte)exponent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        raw_bytes = bos.toByteArray();

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
        return "\nBlock\n{" +
                "\nversion: " + version +
                "\nprev_block: " + Kit.bytesToHexString(prev_block) +
                "\nmerkle_root: " + Kit.bytesToHexString(merkle_root) +
                "\ntimestamp: " + timestamp +
                "\nbits: " + Kit.bytesToHexString(bits) +
                "\nnonce: " + Kit.bytesToHexString(nonce) +
                "\n}";
    }
}