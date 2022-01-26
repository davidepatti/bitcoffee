import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/*****************************************************************/
public class Tx implements Message {
    private final int version;
    private final ArrayList<TxIn> tx_ins;
    private final ArrayList<TxOut> tx_outs;
    private long locktime;
    private final boolean testnet;

    public final static String COMMAND = "tx";

    /*****************************************************************/
    public Tx(int version, ArrayList<TxIn> tx_ins, ArrayList<TxOut> tx_outs, long locktime, boolean testnet) {
        this.version = version;
        this.tx_ins = tx_ins;
        this.tx_outs = tx_outs;
        this.locktime = locktime;
        this.testnet = testnet;
    }

    /*****************************************************************/
    public void updateLockTime(long locktime) {
        this.locktime = locktime;
    }

    /*****************************************************************/
    public String getId() {
        var hash_bytes = this.hash();
        // "Due to historical accident, the tx and block hashes that bitcoin core uses are byte-reversed.
        // I’m not entirely sure why. May be something like using openssl bignum to store hashes or something like that,
        // then printing them as a number."

        // Wladimir van der Laan (Bitcoin Core developer)
        return Kit.bytesToHexString(Kit.reverseBytes(hash_bytes));
    }

    /*****************************************************************/
    public byte[] hash() {
        return Kit.hash256(this.serialize());
    }


    @Override
    public String getCommand() {
        return Tx.COMMAND;
    }

    @Override
    public byte[] getPayload() {
        return this.serialize();
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
        var prev_script_pubkey = tx_in.getPreviousTxScriptPubKey(this.isTestnet());
        byte[] redeem_script = null;

        var script_pubkey = new Script(prev_script_pubkey);
        if (script_pubkey.isP2shScriptPubKey()) {
            // the commands of the redeem script are encoded as data at the bottom of the scriptsig
            var script_sig = new Script(tx_in.getScriptSig());
            var cmd = script_sig.commands.elementAt(0);

            redeem_script = cmd.value;
        }

        var z = this.getSigHash(input_index,redeem_script);

        var script_sig = new Script(tx_in.getScriptSig());
        var script_combined = new Script(prev_script_pubkey);
        script_combined.addTop(script_sig);
        eval = script_combined.evaluate(z);

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
        return getSigHash(input_index,null);
    }


    public byte[] getSigHash(int input_index, byte[] redeem_script) {
       byte[] x = null;

        var bos = new ByteArrayOutputStream();

        try {
            byte[] buf = Kit.intToLittleEndianBytes(version);
            bos.write(buf,0,4);

            int num_ins = tx_ins.size();
            bos.write(Objects.requireNonNull(Kit.encodeVarint(num_ins)));
            for (int i=0;i < num_ins; i++) {

                var prev_tx = tx_ins.get(i).getPrevTxId();
                var pre_index = tx_ins.get(i).getPrevIndex();
                var sequence = tx_ins.get(i).getSequence();

                // the script_sig of the index_input to be signed must be
                // replaced with the script_pubkey found in the previous transaction output
                // otherwise should be zero byte
                if (i==input_index) {
                    byte[] script_pubkey;

                    if (redeem_script==null)
                        script_pubkey = tx_ins.get(i).getPreviousTxScriptPubKey(this.isTestnet());
                    else  // p2psh
                        script_pubkey = redeem_script;

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
            bos.write(Objects.requireNonNull(Kit.encodeVarint(num_outs)));
            for (TxOut txout: tx_outs)
                bos.write(txout.getSerialized());

            buf = Kit.intToLittleEndianBytes(this.locktime);
            bos.write(buf,0,4);

            // SIGHASH_ALL hash type
            buf = Kit.intToLittleEndianBytes(1);
            bos.write(buf,0,4);

            x= (Kit.hash256(bos.toByteArray()));

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
            var version = Kit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();

            // TODO: check whether varint should be little endian (see the other)
            var num_inputs = Kit.readVarint(bis);
            ArrayList<TxIn> inputs = new ArrayList<>();

            for (int i=0;i<num_inputs;i++) {
                inputs.add(TxIn.parse(bis));
            }

            var num_outputs = Kit.readVarint(bis);
            ArrayList<TxOut> outputs = new ArrayList<>();
            for (int i=0;i<num_outputs;i++) {
                outputs.add(TxOut.parse(bis));
            }

            var locktime = Kit.litteEndianBytesToInt(bis.readNBytes(4)).longValue();

            tx = new Tx(version,inputs,outputs,locktime, testnet);


            var original_ser = Kit.bytesToHexString(serialization);
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
        return Kit.bytesToHexString(this.serialize());
    }

    /*****************************************************************/
    private byte[] serialize() {
        var bos = new ByteArrayOutputStream();

        try {
            byte[] buf = Kit.intToLittleEndianBytes(version);
            bos.write(buf,0,4);

            // TODO: check whether varint should be little endian (see the other)

            int num_ins = tx_ins.size();
            bos.write(Objects.requireNonNull(Kit.encodeVarint(num_ins)));
            for (TxIn txin: tx_ins)
                bos.write(txin.getSerialized());

            int num_outs = tx_outs.size();
            bos.write(Objects.requireNonNull(Kit.encodeVarint(num_outs)));
            for (TxOut txout: tx_outs)
                bos.write(txout.getSerialized());

            buf = Kit.intToLittleEndianBytes(this.locktime);
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

    public boolean isCoinbase() {

        if (this.getTxIns().size()!=1) return false;
        var single_in = this.getTxIns().get(0);
        if (!(new BigInteger(single_in.getPrevTxId()).equals(BigInteger.ZERO))) return false;
        var prev_target = Kit.hexStringToByteArray("ffffffff");
        var prev = Arrays.copyOfRange(Kit.intToLittleEndianBytes(single_in.getPrevIndex()),0,4);
        return Arrays.equals(prev, prev_target);
    }

    public int getCoinbaseHeight() {
        if (!this.isCoinbase()) return -1;
        int height = -1;

        var scriptsig = new Script(this.getTxIns().get(0).getScriptSig());
        height = Kit.litteEndianBytesToInt(scriptsig.commands.pop().value).intValue();
        return height;
    }
}

