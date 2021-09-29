import java.awt.*;
import java.math.BigInteger;

public class TestValidation {
    public static void main(String[] args) {
        var raw_tx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        var tx = Tx.parse(CryptoKit.hexStringToByteArray(raw_tx),false);
        //System.out.println(tx);
        var fee = tx.calculateFee();
        System.out.println("Checking fee for tx id:"+tx.getId());
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
        System.out.println("Testing hex z:"+z.toString(16).equals(new String("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6")));

        sec = "0349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a";
        der = "3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed";
        z = new BigInteger("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6",16);
        point = S256Point.parseSEC(sec);
        signature = Signature.parse(CryptoKit.hexStringToByteArray(der));
        System.out.println("Test verify: "+point.verify(z,signature));
        System.out.println("--------------------------------------------------");

        System.out.println("Testing SigHash on input 0 of tx:"+tx.getId());

        z = tx.getSigHash(0);
        System.out.println(z.toString(16));

        System.out.println("Testing Verify Input");
        System.out.println(tx.verifyInput(0));


    }
}