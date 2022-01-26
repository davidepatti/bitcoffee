import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

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
        String res="INVALID";
        var script = new Script(script_pubkey);
        String script_str = Kit.bytesToHexString(script_pubkey);
        res= "\nTxOut{" + "amount=" + amount + ", script_pubkey=" + script_str + "}["+script+"]";
        return res;
    }

    public static TxOut parse(ByteArrayInputStream bis) {
        TxOut txout = null;

        try {
            var amount = Kit.litteEndianBytesToInt(bis.readNBytes(8)).longValue();
            var script_len = (int) Kit.readVarint(bis);
            var script_pub_key = bis.readNBytes(script_len);
            txout = new TxOut(amount,script_pub_key);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return txout;
    }

    private byte[] serialize() {

        var bos = new ByteArrayOutputStream();
        try {
            byte[] buf = Kit.intToLittleEndianBytes(amount);
            // buf is 32 bytes little endian, we need only the first 8
            bos.write(buf,0,8);
            var len = script_pubkey.length;
            bos.write(Objects.requireNonNull(Kit.encodeVarint(len)));
            bos.write(this.script_pubkey);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    public byte[] getSerialized() {
        return serialized;
    }

    public long getAmount() {
        return amount;
    }

    public byte[] getScriptPubkeyBytes() {
        return script_pubkey;
    }

    public Script getScriptPubKey() {
        return new Script(this.script_pubkey);
    }
}
