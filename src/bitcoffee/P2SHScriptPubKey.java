package bitcoffee;

import java.io.ByteArrayOutputStream;
import java.util.Stack;

public class P2SHScriptPubKey extends ScriptPubKey{
    // Convert the 20 bytes hash in a ScriptPubKey

    public P2SHScriptPubKey(byte[] h160) {
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmd.Type.OP_EQUAL));
        cmds.push(new ScriptCmd(ScriptCmd.Type.DATA,h160));
        cmds.push(new ScriptCmd(ScriptCmd.Type.OP_HASH160));
        this.commands = cmds;
    }

    public static String h160ToAddress(byte[] h160, boolean testnet) {
        byte prefix;

        if (testnet) prefix = (byte)0xc4;
        else
            prefix = 0x05;

        var bos = new ByteArrayOutputStream();
        bos.write(prefix);
        bos.writeBytes(h160);
        var res_bytes = bos.toByteArray();
        return Kit.encodeBase58Checksum(res_bytes);
    }

    public byte[] getHash160() {
        return getCommands().get(1).value;
    }

    public String getAddress(boolean testnet){
        return h160ToAddress(this.getHash160(),testnet);
    }


}
