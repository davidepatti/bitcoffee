import java.math.BigInteger;

public class TestSerialization {
    public static void main(String args[]) {
        System.out.println("g sec = "+Secp256k1.G.SEC65());

        var e = BigInteger.valueOf(5000);
        var pk = new PrivateKey(e.toByteArray());
        String target = "04ffe558e388852f0120e46af2d1b370f85854a8eb0841811ece0e3e03d282d57c315dc72890a4f10a1481c031b03b351b0dc79901ca18a00cf009dbdb157a1d10";
        var result = pk.point.SEC65();
        System.out.println("-->Testing sec on e=5000: "+result.equals(target));
        System.out.println("res: "+result);
        System.out.println("----------------------------------------------------------");

        e = BigInteger.valueOf(2018).pow(5);
        pk = new PrivateKey(e.toByteArray());
        target = "04027f3da1918455e03c46f659266a1bb5204e959db7364d2f473bdf8f0a13cc9dff87647fd023c13b4a4994f17691895806e1b40b57f4fd22581a4f46851f3b06";
        result = pk.point.SEC65();
        System.out.println("-->Testing sec on e=2018^5: "+result.equals(target));
        System.out.println("res: "+result);
        System.out.println("----------------------------------------------------------");

        e = new BigInteger("deadbeef12345",16);
        pk = new PrivateKey(e.toByteArray());
        target = "04d90cd625ee87dd38656dd95cf79f65f60f7273b67d3096e68bd81e4f5342691f842efa762fd59961d0e99803c61edba8b3e3f7dc3a341836f97733aebf987121";
        var sec65 = pk.point.SEC65();
        System.out.println("--> Testing uncompressed SEC on e 0xdeadbeef12345:"+ target.equals(sec65));
        System.out.println("res: "+sec65);
        System.out.println("----------------------------------------------------------");

        var sec33 = pk.point.SEC33();
        target = "03d90cd625ee87dd38656dd95cf79f65f60f7273b67d3096e68bd81e4f5342691f";
        System.out.println("--> Testing compressed SEC on e 0xdeadbeef12345:"+ target.equals(sec33));
        System.out.println("res: "+sec33);
        System.out.println("----------------------------------------------------------");

        var parsed_point65 = pk.point.parseSEC(sec65);
        System.out.println("-->Testing parsed point from SEC65:");
        System.out.println(parsed_point65);
        System.out.println(pk.point.equals(parsed_point65));
        System.out.println("----------------------------------------------------------");

        var parsed_point = pk.point.parseSEC(sec33);
        System.out.println("-->Testing parsed point from compressed SEC33:");
        System.out.println(parsed_point);
        System.out.println(pk.point.equals(parsed_point));
        System.out.println("----------------------------------------------------------");
        var r = new BigInteger("37206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c6",16);
        var s = new BigInteger("8ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec",16);
        System.out.println("-->Testing signature DER for ");
        System.out.println("r="+r.toString(16));
        System.out.println("s="+s.toString(16));
        Signature sig = new Signature(r,s);
        var der_str =sig.DER();
        System.out.println(der_str);
        String target_der = "3045022037206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c60221008ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec";
        System.out.println(der_str.equals(target_der));
        System.out.println("----------------------------------------------------------");
        System.out.println("-->Testing BASE58 encoding");


        String a1 = "7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d";
        System.out.println(CryptoKit.encodeBase58(CryptoKit.hexStringToByteArray(a1)));

        String a2 = "eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c";
        System.out.println(CryptoKit.encodeBase58(CryptoKit.hexStringToByteArray(a2)));

        String a3 = "c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab6";
        System.out.println(CryptoKit.encodeBase58(CryptoKit.hexStringToByteArray(a3)));

        var e_str = "12345deadbeef";
        System.out.println("----------------------------------------------------------");
        System.out.println("--> Testing Address for priv key hex:"+e_str);
        var pk_addr = new PrivateKey(CryptoKit.hexStringToByteArray(e_str));
        var target_addr1 = "1F1Pn2y6pDb68E5nYJJeba4TLg2U7B6KF1";
        var res_addr1 = pk_addr.point.getAddress();
        System.out.println("address:" + res_addr1);
        System.out.println("--> Result:"+res_addr1.equals(target_addr1));;
        System.out.println("----------------------------------------------------------");

        var e2_n = BigInteger.valueOf(2020).pow(5);
        System.out.println("--> Testing Address for priv key :"+e2_n);
        var pk_addr2 = new PrivateKey(e2_n);
        var target_addr2 = "mopVkxp8UhXqRYbCYJsbeE1h1fiF64jcoH";
        var res_addr2 = pk_addr2.point.getTestnetAddress();
        System.out.println("address:" + res_addr2);
        System.out.println("--> Result:"+res_addr2.equals(target_addr2));;
        System.out.println("----------------------------------------------------------");

    }
}
