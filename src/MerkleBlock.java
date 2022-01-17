import java.awt.image.AreaAveragingScaleFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MerkleBlock {

    final private int version;
    final private String prev_block;
    final private String merkle_root;
    final private int timestamp;
    final private byte[] bits;
    final private byte[] nonce;
    final private int total_txs;
    final private String flags;
    private ArrayList<String> tx_hashes;

    final static String GENESIS_BLOCK = "0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4a29ab5f49ffff001d1dac2b7c";
    final static String TESTNET_GENESIS_BLOCK = "0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4adae5494dffff001d1aa4ae18";
    final static String LOWEST_BITS = "ffff001d";




    public MerkleBlock(int version, String prev_block, String merkle_root, int timestamp, byte[] bits, byte[] nonce,
                       int total_txs, ArrayList<String> tx_hashes, String flag_bits) {
        this.version = version;
        this.prev_block = prev_block;
        this.merkle_root = merkle_root;
        this.timestamp = timestamp;
        this.bits = bits;
        this.nonce = nonce;
        this.tx_hashes = tx_hashes;
        this.total_txs = total_txs;
        this.tx_hashes = tx_hashes;
        this.flags = flag_bits;
    }


    public void setTx_hashes(ArrayList<String> tx_hashes) {
        this.tx_hashes = tx_hashes;
    }


    /********************************************************************************/
    public static MerkleBlock parseSerial(ByteArrayInputStream bis) throws IOException {
        var version = Kit.litteEndianBytesToInt(bis.readNBytes(4));
        var version_str = version.toString(16);

        var prev_block = Kit.bytesToHexString(Kit.reverseBytes(bis.readNBytes(32)));
        var merkle_root = Kit.bytesToHexString(Kit.reverseBytes(bis.readNBytes(32)));
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

        var flags = Kit.bytesToHexString(bis.readNBytes(flags_length));

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

    public boolean isValid() {

        ArrayList<String> rev_hash_list = new ArrayList<>();

        rev_hash_list.addAll(this.tx_hashes);
        Collections.reverse(rev_hash_list);

        var mt = new MerkleTree(this.total_txs);
        mt.populateTree(Kit.hexStringToByteArray(flags),rev_hash_list);

        var computed_root = mt.getRoot();
        var root_str = this.merkle_root;

        return computed_root.equals(root_str);
    }


    public int getVersion() {
        return version;
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

    public ArrayList<String> getTx_hashes() {
        return tx_hashes;
    }

    public String getMerkle_root() {
        return merkle_root;
    }

    public String getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return "MerkleBlock{" +
                "version=" + version +
                ", prev_block='" + prev_block + '\'' +
                ", merkle_root='" + merkle_root + '\'' +
                ", timestamp=" + timestamp +
                ", bits=" + Arrays.toString(bits) +
                ", nonce=" + Arrays.toString(nonce) +
                ", total_txs=" + total_txs +
                ", flags='" + flags + '\'' +
                ", tx_hashes=" + tx_hashes +
                '}';
    }
}