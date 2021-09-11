import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;

public class TestScript {

    public static void main(String[] args) {

        var bos = new ByteArrayOutputStream();

        bos.write(3);

        bos.write(0x54);
        bos.write(0);
        bos.write(79);


        try {
            Script script = Script.parse(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        var test_hash160 = new Stack<ScriptCmd>();
        var hello = CryptoKit.stringToBytes("hello world");
        test_hash160.push(new ScriptCmd(OpCode.OP_HASH160));
        test_hash160.push(new ScriptCmd(OpCode.DATA,hello));
        var test_script = new Script(test_hash160);
        System.out.println("Testing hash160 on "+ CryptoKit.bytesToHexString(hello));
        // when debugging, check that stack remains with DATA d7d5ee7824ff93f94c3055af9382c86c68b5ca92
        System.out.println(test_script.evaluate(null));
        System.out.println("------------------------------------------------------");

        System.out.println("Testing script to pubkey");

        var z = CryptoKit.hexStringToByteArray("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d");
        var sec = CryptoKit.hexStringToByteArray("04887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e" +
                "4da568744d06c61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34");
        var sig = CryptoKit.hexStringToByteArray("3045022000eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c022100c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab601");
                                               ///        3045022000eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c022100c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab601
        var script_pubkey_ops = new Stack<ScriptCmd>();
        script_pubkey_ops.push(new ScriptCmd(OpCode.OP_CHECKSIG));
        script_pubkey_ops.push(new ScriptCmd(OpCode.DATA,sec));

        var script_sig_ops = new Stack<ScriptCmd>();
        script_sig_ops.push(new ScriptCmd(OpCode.DATA,sig));

        var combined_script = new Script(script_pubkey_ops);
        combined_script.addTop(script_sig_ops);
        System.out.println("Testing Combined script:");
        System.out.println(combined_script);
        System.out.println("--> Result: " +combined_script.evaluate(z));

        System.out.println("---------------------------------------------------");
        System.out.println("Testing encode_num:");

        var n = -256;
        var tn = "0081";
        byte[] encoded_n = CryptoKit.encodeNum(n);
        var res_n= CryptoKit.bytesToHexString(encoded_n);
        System.out.println("encoding "+n+ " result "+res_n + " --> "+res_n.equals(tn));

        n = 258;
        tn = "0201";
        encoded_n = CryptoKit.encodeNum(n);
        res_n= CryptoKit.bytesToHexString(encoded_n);
        System.out.println("encoding "+n+ " result "+res_n + " --> "+res_n.equals(tn));
        n = -8;
        tn = "88";
        encoded_n = CryptoKit.encodeNum(n);
        res_n= CryptoKit.bytesToHexString(encoded_n);
        System.out.println("encoding "+n+ " result "+res_n + " --> "+res_n.equals(tn));
    }
}
