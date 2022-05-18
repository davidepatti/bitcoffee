package bitcoffee;

import java.util.Stack;

public class ScriptPubKey extends Script {

    public ScriptPubKey() {
        super();
    }

    public ScriptPubKey(byte[] command_bytes) {
        super(command_bytes);
    }

    public ScriptPubKey(Stack<ScriptCmd> cmdStack) {
        super(cmdStack);
    }


    public static ScriptPubKey parse(byte[] command_bytes) {
        var script = new Script(command_bytes);

        // TOOD: check different position in python
        if (script.isP2pkhScriptPubKey()) {
            return new P2PKHScriptPubKey(script.getCommands().get(2).value);
        }
        else
        if (script.isP2shScriptPubKey()) {
            return new P2SHScriptPubKey(script.getCommands().get(1).value);
        }
        else
        if (script.isP2wpkhScriptPubKey()) {
            return new P2WPKHScriptPubKey(script.getCommands().get(0).value);
        }
        else
        if (script.isP2wshScriptPubKey()) {
            return new P2WSHScriptPubKey(script.getCommands().get(0).value);
        }
        else return new ScriptPubKey();
    }

    public String getAddress(boolean testnet) {
        if (this.isP2pkhScriptPubKey()) {
            throw  new RuntimeException("Invoking wrong");
        }
        /*
        else
        if (this.isP2shScriptPubKey()) {
            return new P2SHScriptPubKey(script.getCommands().get(1).value);
        }
        else
        if (script.isP2wpkhScriptPubKey()) {
            return new P2WPKHScriptPubKey(script.getCommands().get(0).value);
        }
        else
        if (script.isP2wshScriptPubKey()) {
            return new P2WSHScriptPubKey(script.getCommands().get(0).value);
        }
         */
        return super.getAddress(testnet);
    }
}
