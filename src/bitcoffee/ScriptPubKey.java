package bitcoffee;

import java.util.Arrays;
import java.util.Locale;
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

    public static ScriptPubKey fromAddress(String address) {
        var normalized = address.toLowerCase(Locale.ROOT);

        if (normalized.startsWith(Bech32.PREFIX_MAINNET + "1")
                || normalized.startsWith(Bech32.PREFIX_TESTNET + "1")
                || normalized.startsWith(Bech32.PREFIX_REGTEST + "1")) {
            var decoded = Bech32.decodeSegwitAddress(address);
            if (decoded.version == 0) {
                if (decoded.program.length == 20) {
                    return new P2WPKHScriptPubKey(decoded.program);
                }
                if (decoded.program.length == 32) {
                    return new P2WSHScriptPubKey(decoded.program);
                }
            }
            throw new IllegalArgumentException("Unsupported segwit address: " + address);
        }

        var decoded = Kit.decodeBase58Checked(address);
        int prefix = decoded[0] & 0xff;
        var payload = Arrays.copyOfRange(decoded, 1, decoded.length - 4);

        if (prefix == 0x00 || prefix == 0x6f) {
            return new P2PKHScriptPubKey(payload);
        }
        if (prefix == 0x05 || prefix == 0xc4) {
            return new P2SHScriptPubKey(payload);
        }
        throw new IllegalArgumentException("Unsupported Base58 address prefix: " + prefix);
    }

    public static byte[] addressPayload(String address) {
        var scriptPubKey = fromAddress(address);

        if (scriptPubKey instanceof P2PKHScriptPubKey) {
            return ((P2PKHScriptPubKey) scriptPubKey).getHash160();
        }
        if (scriptPubKey instanceof P2SHScriptPubKey) {
            return ((P2SHScriptPubKey) scriptPubKey).getHash160();
        }
        if (scriptPubKey instanceof P2WPKHScriptPubKey) {
            return ((P2WPKHScriptPubKey) scriptPubKey).getHash160();
        }
        if (scriptPubKey instanceof P2WSHScriptPubKey) {
            return ((P2WSHScriptPubKey) scriptPubKey).getHash160();
        }
        throw new IllegalArgumentException("Unsupported address type: " + address);
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

    public Script getRedeemScript() {
        return new RedeemScript(this.commands);
    }
}
