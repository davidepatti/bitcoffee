package bitcoffee;

import java.io.ByteArrayOutputStream;
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
        this.commands = cmds;

    }

    public static String h160ToAddress(byte[] h160, boolean testnet) {
        byte prefix;

        if (testnet) prefix = 0x6f;
                else
                    prefix = 0;

        var bos = new ByteArrayOutputStream();
        bos.write(prefix);
        bos.writeBytes(h160);
        var res_bytes = bos.toByteArray();
        return Kit.encodeBase58Checksum(res_bytes);
    }

    public String getAddress(boolean testnet){
        return h160ToAddress(this.getHash160(),testnet);
    }

    public byte[] getHash160() {
        return this.commands.get(2).value;
    }

}
