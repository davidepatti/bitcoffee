package bitcoffee;

import java.util.Stack;

public class P2PKHScriptPubKey extends ScriptPubKey{
    // Convert the 20 bytes hash in a ScriptPubKey

    public P2PKHScriptPubKey(byte[] h160) {
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmd.Type.OP_CHECKSIG));
        cmds.push(new ScriptCmd(ScriptCmd.Type.OP_EQUALVERIFY));
        cmds.push(new ScriptCmd(ScriptCmd.Type.DATA,h160));
        cmds.push(new ScriptCmd(ScriptCmd.Type.OP_HASH160));
        cmds.push(new ScriptCmd(ScriptCmd.Type.OP_DUP));

    }

}
