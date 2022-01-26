import java.io.IOException;

public class TestCoinbase {
    public static void main(String[] args) throws IOException {


        System.out.println("---------------------------------------------------");
        System.out.println(">> Testing Satoshi's genesis block message:");
        var genesis_scriptsig = "4d04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73";
        var genesis_script = Script.parseSerial(Kit.hexStringToByteArray(genesis_scriptsig));
        System.out.println(genesis_script);
        System.out.println(Kit.bytesToAscii(genesis_script.commands.get(0).value));
        System.out.println("---------------------------------------------------");

        System.out.println(">> Testing BIP0034 (height as top element in coinbase scriptsig");

        var coinbase_scriptsig = Script.parseSerial(Kit.hexStringToByteArray("5e03d71b07254d696e656420627920416e74506f6f6c20626a31312f4542312f4144362f43205914293101fabe6d6d678e2c8c34afc36896e7d9402824ed38e856676ee94bfdb0c6c4bcd8b2e5666a0400000000000000c7270000a5e00e00"));

        System.out.println(coinbase_scriptsig);

        System.out.println("Height:"+ Kit.litteEndianBytesToInt(coinbase_scriptsig.commands.pop().value));

    }


}
