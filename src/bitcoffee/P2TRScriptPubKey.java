package bitcoffee;

import java.util.Stack;

public class P2TRScriptPubKey extends ScriptPubKey{
    // Convert the 20 bytes hash in a ScriptPubKey

    public P2TRScriptPubKey(byte[] h160) {
        var cmds = new Stack<ScriptCmd>();
        throw new RuntimeException("Not implemented");

        //this.commands = cmds;
    }

    public byte[] getHash160() {
        return getCommands().get(1).value;
    }

    @Override
    public String getAddress(boolean testnet){
        return P2SHScriptPubKey.h160ToAddress(this.getHash160(),testnet);
    }
}
