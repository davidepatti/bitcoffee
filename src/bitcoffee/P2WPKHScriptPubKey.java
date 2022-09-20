package bitcoffee;

import java.util.Stack;

public class P2WPKHScriptPubKey extends SegwitPubKey {
    // Convert the 20 bytes hash in a ScriptPubKey

    public P2WPKHScriptPubKey(byte[] h160) {
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmd.Type.DATA,h160));
        cmds.push(new ScriptCmd(ScriptCmd.Type.OP_0));
        this.commands = cmds;
    }

    public byte[] getHash160() {
        return getCommands().get(1).value;
    }

    /*
    @Override
    public String getAddress(boolean testnet){
        return P2SHScriptPubKey.h160ToAddress(this.getHash160(),testnet);
    }
    */

    public static String h160ToAddress(byte[] h160,boolean testnet) {
        return P2SHScriptPubKey.h160ToAddress(h160,testnet);
    }
}
