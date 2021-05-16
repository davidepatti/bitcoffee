import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Secp256k1 {
    public static final BigInteger a = BigInteger.ZERO;
    public static final BigInteger b = BigInteger.valueOf(7);
    public static final BigInteger p = (BigInteger.TWO).pow(256).subtract((BigInteger.TWO).pow(32)).subtract(BigInteger.valueOf(977));
    public static final BigInteger Gx = new BigInteger("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798",16);
    public static final BigInteger Gy = new BigInteger("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8",16);
    public static final BigInteger N = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141",16);
    public static final S256Point G = new S256Point(Gx,Gy);

    public static final byte[] sha256(byte[] b) {
        MessageDigest digester = null;
        try {
            digester = MessageDigest.getInstance("SHA-256");
        } catch (
                NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hash = digester.digest(b);
        return hash;
    }

    public static final byte[] hash256(byte[] b) {
        return sha256(sha256(b));
    }

    public static final byte[] ripemd160(byte[] b) {
        MessageDigest digester = null;
        try {
            digester = MessageDigest.getInstance("RIPEMD160");
        } catch (
                NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hash = digester.digest(b);
        return hash;
    }

    public static final byte[] hash160(byte[] b) {
        return ripemd160(sha256(b));
    }

    public static final byte[] hash256(String message) {
        return hash256(message.getBytes(StandardCharsets.UTF_8));
    }

    public static String encodeBase58(byte[] s) {
        String BASE58_AlPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        int count = 0;
        for (byte b: s) {
            if (b==0) count++;
            else break;
        }
        // TODO: fix this
        if (count>1) {
            System.out.println("Warning on encodeBase58 leading zero' byets");
            System.exit(-1);
        }
        var num = new BigInteger(1,s);
        String prefix = "";
        for (int c=0;c<count;c++)
            prefix = "1"+prefix;

        String result = "";
        BigInteger[] num_mod = new BigInteger[2];
        while (num.compareTo(BigInteger.ZERO)>0) {
            num_mod = num.divideAndRemainder(BigInteger.valueOf(58));
            num = num_mod[0];
            int mod = num_mod[1].intValue();
            result = BASE58_AlPHABET.charAt(mod)+result;
        }
        //return prefix+result;
        return result;
    }

    public static String encodeBase58Checksum(byte[] b) {
        var hash_b = hash256(b);
        var bos = new ByteArrayOutputStream();
        bos.writeBytes(b);
        bos.write(hash_b[0]);
        bos.write(hash_b[1]);
        bos.write(hash_b[2]);
        bos.write(hash_b[3]);
        var res = bos.toByteArray();
        var res_enc = encodeBase58(res);
        return res_enc;
    }

}
