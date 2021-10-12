import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class TestP2SH {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------------");
        System.out.println(">> Test 1 ");
        var mod_tx = CryptoKit.hexStringToByteArray("0100000001868278ed6ddfb6c1ed3ad5f8181eb0c7a385aa0836f01d5e4789e6bd304d87221a000000475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152aeffffffff04d3b11400000000001976a914904a49878c0adfc3aa05de7afad2cc15f483a56a88ac7f400900000000001976a914418327e3f3dda4cf5b9089325a4b95abdfa0334088ac722c0c00000000001976a914ba35042cfe9fc66fd35ac2224eebdafd1028ad2788acdc4ace020000000017a91474d691da1574e6b3c192ecfb52cc8984ee7b6c56870000000001000000");
        var s256 = CryptoKit.hash256(mod_tx);
        var hex_z = CryptoKit.bytesToHexString(s256);
        System.out.println("hex z = "+hex_z);
        var sec = CryptoKit.hexStringToByteArray("022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb70");
        var der = CryptoKit.hexStringToByteArray("3045022100dc92655fe37036f47756db8102e0d7d5e28b3beb83a8fef4f5dc0559bddfb94e02205a36d4e4e6c7fcd16658c50783e00c341609977aed3ad00937bf4ee942a89937");
        var point = S256Point.parseSEC(sec);
        var sig = Signature.parse(der);
        System.out.println(" Point verify: "+point.verify(new BigInteger(1,s256),sig));

        System.out.println("----------------------------------------------------");
        System.out.println(">> TEST Validating Signature with Redeem Script ");
        var tx_bytes = CryptoKit.hexStringToByteArray("0100000001868278ed6ddfb6c1ed3ad5f8181eb0c7a385aa0836f01d5e4789e6bd304d87221a000000db00483045022100dc92655fe37036f47756db8102e0d7d5e28b3beb83a8fef4f5dc0559bddfb94e02205a36d4e4e6c7fcd16658c50783e00c341609977aed3ad00937bf4ee942a8993701483045022100da6bee3c93766232079a01639d07fa869598749729ae323eab8eef53577d611b02207bef15429dcadce2121ea07f233115c6f09034c0be68db99980b9a6c5e75402201475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152aeffffffff04d3b11400000000001976a914904a49878c0adfc3aa05de7afad2cc15f483a56a88ac7f400900000000001976a914418327e3f3dda4cf5b9089325a4b95abdfa0334088ac722c0c00000000001976a914ba35042cfe9fc66fd35ac2224eebdafd1028ad2788acdc4ace020000000017a91474d691da1574e6b3c192ecfb52cc8984ee7b6c568700000000");
        sec = CryptoKit.hexStringToByteArray("03b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb71");
        der = CryptoKit.hexStringToByteArray("3045022100da6bee3c93766232079a01639d07fa869598749729ae323eab8eef53577d611b02207bef15429dcadce2121ea07f233115c6f09034c0be68db99980b9a6c5e754022");
        var serial_redeem_script = CryptoKit.hexStringToByteArray("475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152ae");

        try {
            // Please notice: we need to parse the script anyway, because in the TxIn constructor we must
            // provide only the bytes encoding the ops, without the length prefix added when serialized
            var redeem_script = Script.parseSerial(serial_redeem_script);
            System.out.println("Using redeem script:");
            System.out.println(redeem_script);

            var tx = Tx.parse(tx_bytes,true);
            var bos = new ByteArrayOutputStream();

            // Start composing the serialization of the modified tx

            bos.write(CryptoKit.intToLittleEndianBytes(tx.getVersion()),0,4);
            bos.write(CryptoKit.encodeVarint(tx.getTxIns().size()));

            // modify the single TxIn to have the ScriptSig to be the RedeemScript
            var i = tx.getTxIns().get(0);
            /*
            System.out.println("Prev TxIn:");
            System.out.println(i);
            System.out.println("New TxIn:");
             */
            var txin = new TxIn(i.getPrevTxId(),i.getPrevIndex(),redeem_script.getBytes(),i.getSequence());
            //System.out.println(txin);
            bos.write(txin.getSerialized());

            bos.write(CryptoKit.encodeVarint(tx.getTxOuts().size()));

            for (TxOut txout: tx.getTxOuts()) {
                bos.write(txout.getSerialized());
            }

            bos.write(CryptoKit.intToLittleEndianBytes(tx.getLocktime()),0,4);
            // SIGHASH ALL, value 1 in little endian
            bos.write(1);
            bos.write(0);
            bos.write(0);
            bos.write(0);

            var res_raw = CryptoKit.bytesToHexString(bos.toByteArray());
            var target_raw = "0100000001868278ed6ddfb6c1ed3ad5f8181eb0c7a385aa0836f01d5e4789e6bd304d87221a000000475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152aeffffffff04d3b11400000000001976a914904a49878c0adfc3aa05de7afad2cc15f483a56a88ac7f400900000000001976a914418327e3f3dda4cf5b9089325a4b95abdfa0334088ac722c0c00000000001976a914ba35042cfe9fc66fd35ac2224eebdafd1028ad2788acdc4ace020000000017a91474d691da1574e6b3c192ecfb52cc8984ee7b6c56870000000001000000";
            System.out.println(">> RESULT Tx modified: "+res_raw.equals(target_raw));
            System.out.println(" -----------------------------------------------------------------");

            var z = CryptoKit.hash256(bos.toByteArray());
            point = S256Point.parseSEC(sec);
            sig = Signature.parse(der);
            System.out.println(">> RESULT Redeem Script verification: "+point.verify(new BigInteger(1,z),sig));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
