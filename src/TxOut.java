import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TxOut {
    // TODO check in general for final private in other places
    final private long amount;
    final private byte[] script_pubkey;
    final private byte[] serialized;

    public TxOut(long amount, byte[] script_pubkey) {
        this.amount = amount;
        this.script_pubkey = script_pubkey;
        this.serialized = serialize();
    }

    @Override
    public String toString() {
        String script_str = Hex.toHexString(script_pubkey);
        return "\nTxOut{" + "amount=" + amount + ", script_pubkey=" + script_str + '}';
    }



    public static TxOut parse(ByteArrayInputStream bis) {
        TxOut txout = null;

        try {
            var amount = CryptoKit.litteEndianBytesToInt(bis.readNBytes(8)).longValue();
            var script_len = (int)CryptoKit.readVarint(bis);
            var script_pub_key = bis.readNBytes(script_len);

            txout = new TxOut(amount,script_pub_key);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return txout;
    }

    private byte[] serialize() {
        var bos = new ByteArrayOutputStream();

        byte[] buf = CryptoKit.intToLittleEndianBytes(amount);

        Script script = new Script(script_pubkey);
        // buf is 32 bytes little endian, we need only the first 8
        try {
            bos.write(buf,0,8);
            bos.write(script.serialize());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();

    }

    public byte[] getSerialized() {
        return serialized;
    }
}
