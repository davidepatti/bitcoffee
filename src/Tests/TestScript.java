package Tests;

import bitcoffee.Kit;
import bitcoffee.Script;
import bitcoffee.ScriptCmd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Stack;

public class TestScript {

    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException {
        System.out.println("---------------------------------------------------");
        System.out.println("Testing encode_num:");

        var n = -256;
        var tn = "0081";
        byte[] encoded_n = Script.encodeNum(n);
        var res_n= Kit.bytesToHexString(encoded_n);
        System.out.println("encoding "+n+ " result "+res_n + " --> "+res_n.equals(tn));
        var decode_n = Script.decodeNum(encoded_n);
        System.out.println("decoding "+res_n+" result "+decode_n+ "-->"+decode_n.equals(BigInteger.valueOf(n)));

        n = 258;
        tn = "0201";
        encoded_n = Script.encodeNum(n);
        res_n= Kit.bytesToHexString(encoded_n);
        System.out.println("encoding "+n+ " result "+res_n + " --> "+res_n.equals(tn));
        decode_n = Script.decodeNum(encoded_n);
        System.out.println("decoding "+res_n+" result "+decode_n+ "-->"+decode_n.equals(BigInteger.valueOf(n)));
        n = -8;
        tn = "88";
        encoded_n = Script.encodeNum(n);
        res_n= Kit.bytesToHexString(encoded_n);
        System.out.println("encoding "+n+ " result "+res_n + " --> "+res_n.equals(tn));
        decode_n = Script.decodeNum(encoded_n);
        System.out.println("decoding "+res_n+" result "+decode_n+ "-->"+decode_n.equals(BigInteger.valueOf(n)));

        System.out.println("------------------------------------------------------");
        System.out.println(">> TEST Parsing Simple bitcoffee.Script");
        var bos = new ByteArrayOutputStream();
        //bos.write(3);
        bos.write(Kit.encodeVarint(3));
        bos.write(0x54);
        bos.write(0);
        bos.write(79);
        var simple_serial = bos.toByteArray();
        System.out.println("script serial hex: "+ Kit.bytesToHexString(simple_serial));

        // not using the constructor bitcoffee.Script(), since the serial includes the length at the beginning
        Script script = Script.parseSerial(simple_serial);
        System.out.println(script);
        System.out.println("------------------------------------------------------");

        var test_hash160 = new Stack<ScriptCmd>();
        var hello = Kit.asciiStringToBytes("hello world");
        test_hash160.push(new ScriptCmd(ScriptCmd.Type.OP_HASH160));
        test_hash160.push(new ScriptCmd(ScriptCmd.Type.DATA,hello));
        var test_script = new Script(test_hash160);
        System.out.println("------------------------------------------------------");
        System.out.println(">> Testing hash160 on "+ Kit.bytesToHexString(hello));
        // when debugging, check that stack remains with DATA d7d5ee7824ff93f94c3055af9382c86c68b5ca92

        System.out.println(test_script.evaluate(null));
        System.out.println("------------------------------------------------------");
        System.out.println("Testing script p2pk");

        var z = Kit.hexStringToByteArray("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d");
        var sec = Kit.hexStringToByteArray("04887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e" +
                "4da568744d06c61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34");
        var sig = Kit.hexStringToByteArray("3045022000eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c022100c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab601");
                                               ///        3045022000eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c022100c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab601
        var script_pubkey_ops = new Stack<ScriptCmd>();
        script_pubkey_ops.push(new ScriptCmd(ScriptCmd.Type.OP_CHECKSIG));
        script_pubkey_ops.push(new ScriptCmd(ScriptCmd.Type.DATA,sec));

        var script_sig_ops = new Stack<ScriptCmd>();
        script_sig_ops.push(new ScriptCmd(ScriptCmd.Type.DATA,sig));

        var combined_script = new Script(script_pubkey_ops);
        combined_script.addTop(script_sig_ops);
        System.out.println("Testing Combined script:");
        System.out.println(combined_script);
        System.out.println("--> Result: " +combined_script.evaluate(z));

        System.out.println("----------------------------------------------------");
        System.out.println(" Testing exercise 3 - chapter 6");

        var script_pubkey3 = new Stack<ScriptCmd>();
        script_pubkey3.push(new ScriptCmd(ScriptCmd.Type.OP_EQUAL));
        script_pubkey3.push(new ScriptCmd(ScriptCmd.Type.OP_6));
        script_pubkey3.push(new ScriptCmd(ScriptCmd.Type.OP_ADD));
        script_pubkey3.push(new ScriptCmd(ScriptCmd.Type.OP_MUL));
        script_pubkey3.push(new ScriptCmd(ScriptCmd.Type.OP_DUP));
        script_pubkey3.push(new ScriptCmd(ScriptCmd.Type.OP_DUP));

        var script_sig3 = new Stack<ScriptCmd>();
        script_sig3.push(new ScriptCmd(ScriptCmd.Type.OP_2));

        var combined3 = new Script(script_pubkey3);
        combined3.addTop(script_sig3);

        System.out.println("Result: "+combined3.evaluate(null));

    }
}
