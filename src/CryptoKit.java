import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoKit {
    // TODO: REPLACE THIS whenever hex-> bytes
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        // add missing leading 0 to make total amount even
        if (len%2!=0) {
            s = "0"+s;
            len++;
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] RIPEMD160(byte[] r) {
        RIPEMD160Digest d = new RIPEMD160Digest();
        d.update(r, 0, r.length);
        byte[] o = new byte[d.getDigestSize()];
        d.doFinal(o, 0);
        return o;
    }
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


    public static final byte[] hash160(byte[] b) {
        return RIPEMD160(sha256(b));
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
        return prefix+result;
        //return result;
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

    static public byte[] calcHmacSha256(byte[] secretKey, byte[] message) {
      byte[] hmacSha256 = null;
      try {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
        mac.init(secretKeySpec);
        hmacSha256 = mac.doFinal(message);
      } catch (Exception e) {
        throw new RuntimeException("Failed to calculate hmac-sha256", e);
      }
      return hmacSha256;
    }

    static public byte[] to32bytes(byte[] secret) {
        var bos = new ByteArrayOutputStream();
        for (int i=0;i<32-secret.length;i++)
            bos.write(0);
        bos.writeBytes(secret);
        return bos.toByteArray();
    }
}


