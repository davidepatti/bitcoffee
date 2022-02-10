package Tests;

import bitcoffee.*;
import java.math.BigInteger;
import java.util.Objects;

public class TestSerialization {
    public static void main(String[] args) {
        var e = BigInteger.valueOf(5000);
        var pk = new PrivateKey(e.toByteArray());
        String target = "04ffe558e388852f0120e46af2d1b370f85854a8eb0841811ece0e3e03d282d57c315dc72890a4f10a1481c031b03b351b0dc79901ca18a00cf009dbdb157a1d10";
        var result = pk.point.SEC65();
        System.out.println("-->Testing sec65 on e="+e+": "+result.equals(target));
        System.out.println("G sec65 = "+ Secp256k1.G.SEC65());
        System.out.println("res: "+result);
        System.out.println("----------------------------------------------------------");

        e = BigInteger.valueOf(2018).pow(5);
        pk = new PrivateKey(e.toByteArray());
        target = "04027f3da1918455e03c46f659266a1bb5204e959db7364d2f473bdf8f0a13cc9dff87647fd023c13b4a4994f17691895806e1b40b57f4fd22581a4f46851f3b06";
        result = pk.point.SEC65();
        System.out.println("-->Testing sec65 on e=2018^5: "+result.equals(target));
        System.out.println("res: "+result);
        System.out.println("----------------------------------------------------------");

        e = new BigInteger("deadbeef12345",16);
        pk = new PrivateKey(e.toByteArray());
        target = "04d90cd625ee87dd38656dd95cf79f65f60f7273b67d3096e68bd81e4f5342691f842efa762fd59961d0e99803c61edba8b3e3f7dc3a341836f97733aebf987121";
        var sec65 = pk.point.SEC65();
        System.out.println("--> Testing uncompressed SEC65 on e 0xdeadbeef12345:"+ target.equals(sec65));
        System.out.println("res: "+sec65);
        System.out.println("----------------------------------------------------------");

        var sec33 = pk.point.SEC33();
        target = "03d90cd625ee87dd38656dd95cf79f65f60f7273b67d3096e68bd81e4f5342691f";
        System.out.println("--> Testing compressed SEC33 on e 0xdeadbeef12345:"+ target.equals(sec33));
        System.out.println("res: "+sec33);
        System.out.println("----------------------------------------------------------");

        var parsed_point65 = S256Point.parseSEC(sec65);
        System.out.println("-->Testing parsed point from SEC65:");
        System.out.println(parsed_point65);
        System.out.println(pk.point.equals(parsed_point65));
        System.out.println("----------------------------------------------------------");

        var parsed_point = S256Point.parseSEC(sec33);
        System.out.println("-->Testing parsed point from compressed SEC33:");
        System.out.println(parsed_point);
        System.out.println(pk.point.equals(parsed_point));


        System.out.println("----------------------------------------------------------");
        var r = new BigInteger("37206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c6",16);
        var s = new BigInteger("8ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec",16);
        System.out.println("-->Testing signature DER for: ");
        Signature sig = new Signature(r,s);
        System.out.println(sig);
        var der_str =sig.DER();
        System.out.println(der_str);
        String target_der = "3045022037206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c60221008ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec";
        System.out.println(der_str.equals(target_der));
        System.out.println("Back parsing "+target_der);
        Signature sig_res = Signature.parse(Kit.hexStringToByteArray(target_der));
        System.out.println("Result:");
        System.out.println(sig_res);



        System.out.println("----------------------------------------------------------");
        System.out.println("-->Testing BASE58 encoding");

        String a1 = "7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d";
        System.out.println(Kit.encodeBase58(Kit.hexStringToByteArray(a1)));

        String a2 = "eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c";
        System.out.println(Kit.encodeBase58(Kit.hexStringToByteArray(a2)));

        String a3 = "c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab6";
        System.out.println(Kit.encodeBase58(Kit.hexStringToByteArray(a3)));

        var e_str = "12345deadbeef";
        System.out.println("----------------------------------------------------------");
        System.out.println("--> Testing Address for priv key hex:"+e_str);
        var pk_addr = new PrivateKey(Kit.hexStringToByteArray(e_str));
        var target_addr1 = "1F1Pn2y6pDb68E5nYJJeba4TLg2U7B6KF1";
        var res_addr1 = pk_addr.point.getP2pkhAddress(true);
        System.out.println("address:" + res_addr1);
        System.out.println("--> Result:"+res_addr1.equals(target_addr1));

        System.out.println("----------------------------------------------------------");
        var name = "Satoshi Nakamoto";
        System.out.println("Testing address for brainwallet: "+name);
        var name_sha256 = Kit.sha256(Kit.asciiStringToBytes(name));
        var name_hex = Kit.bytesToHexString(name_sha256);
        var some_key = new PrivateKey(name_sha256);
        var some_addr_compressed = some_key.point.getP2pkhAddress(true);
        var some_addr_not_compresssed = some_key.point.getP2pkhAddress(false);
        System.out.println("(sec33): "+ some_addr_compressed);
        System.out.println("(sec65): "+ some_addr_not_compresssed);

        System.out.println("----------------------------------------------------------");

        var e2_n = BigInteger.valueOf(2020).pow(5);
        System.out.println("--> Testing Address for priv key :"+e2_n);
        var pk_addr2 = new PrivateKey(e2_n);
        var target_addr2 = "mopVkxp8UhXqRYbCYJsbeE1h1fiF64jcoH";
        var res_addr2 = pk_addr2.point.getP2pkhTestnetAddress();
        System.out.println("address:" + res_addr2);
        System.out.println("--> Result:"+res_addr2.equals(target_addr2));
        System.out.println("----------------------------------------------------------");

        var ewif = "54321deadbeef";
        System.out.println("--> Testing WIF for priv key hex:"+ewif);
        var pkwif = new PrivateKey(Kit.hexStringToByteArray(ewif));
        var reswif = pkwif.getWIF(true,false);
        var target_wif = "KwDiBf89QgGbjEhKnhXJuH7LrciVrZi3qYjgiuQJv1h8Ytr2S53a";
        System.out.println("WIF: "+reswif);
        System.out.println("-->Result:"+reswif.equals(target_wif));
        System.out.println("----------------------------------------------------------");
        System.out.println("Testing little endian to int");

        var le_hex = "99c3980000000000";
        var le_bytes = Kit.hexStringToByteArray(le_hex);
        var le_target = BigInteger.valueOf(10011545);
        var le_n = Kit.litteEndianBytesToInt(le_bytes);
        System.out.println("Little Endian bytes:"+le_hex+" int result:"+le_n+ " -->"+le_n.equals(le_target));
        //var res_letoba = CryptoKi
        var le_hex2 = "a135ef0100000000";
        var le_bytes2 = Kit.hexStringToByteArray(le_hex2);
        var le_target2 = BigInteger.valueOf(32454049);
        var le_n2 = Kit.litteEndianBytesToInt(le_bytes2);
        System.out.println("Little Endian bytes:"+le_hex2+" int result:"+le_n2+ " -->"+le_n2.equals(le_target2));
        System.out.println("----------------------------------------------------------");
        System.out.println("Testing int to little endian");

        var le_bytes3 = Kit.intToLittleEndianBytes(BigInteger.ONE);
        var les3 = Kit.bytesToHexString(le_bytes3);
        System.out.println("num 1 to little endian bytes:");
        System.out.println(les3);
        var nle = BigInteger.valueOf(10011545);
        System.out.println("num "+nle+" to little endian bytes:");
        var leb3 = Kit.intToLittleEndianBytes(nle);
        les3 = Kit.bytesToHexString(leb3);
        System.out.println(les3);
        System.out.println("Result:"+les3.equals("99c3980000000000000000000000000000000000000000000000000000000000"));


        System.out.println("-----------------------------------------------------------");
        System.out.println("Testing varint");
        String hex_string = "64";
        long target_n = 100;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        long result_n = Kit.readVarint(Kit.hexStringToByteArray(hex_string));
        System.out.println("Result:"+(target_n==result_n));

        hex_string = "fdff00";
        target_n = 255;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        result_n = Kit.readVarint(Kit.hexStringToByteArray(hex_string));
        System.out.println("Result read:"+(target_n==result_n));
        System.out.println("Result encode:"+hex_string.equals(Kit.bytesToHexString(Objects.requireNonNull(Kit.encodeVarint(target_n)))));

        hex_string = "fd2b02";
        target_n = 555;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        result_n = Kit.readVarint(Kit.hexStringToByteArray(hex_string));
        System.out.println("Result read:"+(target_n==result_n));
        System.out.println("Result encode:"+hex_string.equals(Kit.bytesToHexString(Objects.requireNonNull(Kit.encodeVarint(target_n)))));

        hex_string = "fe7f110100";
        target_n = 70015;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        result_n = Kit.readVarint(Kit.hexStringToByteArray(hex_string));
        System.out.println("Result read:"+(target_n==result_n));
        System.out.println("Result encode:"+hex_string.equals(Kit.bytesToHexString(Objects.requireNonNull(Kit.encodeVarint(target_n)))));

        hex_string = "ff6dc7ed3e60100000";
        target_n = 18005558675309L;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        result_n = Kit.readVarint(Kit.hexStringToByteArray(hex_string));
        System.out.println("Result read:"+(target_n==result_n));
        System.out.println("Result encode:"+hex_string.equals(Kit.bytesToHexString(Objects.requireNonNull(Kit.encodeVarint(target_n)))));
        System.out.println("-----------------------------------------------------------");

    }
}
