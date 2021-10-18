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
        String res="INVALID";
        String script_str = CryptoKit.bytesToHexString(script_pubkey);
        try {
            res= "\nTxOut{" + "amount=" + amount + ", script_pubkey=" + script_str + "}["+Script.parseSerialisation(CryptoKit.addLenPrefix(script_pubkey))+"]";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String printScript() {
        try {
            var script = Script.parseSerialisation(CryptoKit.addLenPrefix(this.script_pubkey));
            return script.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error printing script";
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
        try {
            byte[] buf = CryptoKit.intToLittleEndianBytes(amount);
            // buf is 32 bytes little endian, we need only the first 8
            bos.write(buf,0,8);
            var len = script_pubkey.length;
            bos.write(CryptoKit.encodeVarint(len));
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

    public byte[] getScriptPubkey() {
        return script_pubkey;
    }
}
