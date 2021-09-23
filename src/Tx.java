import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/*****************************************************************/
public class Tx {
    private final int version;
    public final ArrayList<TxIn> tx_ins;
    public final ArrayList<TxOut> tx_outs;
    private long locktime;
    private final boolean testnet;
    private byte [] serialized;

    /*****************************************************************/
    public Tx(int version, ArrayList<TxIn> tx_ins, ArrayList<TxOut> tx_outs, long locktime, boolean testnet) {
        this.version = version;
        this.tx_ins = tx_ins;
        this.tx_outs = tx_outs;
        this.locktime = locktime;
        this.testnet = testnet;
        this.serialized = serialize();
    }

    /*****************************************************************/
    public void updateLockTime(long locktime) {
        this.locktime = locktime;
        this.serialized = serialize();
    }

    /*****************************************************************/
    public String getId() {
        String hex = CryptoKit.bytesToHexString(this.hash());
        return hex;
    }

    /*****************************************************************/
    public byte[] hash() {
        return CryptoKit.hash256(this.serialized);
    }


    /*****************************************************************/
    // parses a stream to construct a Tx instance
    static public Tx parse(byte[] serialization, boolean testnet) {

        // read serialized version into the bytestream to create a corresponding tx
        var bis = new ByteArrayInputStream(serialization);

        Tx tx = null;

        try {
            var version = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();

            // TODO: check whether varint should be little endian (see the other)
            var num_inputs = CryptoKit.readVarint(bis);
            ArrayList<TxIn> inputs = new ArrayList<>();

            for (int i=0;i<num_inputs;i++) {
                inputs.add(TxIn.parse(bis));
            }

            var num_outputs = CryptoKit.readVarint(bis);
            ArrayList<TxOut> outputs = new ArrayList<>();
            for (int i=0;i<num_outputs;i++) {
                outputs.add(TxOut.parse(bis));
            }

            var locktime = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4)).longValue();

            tx = new Tx(version,inputs,outputs,locktime, testnet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tx;
    }

    /*****************************************************************/
    public String getSerialString() {
        return CryptoKit.bytesToHexString(this.serialized);
    }

    /*****************************************************************/
    private byte[] serialize() {
        var bos = new ByteArrayOutputStream();

        try {
            byte[] buf = CryptoKit.intToLittleEndianBytes(version);
            bos.write(buf,0,4);

            // TODO: check whether varint should be little endian (see the other)
            int txins_len = 0;
            for (TxIn txin: tx_ins)
                txins_len+= txin.getSerialized().length;
            bos.write(CryptoKit.encodeVarint(txins_len));
            for (TxIn txin: tx_ins)
                bos.write(txin.getSerialized());

            int txouts_len = 0;
            for (TxOut txout: tx_outs)
                txouts_len+= txout.getSerialized().length;
            bos.write(CryptoKit.encodeVarint(txouts_len));
            for (TxOut txout: tx_outs)
                bos.write(txout.getSerialized());

            buf = CryptoKit.intToLittleEndianBytes(this.locktime);
            bos.write(buf,0,4);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    /*****************************************************************/
    @Override
    public String toString() {
        return "Tx{\n" +
                "version='" + version + '\'' +
                ",\n tx_ins=" + tx_ins +
                ",\n tx_outs=" + tx_outs +
                ",\n locktime=" + locktime +
                ",\n testnet=" + testnet +
                '}';
    }

    /*****************************************************************/
    public long calculateFee() {
        // in satoshis
        long total_in = 0;
        long total_out = 0;
        for (TxIn txin: tx_ins) {
            total_in+= txin.getValue(false);
        }

        for (TxOut txout: tx_outs) {
            total_out+= txout.getAmount();
        }

        long fee = total_in-total_out;
        return fee;
    }
}
