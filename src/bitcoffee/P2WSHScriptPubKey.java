package bitcoffee;

import java.util.Stack;

public class P2WSHScriptPubKey extends SegwitPubKey {
    // Convert the 20 bytes hash in a ScriptPubKey

    public P2WSHScriptPubKey(byte[] h160) {
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmd.Type.DATA,h160));
        cmds.push(new ScriptCmd(ScriptCmd.Type.OP_0));

        this.commands = cmds;

    }
    public byte[] getHash160() {
        return getCommands().get(1).value;
    }

}
