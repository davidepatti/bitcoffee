import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Tx {
    byte[] version;
    ArrayList<TxIn> tx_ins;
    ArrayList<String> tx_outs;
    long locktime;
    boolean testnet;

    public Tx(byte[] version, ArrayList<TxIn> tx_ins, ArrayList<String> tx_outs, long locktime, boolean testnet) {
        this.version = version;
        this.tx_ins = tx_ins;
        this.tx_outs = tx_outs;
        this.locktime = locktime;
        this.testnet = testnet;
    }

    public String getId() {
        String hex = Hex.toHexString(this.hash());
        return hex;
    }

    public byte[] hash() {
        byte[] serialized = CryptoKit.hexStringToByteArray("ffaa");
        return CryptoKit.hash256(serialized);
    }


    // parses a stream to construct a Tx instance
    static public Tx parse(byte[] serialization) {

        var bis = new ByteArrayInputStream(serialization);

        Tx tx = null;

        try {
            var version = bis.readNBytes(4);
            var num_inputs = CryptoKit.readVarint(bis);
            ArrayList<TxIn> tx_ins = new ArrayList<>();

            for (int i=0;i<num_inputs;i++) {
                tx_ins.add(TxIn.parse(bis));
            }

            tx = new Tx(version,tx_ins,null,0, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tx;
    }

    @Override
    public String toString() {
        return "Tx{" +
                "version='" + version + '\'' +
                ", tx_ins=" + tx_ins +
                ", tx_outs=" + tx_outs +
                ", locktime=" + locktime +
                ", testnet=" + testnet +
                '}';
    }
}
