import java.awt.image.AreaAveragingScaleFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class MerkleBlock {

    final private int version;
    final private byte[] prev_block;
    final private byte[] merkle_root;
    final private int timestamp;
    final private byte[] bits;
    final private byte[] nonce;
    final private int total_txs;
    final private ArrayList<String> hashes;
    final private byte[] flags;

    private ArrayList<String> tx_hashes;

    final static String GENESIS_BLOCK = "0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4a29ab5f49ffff001d1dac2b7c";
    final static String TESTNET_GENESIS_BLOCK = "0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4adae5494dffff001d1aa4ae18";
    final static String LOWEST_BITS = "ffff001d";




    public MerkleBlock(int version, byte[] prev_block, byte[] merkle_root, int timestamp, byte[] bits, byte[] nonce,
                       int total_txs, ArrayList<String> tx_hashes, byte[] flag_bits) {
        this.version = version;
        this.prev_block = prev_block;
        this.merkle_root = merkle_root;
        this.timestamp = timestamp;
        this.bits = bits;
        this.nonce = nonce;
        this.tx_hashes = tx_hashes;
        this.total_txs = total_txs;
        this.hashes = tx_hashes;
        this.flags = flag_bits;
    }


    public void setTx_hashes(ArrayList<String> tx_hashes) {
        this.tx_hashes = tx_hashes;
    }


    /********************************************************************************/
    public static MerkleBlock parseSerial(ByteArrayInputStream bis) throws IOException {
        var version = Kit.litteEndianBytesToInt(bis.readNBytes(4));
        var prev_block = Kit.reverseBytes(bis.readNBytes(32));
        var merkle_root = Kit.reverseBytes(bis.readNBytes(32));
        var timestamp = Kit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
        var bits = bis.readNBytes(4);
        var nonce = bis.readNBytes(4);

        var total_txs =Kit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
        var num_hashes = Kit.readVarint(bis);

        var hashes = new ArrayList<String>();

        for (int i=0;i<num_hashes;i++) {
            hashes.add(Kit.bytesToHexString(Kit.reverseBytes(bis.readNBytes(32))));
        }

        var flags_length = (int)Kit.readVarint(bis);

        var flags = bis.readNBytes(flags_length);

        return new MerkleBlock(version.intValue(),prev_block,merkle_root,timestamp,bits,nonce,total_txs,hashes,flags);
    }
    /********************************************************************************/
    public static MerkleBlock parseSerial(byte[] serial)  {
        try (var bis = new ByteArrayInputStream(serial)) {
            return parseSerial(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


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

}