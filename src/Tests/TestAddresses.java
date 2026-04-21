package Tests;

import bitcoffee.Kit;
import bitcoffee.P2WPKHScriptPubKey;
import bitcoffee.PrivateKey;
import bitcoffee.ScriptPubKey;

public class TestAddresses {
    public static void main(String[] args) {
        Test.__BEGIN_TEST("Address Helpers");

        var secret = Kit.sha256(Kit.asciiStringToBytes("Satoshi Nakamoto"));
        var pk = new PrivateKey(secret);

        var nativeSegwitAddress = pk.point.getP2wpkhAddress(true);
        var witnessScript = new P2WPKHScriptPubKey(pk.point.getHash160(true));

        Test.check("native segwit prefix", "", true, nativeSegwitAddress.startsWith("tb1"));
        Test.check("native segwit helper consistency", "", witnessScript.getAddress(true), nativeSegwitAddress);

        var parsedWitnessScript = ScriptPubKey.fromAddress(nativeSegwitAddress);
        Test.check("native segwit roundtrip", "",
                Kit.bytesToHexString(witnessScript.rawSerialize()),
                Kit.bytesToHexString(parsedWitnessScript.rawSerialize()));

        var highBitProgram = Kit.hexStringToByteArray("ffffffffffffffffffffffffffffffffffffffff");
        var highBitAddress = new P2WPKHScriptPubKey(highBitProgram).getAddress(true);
        var parsedHighBitScript = (P2WPKHScriptPubKey) ScriptPubKey.fromAddress(highBitAddress);
        Test.check("high-bit bech32 roundtrip", "",
                Kit.bytesToHexString(highBitProgram),
                Kit.bytesToHexString(parsedHighBitScript.getHash160()));

        var wrappedSegwitAddress = pk.point.getP2shAddress(true);
        var parsedWrappedScript = ScriptPubKey.fromAddress(wrappedSegwitAddress);
        Test.check("wrapped segwit roundtrip", "",
                wrappedSegwitAddress,
                parsedWrappedScript.getAddress(true));
        Test.__END_TEST();
    }
}
