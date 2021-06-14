import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Tx {
    private final byte[] version;
    public final ArrayList<TxIn> tx_ins;
    public final ArrayList<TxOut> tx_outs;
    private final long locktime;
    private final boolean testnet;
    private final byte [] serialized;

    public Tx(byte[] version, ArrayList<TxIn> tx_ins, ArrayList<TxOut> tx_outs, long locktime, boolean testnet) {
        this.version = version;
        this.tx_ins = tx_ins;
        this.tx_outs = tx_outs;
        this.locktime = locktime;
        this.testnet = testnet;
        this.serialized = serialize();
    }

    public String getId() {
        String hex = Hex.toHexString(this.hash());
        return hex;
    }

    public byte[] hash() {
        return CryptoKit.hash256(this.serialized);
    }


    // parses a stream to construct a Tx instance
    static public Tx parse(byte[] serialization, boolean testnet) {

        var bis = new ByteArrayInputStream(serialization);

        Tx tx = null;

        try {
            var version = bis.readNBytes(4);
            var num_inputs = CryptoKit.readVarint(bis);
            ArrayList<TxIn> inputs = new ArrayList<>();
            ArrayList<TxOut> outputs = new ArrayList<>();

            for (int i=0;i<num_inputs;i++) {
                inputs.add(TxIn.parse(bis));
            }

            var num_outputs = CryptoKit.readVarint(bis);
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

    private byte[] serialize() {
        var bos = new ByteArrayOutputStream();

        try {
            bos.write(version);

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

            byte[] buf = CryptoKit.intToLittleEndianBytes(this.locktime);
            bos.write(buf,0,4);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @Override
    public String toString() {
        String version_str = Hex.toHexString(version);
        return "Tx{\n" +
                "version='" + version_str + '\'' +
                ",\n tx_ins=" + tx_ins +
                ",\n tx_outs=" + tx_outs +
                ",\n locktime=" + locktime +
                ",\n testnet=" + testnet +
                '}';
    }

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
