package bitcoffee;

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
    private final boolean segwit;

    public final static String COMMAND = "tx";

    private static final int SIGHASH_ALL = 1;


    // not to be used directly
    private byte[] _hash_prevouts = null;
    private byte[] _hash_sequence = null;
    private byte[] _hash_outputs = null;


    /*****************************************************************/
    public boolean isSegwit() {
        return segwit;
    }

    /*****************************************************************/
    public Tx(int version, ArrayList<TxIn> tx_ins, ArrayList<TxOut> tx_outs, long locktime, boolean testnet, boolean segwit) {
        this.version = version;
        this.tx_ins = tx_ins;
        this.tx_outs = tx_outs;
        this.locktime = locktime;
        this.testnet = testnet;
        this.segwit = segwit;
    }
    /*****************************************************************/
    public Tx(int version, ArrayList<TxIn> tx_ins, ArrayList<TxOut> tx_outs, long locktime, boolean testnet) {
        this(version,tx_ins,tx_outs,locktime,testnet,false);
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
    // use legacy (non-segwit) version to keep id stable
    public byte[] hash() {
        return Kit.hash256(this.serializeLegacy());
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
        byte[] z = null;
        byte[] raw_redeem = null;
        ArrayList<byte[]> witness_data = null;

        var tx_in = tx_ins.get(input_index);
        var prevtx_script_pubkey_raw = tx_in.getPreviousTxScriptPubKey(this.isTestnet());
        var script_pubkey = new Script(prevtx_script_pubkey_raw);

        if (script_pubkey.isP2shScriptPubKey()) {
            // the commands of the redeem script are encoded as data at the bottom of the scriptsig
            var input_script_sig = new Script(tx_in.getScriptSig());
            // the hash160 is at the bottom of the commands stack
            raw_redeem = input_script_sig.getCommands().elementAt(0).value;

            var redeem_script = new Script(raw_redeem);

            // handle the p2sh-p2wpkh
            if (redeem_script.isP2wpkhScriptPubKey()) {
                z = this.getSigHashBIP143(input_index,redeem_script,null);
                witness_data = tx_in.getWitnessData();
            }
            else if (redeem_script.isP2wshScriptPubKey()) {
                witness_data = tx_in.getWitnessData();
                var command = witness_data.get(witness_data.size()-1);
                var witness_script = new Script(command);

                z = getSigHashBIP143(input_index,null,witness_script);
            }
            else z = getSigHash(input_index,raw_redeem);

        }
        else {
            if (script_pubkey.isP2wpkhScriptPubKey()) {
                z = getSigHashBIP143(input_index,null,null);
                witness_data = tx_in.getWitnessData();
            }
            else if (script_pubkey.isP2wshScriptPubKey()) {
                witness_data = tx_in.getWitnessData();
                var command = witness_data.get(witness_data.size()-1);
                var witness_script = new Script(command);
                z = getSigHashBIP143(input_index,null,witness_script);
            }
            else {
                z = getSigHash(input_index);
                witness_data = null;
            }
        }

        var script_sig = new Script(tx_in.getScriptSig());
        var script_combined = new Script(prevtx_script_pubkey_raw);
        script_combined.addTop(script_sig);
        return  script_combined.evaluate(z,witness_data);
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

    // Hashing utils for bip143 /////////////////////////////////////////////////////

    public byte[] hashPrevOuts() {
        if (this._hash_prevouts == null)  {
            var all_prevouts = new ByteArrayOutputStream();
            var all_sequence = new ByteArrayOutputStream();

            for (TxIn tx_in:tx_ins) {
                try {
                    all_prevouts.write(Kit.reverseBytes(tx_in.getPrevTxId()));
                    all_prevouts.write(Kit.intToLittleEndianBytes(tx_in.getPrevIndex()),0,4);
                    all_sequence.write(Kit.reverseBytes(tx_in.getSequence()),0,4);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            this._hash_prevouts = Kit.hash256(all_prevouts.toByteArray());
            this._hash_sequence = Kit.hash256(all_sequence.toByteArray());
        }
        return this._hash_prevouts;
    }

    public byte[] hashSequence() {
        if (this._hash_sequence ==null)
            this.hashPrevOuts();
        return this._hash_sequence;
    }


    public byte[] hashOutputs() {
        if (this._hash_outputs ==null) {
            var all_outputs = new ByteArrayOutputStream();
            for (TxOut tx_out: tx_outs) {
                try {
                    all_outputs.write(tx_out.getSerialized());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this._hash_outputs = Kit.hash256(all_outputs.toByteArray());
        }
        return this._hash_outputs;
    }


    /**********************************************************************************************/
    // Returns the hash that needs to get signed for index input_index
    // the BIP143 version solves the quadratic hashing problem
    public byte[] getSigHashBIP143(int input_index, Script redeem_script, Script witness_script) {
        var tx_in = this.tx_ins.get(input_index);

        var bos = new ByteArrayOutputStream();

        try {
            bos.write(Kit.intToLittleEndianBytes(this.version), 0, 4);
            bos.write(this.hashPrevOuts());
            bos.write(this.hashSequence());
            bos.write(Kit.reverseBytes(tx_in.getPrevTxId()));
            bos.write(Kit.intToLittleEndianBytes(tx_in.getPrevIndex()),0,4);

            byte[] script_code;

            if (witness_script!=null)  {
                script_code = witness_script.serialize();
            }
            else
            if (redeem_script!=null) {
                var h160 = redeem_script.getCommands().elementAt(0).value;
                script_code = new P2PKHScriptPubKey(h160).serialize();
            }
            else {
                var prev_script = new Script(tx_in.getPreviousTxScriptPubKey(this.testnet));
                var h160 = prev_script.getCommands().elementAt(0).value;
                script_code = new P2PKHScriptPubKey(h160).serialize();
            }

            bos.write(script_code);
            bos.write(Kit.intToLittleEndianBytes(tx_in.getValue(this.testnet)),0,8);
            bos.write(Kit.reverseBytes(tx_in.getSequence()),0,4);
            bos.write(this.hashOutputs());
            bos.write(Kit.intToLittleEndianBytes(this.locktime),0,4);
            bos.write(Kit.intToLittleEndianBytes(SIGHASH_ALL),0,4);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Kit.hash256(bos.toByteArray());

    }

        /*****************************************************************/
        //Returns the integer representation of the hash that needs to get signed for index input_index

        public byte[] getSigHash(int input_index) {
            return getSigHash(input_index,null);
        }

    /**********************************************************************************************/
   // Returns the integer representation of the hash that needs to get signed for index input_index
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
            bos.write(Kit.intToLittleEndianBytes(SIGHASH_ALL),0,4);

            x= (Kit.hash256(bos.toByteArray()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return x;
    }

    /************************************************************************/
    // read serialized version into the bytestream to create a corresponding tx
    static public Tx parseLegacy(byte[] serialization, boolean testnet) {
        var bis = new ByteArrayInputStream(serialization);

        Tx tx = null;

        try {
            var version = Kit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();

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


    static public Tx parseSegwit(byte[] serialization, boolean testnet) {
        var bis = new ByteArrayInputStream(serialization);

        Tx tx = null;

        try {
            var version = Kit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
            var marker = bis.readNBytes(2);

            // segwit marker
            if (marker[0]!=0 || marker[1]!=1) {
                throw new RuntimeException("Not valid segwit marker!");
            }


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

            // witness field, containing items for each input

            for (TxIn in: inputs) {
                var num_items = Kit.readVarint(bis);

                ArrayList<byte[]> items = new ArrayList<>();

                for (int i=0;i<num_items;i++) {
                    var item_len = (int) Kit.readVarint(bis);
                    if (item_len==0)
                        items.add((new byte[] {0}));
                    else
                        items.add(bis.readNBytes(item_len));
                }

                in.setWitnessData(items);
            }

            var locktime = Kit.litteEndianBytesToInt(bis.readNBytes(4)).longValue();

            tx = new Tx(version,inputs,outputs,locktime, testnet,true);


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
    // parses a stream to construct a bitcoffee.Tx instance
    static public Tx parse(byte[] serialization, boolean testnet) {

        if (serialization[4]==0)
            return parseSegwit(serialization,testnet);

        return parseLegacy(serialization,testnet);
    }

    /*****************************************************************/
    public String getSerialString() {
        return Kit.bytesToHexString(this.serialize());
    }

    /*****************************************************************/
    private byte[] serializeSegwit() {
        var bos = new ByteArrayOutputStream();

        try {
            byte[] buf = Kit.intToLittleEndianBytes(version);
            bos.write(buf,0,4);

            // segwit marker (2 bytes)
            bos.write(0);
            bos.write(1);

            int num_ins = tx_ins.size();
            bos.write(Objects.requireNonNull(Kit.encodeVarint(num_ins)));
            for (TxIn txin: tx_ins)
                bos.write(txin.getSerialized());

            int num_outs = tx_outs.size();
            bos.write(Objects.requireNonNull(Kit.encodeVarint(num_outs)));
            for (TxOut txout: tx_outs)
                bos.write(txout.getSerialized());

            // witness data
            for (TxIn txin: tx_ins) {
                var witness_data = txin.getWitnessData();

                bos.write(Kit.encodeVarint(witness_data.size()));
                for (byte[] item: witness_data) {
                    if (item.length==1 && item[0]==0)
                        bos.write(item);
                    else {
                        bos.write(Kit.encodeVarint(item.length));
                        bos.write(item);
                    }
                }
            }

            buf = Kit.intToLittleEndianBytes(this.locktime);
            bos.write(buf,0,4);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();

    }
    /*****************************************************************/
    private byte[] serializeLegacy() {
        var bos = new ByteArrayOutputStream();

        try {
            byte[] buf = Kit.intToLittleEndianBytes(version);
            bos.write(buf,0,4);

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
    private byte[] serialize() {
        if (this.segwit) return serializeSegwit();
        return serializeLegacy();
    }

    /*****************************************************************/
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(">>>>>>>>>>>>> TRANSACTION >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        ret.append("\nID: ").append(this.getId()).append("\nversion: ").append(version);

        ret.append("\n------------------ INPUTS ---------------------------------");
        for (TxIn txin: tx_ins)
            ret.append("\n").append(txin);
        ret.append("\n----------------- OUTPUTS ------------------------------");
        for (TxOut txout: tx_outs)
            ret.append("\n").append(txout);
        ret.append("\n-----------------------------------------------------");

        ret.append("\nlocktime: ").append(locktime).append(" testnet: ").append(testnet);
        ret.append("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return ret.toString();
    }

    /*****************************************************************/
    public long calculateFee() {
        // in satoshis
        long total_in = 0;
        long total_out = 0;
        for (TxIn txin: tx_ins) {
            total_in+= txin.getValue(this.testnet);
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
        height = Kit.litteEndianBytesToInt(scriptsig.getCommands().pop().value).intValue();
        return height;
    }
}

