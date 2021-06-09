import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TxIn {
    byte[] prev_tx;
    long prev_index;
    byte[] script_sig;
    long sequence;

    public TxIn(byte[] prev_tx, long prev_index, byte[] script_sig, long sequence) {
        this.prev_tx = prev_tx;
        this.prev_index = prev_index;
        this.script_sig = script_sig;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "TxIn{" +
                "prev_tx='" + prev_tx + '\'' +
                ", prev_index=" + prev_index +
                ", script_sig=" + script_sig +
                ", sequence=" + sequence +
                '}';
    }


    // parses the stream to create a TxIn instance
    public static TxIn parse(ByteArrayInputStream bis) {
        // TODO: check whether long/int types matter
        TxIn tx_input = null;
        try {
            var prev_tx = bis.readNBytes(32);
            var prev_index = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4)).longValue();
            var script_sig_len = (int)CryptoKit.readVarint(bis);
            var script_sig = bis.readNBytes(script_sig_len);
            var sequence = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4)).longValue();
            tx_input = new TxIn(prev_tx,prev_index,script_sig,sequence);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tx_input;
    }


}
