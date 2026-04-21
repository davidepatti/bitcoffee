package Tests;
import bitcoffee.*;

import java.util.ArrayList;
import java.util.Stack;


public class TestBroadcastTx {
    private static final String SECRET_TEXT_PLACEHOLDER = "REPLACE_WITH_YOUR_TEXT";
    private static final String PREV_TX_ID_PLACEHOLDER = "YOUR_SPENDABLE_TX_ID";
    private static final String CHANGE_ADDRESS_PLACEHOLDER = "YOUR_CHANGE_ADDRESS";
    private static final String TARGET_ADDRESS_PLACEHOLDER = "YOUR_TARGET_ADDRESS";

    public static void main(String[] args) {

        System.out.println("---------------------------------------------");
        System.out.println(">> Testing Transaction to be broadcasted in Testnet");
        // brainwallet style, use text to derive private key (be careful to not share it!)
        var secret_text = System.getenv().getOrDefault("BITCOFFEE_SECRET_TEXT", SECRET_TEXT_PLACEHOLDER);
        ensureConfigured(secret_text, SECRET_TEXT_PLACEHOLDER, "BITCOFFEE_SECRET_TEXT");
        var secret_bytes = Kit.hash256(secret_text);
        var mypk = new PrivateKey(secret_bytes);

        var myaddress = mypk.point.getP2pkhAddress(true);
        System.out.println("Testnet address for secret:"+secret_text);
        System.out.println("address: "+myaddress);
        var wif = mypk.getWIF(true,true);
        System.out.println("Use this WIF to import the private key into a wallet (testnet): "+wif);
        System.out.println("---------------------------------------------");

        System.out.println("---------------------------------------------");
        System.out.println("REPLACE THE ID BELOW WITH SOME TX ID WHERE YOUR RECEIVED BTC FOR ADDRESS: "+myaddress);
        //var prev_tx_id = "1818136d9d0ca83c369a70c41fd2b5d25e286895e358a0bcd872c17534846659";
        var prev_tx_id = System.getenv().getOrDefault("BITCOFFEE_PREV_TX_ID", PREV_TX_ID_PLACEHOLDER);
        ensureConfigured(prev_tx_id, PREV_TX_ID_PLACEHOLDER, "BITCOFFEE_PREV_TX_ID");
        var prev_tx = Kit.hexStringToByteArray(prev_tx_id);
        // replace with your prev index
        var prev_index = Integer.parseInt(System.getenv().getOrDefault("BITCOFFEE_PREV_INDEX", "1"));
        // leave the script below empty
        byte[] script_null = {};
        var tx_in = new TxIn(prev_tx,prev_index,script_null);

        var btc_change_amount = Double.parseDouble(System.getenv().getOrDefault("BITCOFFEE_CHANGE_BTC", "0.00069"));

        // must multiply to express it in sats
        var change_amount = (int)(btc_change_amount*100000000);
        //var change_address = "mnwUykq9XxccVMuXgwrA97gSxYxFs4vNRW";
        var change_address = System.getenv().getOrDefault("BITCOFFEE_CHANGE_ADDRESS", CHANGE_ADDRESS_PLACEHOLDER);
        ensureConfigured(change_address, CHANGE_ADDRESS_PLACEHOLDER, "BITCOFFEE_CHANGE_ADDRESS");
        var change_script = ScriptPubKey.fromAddress(change_address);
        var change_script_bytes = change_script.rawSerialize();
        var change_output = new TxOut(change_amount,change_script_bytes);


        // DEFAULT: send a tx to give back to faucet https://testnet-faucet.mempool.co/
        var target_address = System.getenv().getOrDefault("BITCOFFEE_TARGET_ADDRESS", TARGET_ADDRESS_PLACEHOLDER);
        ensureConfigured(target_address, TARGET_ADDRESS_PLACEHOLDER, "BITCOFFEE_TARGET_ADDRESS");
        var target_btc_amount = Double.parseDouble(System.getenv().getOrDefault("BITCOFFEE_TARGET_BTC", "0.0001"));
        var target_amount = (int)(target_btc_amount*100000000);
        var target_script = ScriptPubKey.fromAddress(target_address);
        var target_output = new TxOut(target_amount,target_script.rawSerialize());

        var tx_ins = new ArrayList<TxIn>();
        var tx_outs = new ArrayList<TxOut>();
        tx_outs.add(change_output);
        tx_outs.add(target_output);
        tx_ins.add(tx_in);
        var tx_obj = new Tx(1,tx_ins,tx_outs,0,true);

        // creating the scriptsig to unlock the previous output
        var input_index = 0;
        var z = tx_obj.getSigHash(input_index);
        var der = mypk.signDeterminisk(z).DER();
        // DER + SIGHASH_ALL
        var sig = Kit.hexStringToByteArray(der+"01");
        var sec = Kit.hexStringToByteArray(mypk.point.SEC33());
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmd.Type.DATA,sec));
        cmds.push(new ScriptCmd(ScriptCmd.Type.DATA,sig));
        var scriptsig = new Script(cmds);
        ////////////////// end script

        var txins = tx_obj.getTxIns();
        // a new txin must be created to adde the signature by replacing the empty script one (input_index)
        var new_txin = new TxIn(txins.get(input_index).getPrevTxId(),txins.get(input_index).getPrevIndex(),scriptsig.rawSerialize());
        txins.set(input_index,new_txin);
        var newtx = new Tx(tx_obj.getVersion(),tx_ins,tx_obj.getTxOuts(),tx_obj.getLocktime(),tx_obj.isTestnet());

        System.out.println("Created tx with content:");
        System.out.println(newtx);
        System.out.println("Fees:"+newtx.calculateFee());
        System.out.println("Checking validity:" +newtx.verify());
        System.out.println(">>>>>> PLEASE USE THIS RAW TEXT BELOW TO BROADCAST TX: ");
        System.out.println(newtx.getSerialString());
    }

    private static void ensureConfigured(String value, String placeholder, String envName) {
        if (placeholder.equals(value)) {
            throw new IllegalStateException("Set " + envName + " before running this example");
        }
    }
}
