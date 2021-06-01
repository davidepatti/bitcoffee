import org.bouncycastle.util.encoders.Hex;

public class TestTransactions {
    public static void main(String args[]) {
        System.out.println("Testing varint");
        String hex_string = "64";
        long target_n = 100;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        long result = CryptoKit.readVarint(CryptoKit.hexStringToByteArray(hex_string));
        System.out.println("Result:"+(target_n==result));

        hex_string = "fdff00";
        target_n = 255;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        result = CryptoKit.readVarint(CryptoKit.hexStringToByteArray(hex_string));
        System.out.println("Result read:"+(target_n==result));
        System.out.println("Result encode:"+hex_string.equals(Hex.toHexString(CryptoKit.encodeVarint(target_n))));

        hex_string = "fd2b02";
        target_n = 555;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        result = CryptoKit.readVarint(CryptoKit.hexStringToByteArray(hex_string));
        System.out.println("Result read:"+(target_n==result));
        System.out.println("Result encode:"+hex_string.equals(Hex.toHexString(CryptoKit.encodeVarint(target_n))));

        hex_string = "fe7f110100";
        target_n = 70015;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        result = CryptoKit.readVarint(CryptoKit.hexStringToByteArray(hex_string));
        System.out.println("Result read:"+(target_n==result));
        System.out.println("Result encode:"+hex_string.equals(Hex.toHexString(CryptoKit.encodeVarint(target_n))));

        hex_string = "ff6dc7ed3e60100000";
        target_n = 18005558675309L;
        System.out.println("hex="+hex_string+ " target_n="+target_n);
        result = CryptoKit.readVarint(CryptoKit.hexStringToByteArray(hex_string));
        System.out.println("Result read:"+(target_n==result));
        System.out.println("Result encode:"+hex_string.equals(Hex.toHexString(CryptoKit.encodeVarint(target_n))));
    }
}
