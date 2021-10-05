import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class TestValidation {
    public static void main(String[] args) {
        var raw_tx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        var tx = Tx.parse(CryptoKit.hexStringToByteArray(raw_tx),false);
        //System.out.println(tx);
        var fee = tx.calculateFee();
        System.out.println("--------------------------------------------------");
        System.out.println(">> Checking fee for tx id:"+tx.getId());
        if (fee>=0)
            System.out.println("Valid transactions fees:"+fee);
        System.out.println("--------------------------------------------------");
        var sec = "0349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a";
        var der = "3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed";
        var z = new BigInteger("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6",16);
        var point = S256Point.parseSEC(sec);
        var signature = Signature.parse(CryptoKit.hexStringToByteArray(der));
        System.out.println("Verify signature test: "+point.verify(z,signature));
        var mod_tx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000001976a914a802fc56c704ce87c42d7c92eb75e7896bdc41ae88acfeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac1943060001000000";
        var h256 = CryptoKit.hash256(CryptoKit.hexStringToByteArray(mod_tx));
        z = new BigInteger(h256);
        System.out.println(">> Testing hex z:"+z.toString(16).equals(new String("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6")));

        sec = "0349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a";
        der = "3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed";
        z = new BigInteger("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6",16);
        point = S256Point.parseSEC(sec);
        signature = Signature.parse(CryptoKit.hexStringToByteArray(der));
        System.out.println(">> Test verify: "+point.verify(z,signature));
        System.out.println("--------------------------------------------------");

        System.out.println(">> Testing SigHash on input 0 of tx:"+tx.getId());

        z = tx.getSigHash(0);
        System.out.println(z.toString(16));

        System.out.println(">> Testing Verify Input");
        System.out.println(tx.verifyInput(0));
        System.out.println("--------------------------------------------------");
        System.out.println(">> Testing Transaction creation");
        var prev_tx = CryptoKit.hexStringToByteArray("0d6fe5213c0b3291f208cba8bfb59b7476dffacc4e5cb66f6eb20a080843a299");
        var prev_index = 13;
        byte[] script_null = {};
        var tx_in = new TxIn(prev_tx,prev_index,script_null);
        System.out.println(tx_in);
        var change_amount = (int)(0.33*100000000);
        var change_address = "mzx5YhAH9kNHtcN481u6WkjeHjYtVeKVh2";
        var change_h160 = CryptoKit.decodeBase58(change_address);
        System.out.println("---------------------------------------------------");
        System.out.println(">> TESTING: decoded change_h160 for address :"+change_address);
        var result_tdc = CryptoKit.bytesToHexString(change_h160);
        System.out.println(result_tdc);
        var target_decoded_change = "d52ad7ca9b3d096a38e752c2018e6fbc40cdf26f";
        System.out.println(">> RESULT:"+result_tdc.equals(target_decoded_change));
        System.out.println("---------------------------------------------------");

        var change_script = Script.hash160ToP2pkh(change_h160);
        var change_script_bytes = change_script.getBytes();
        var change_script_serial_hex = CryptoKit.bytesToHexString(change_script_bytes);
        var change_output = new TxOut(change_amount,change_script_bytes);

        var target_amount = (int)(0.1*100000000);
        var target_address = "mnrVtF8DWjMu839VW3rBfgYaAfKk8983Xf";
        var target_h160 = CryptoKit.decodeBase58(target_address);
        var target_script = Script.hash160ToP2pkh(target_h160);
        var target_output = new TxOut(target_amount,target_script.getBytes());

        var tx_ins = new ArrayList<TxIn>();
        var tx_outs = new ArrayList<TxOut>();
        tx_outs.add(change_output);
        tx_outs.add(target_output);
        tx_ins.add(tx_in);
        var tx_obj = new Tx(1,tx_ins,tx_outs,0,true);
        System.out.println("Created TX:");
        System.out.println(tx_obj.getSerialString());
        var target_txhex = "010000000199a24308080ab26e6fb65c4eccfadf76749bb5bfa8cb08f291320b3c21e56f0d0d00000000ffffffff02408af701000000001976a914d52ad7ca9b3d096a38e752c2018e6fbc40cdf26f88ac80969800000000001976a914507b27411ccf7f16f10297de6cef3f291623eddf88ac00000000";
        System.out.println("Target TX:");
        System.out.println(target_txhex);
        System.out.println(tx_obj);

        System.out.println("Tx outs scripts");
        for (TxOut txOut:tx_outs) System.out.println(txOut.printScript());

    }
}