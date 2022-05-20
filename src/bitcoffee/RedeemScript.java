package bitcoffee;

import java.io.IOException;
import java.util.Stack;

public class RedeemScript extends Script {

    public RedeemScript(Stack<ScriptCmd> commands) {
       super(commands);
    }

    public boolean isP2SHMultiSig() {
        return this.getCommands().get(0).type== ScriptCmd.Type.OP_CHECKMULTISIG;
    }

    public byte[] getHash160() {
        return Kit.hash160(this.rawSerialize());

    }



    public Script getScriptPubKey(){
        return new P2SHScriptPubKey(this.getHash160());
    }

    public String getAddress(boolean testnet) {
        return this.getScriptPubKey().getAddress(testnet);
    }

    public static Script convert(byte[] raw_redeem_script) {
        Script s = null;

        try {
            s = parseSerial(raw_redeem_script);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    // TODO: create_p2sh_multisig
    // TODO: get quorum, signing_pubkeys
}
