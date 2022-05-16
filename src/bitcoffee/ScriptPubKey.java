package bitcoffee;

public class ScriptPubKey extends Script {

    public ScriptPubKey() {
        super();
    }

    public ScriptPubKey(byte[] command_bytes) {
        super(command_bytes);
    }


    public static ScriptPubKey parse(byte[] command_bytes) {
        var pubkey = new ScriptPubKey(command_bytes);

        if (pubkey.isP2pkhScriptPubKey()) {
            return new P2PKHScriptPubKey(pubkey.commands.get(2).value);
        }
        else return pubkey;

    }
}
