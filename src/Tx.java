import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/*****************************************************************/
public class Tx {
    private final int version;
    private final ArrayList<TxIn> tx_ins;
    private final ArrayList<TxOut> tx_outs;
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
        var hash_bytes = this.hash();
        // "Due to historical accident, the tx and block hashes that bitcoin core uses are byte-reversed.
        // I’m not entirely sure why. May be something like using openssl bignum to store hashes or something like that,
        // then printing them as a number."

        // Wladimir van der Laan (Bitcoin Core developer)
        return CryptoKit.bytesToHexString(CryptoKit.reverseBytes(hash_bytes));
    }

    /*****************************************************************/
    public byte[] hash() {
        return CryptoKit.hash256(this.serialized);
    }



    /*****************************************************************/
    public boolean verify() {
        // we make sure we are not creating money
        if (this.calculateFee()<0)
            return false;

        // each input has the correct scriptsig
        for (int i=0;i<tx_ins.size();i++)
            if (!this.verifyInput(i)) return false;
        return true;
    }


    /*****************************************************************/
    public boolean verifyInput(int input_index) {
        boolean eval = false;
        var tx_in = tx_ins.get(input_index);
        var prev_script_pubkey = tx_in.getPreviousTxScriptPubKey(false);
        var prevspk = CryptoKit.bytesToHexString(prev_script_pubkey);
        var z = this.getSigHash(input_index);
        try {
            var script_sig = Script.parseSerial(CryptoKit.addLenPrefix(tx_in.getScriptSig()));
            var script_combined = Script.parseSerial(CryptoKit.addLenPrefix(prev_script_pubkey));
            script_combined.addTop(script_sig);
            eval = script_combined.evaluate(z);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return eval;
    }

    public ArrayList<TxIn> getTxIns() {
        return tx_ins;
    }

    public int getVersion() {
        return version;
    }

    public long getLocktime() {
        return locktime;
    }

    public void setLocktime(long locktime) {
        this.locktime = locktime;
    }

    public boolean isTestnet() {
        return testnet;
    }

    public ArrayList<TxOut> getTxOuts() {
        return tx_outs;
    }

    /*****************************************************************/
    //Returns the integer representation of the hash that needs to get signed for index input_index
    public byte[] getSigHash(int input_index) {
       byte[] x = null;

        var bos = new ByteArrayOutputStream();

        try {
            byte[] buf = CryptoKit.intToLittleEndianBytes(version);
            bos.write(buf,0,4);

            int num_ins = tx_ins.size();
            bos.write(CryptoKit.encodeVarint(num_ins));
            for (int i=0;i < num_ins; i++) {

                var prev_tx = tx_ins.get(i).getPrev_tx_id();
                var pre_index = tx_ins.get(i).getPrev_index();
                var sequence = tx_ins.get(i).getSequence();


                // the script_sig of the index_input to be signed must be
                // replaced with the script_pubkey found in the previous transaction output
                // otherwise should be zero byte
                if (i==input_index) {
                    var script_pubkey = tx_ins.get(i).getPreviousTxScriptPubKey(this.isTestnet());
                    var tx_in = new TxIn(prev_tx,pre_index,script_pubkey,sequence);
                    bos.write(tx_in.getSerialized());
                }
                else {
                    byte[] zero = {0};
                    var tx_in = new TxIn(prev_tx,pre_index,zero,sequence);
                    bos.write(tx_in.getSerialized());
                }

            }

            int num_outs = tx_outs.size();
            bos.write(CryptoKit.encodeVarint(num_outs));
            for (TxOut txout: tx_outs)
                bos.write(txout.getSerialized());

            buf = CryptoKit.intToLittleEndianBytes(this.locktime);
            bos.write(buf,0,4);

            // SIGHASH_ALL hash type
            buf = CryptoKit.intToLittleEndianBytes(1);
            bos.write(buf,0,4);

            x= (CryptoKit.hash256(bos.toByteArray()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return x;
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


            var original_ser = CryptoKit.bytesToHexString(serialization);
            var created_ser = tx.getSerialString();


            // sanity check, serialization should match the parsed one
            if (!created_ser.equals(original_ser)) {
                System.out.println("wARNING: mismatching serializations (segwit tx?)");
                System.out.println("Original:"+original_ser);
                System.out.println(" Created:"+created_ser);
                //System.exit(-1);
            }
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

            int num_ins = tx_ins.size();
            bos.write(CryptoKit.encodeVarint(num_ins));
            for (TxIn txin: tx_ins)
                bos.write(txin.getSerialized());

            int num_outs = tx_outs.size();
            bos.write(CryptoKit.encodeVarint(num_outs));
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
        return "Tx (id:"+this.getId()+")"+
                ",\n{ version='" + version + '\'' +
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

        return total_in-total_out;
    }
}

