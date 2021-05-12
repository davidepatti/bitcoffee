import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Secp256k1 {
    public static final BigInteger a = BigInteger.ZERO;
    public static final BigInteger b = BigInteger.valueOf(7);
    public static final BigInteger p = (BigInteger.TWO).pow(256).subtract((BigInteger.TWO).pow(32)).subtract(BigInteger.valueOf(977));
    public static final BigInteger Gx = new BigInteger("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798",16);
    public static final BigInteger Gy = new BigInteger("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8",16);
    public static final BigInteger N = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141",16);
    public static final S256Point G = new S256Point(Gx,Gy);


    public static final byte[] hash256(String message) {
        MessageDigest digester = null;
        try {
            digester = MessageDigest.getInstance("SHA-256");
        } catch (
                NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hash = digester.digest(digester.digest(message.getBytes(StandardCharsets.UTF_8)));

        return hash;
    }


}
