import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoKit {

    // TODO: Replace this whenever bytes->hex string
    public static String bytesToHexString(byte [] bytes) {
        var num = new BigInteger(1,bytes);
        var str = num.toString(16);
        // TODO: removed when confirmed to avoid dep on bouncycastle
        if (!str.equals(Hex.toHexString(bytes))) {
            System.out.println("Failed check on bouncycastel replacement");
            System.exit(-1);
        }

        return str;
    }
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
    public static byte[] sha256(byte[] b) {
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

    public static byte[] hash256(byte[] b) {
        return sha256(sha256(b));
    }


    public static byte[] hash160(byte[] b) {
        return RIPEMD160(sha256(b));
    }

    public static byte[] hash256(String message) {
        return hash256(message.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] stringToBytes(String s ) {
        return  s.getBytes(StandardCharsets.UTF_8);
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
        BigInteger[] num_mod;
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

    public static byte[] calcHmacSha256(byte[] secretKey, byte[] message) {
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

    public static byte[] to32bytes(byte[] secret) {
        var bos = new ByteArrayOutputStream();
        for (int i=0;i<32-secret.length;i++)
            bos.write(0);
        bos.writeBytes(secret);
        return bos.toByteArray();
    }

    public static BigInteger litteEndianBytesToInt(byte[] bytes) {

        var reversed_bytes = to32bytes(reverseBytes(bytes));
        BigInteger little = new BigInteger(reversed_bytes);
        return little;
    }

    public static byte[] intToLittleEndianBytes(long n) {
        return intToLittleEndianBytes(BigInteger.valueOf(n));
    }

    public static byte[] intToLittleEndianBytes(BigInteger bi) {
        byte[] extractedBytes = bi.toByteArray();
        byte[] reversed = reverseBytes(to32bytes(extractedBytes));
        return reversed;
    }

    public static BigInteger littleEndianBytesToInt(byte[] little_bytes) {
        byte[] reversed = to32bytes(reverseBytes(little_bytes));
        var n = new BigInteger(reversed);
        return n;
    }

    public static byte[] reverseBytes(byte[] bytes)  {
        int size = bytes.length;
        byte[] reversed_bytes = new byte[size];

        for (int i = 0; i< size; i++)
            reversed_bytes[size-1-i] = bytes[i];

        return reversed_bytes;

    }

    public static long readVarint(ByteArrayInputStream bis) {
        byte[] buffer;
        long n=0;
        long i = bis.read();

        try {
            if (i==0xfd) {
                buffer = bis.readNBytes(2);
                n = litteEndianBytesToInt(buffer).longValue();
                return n;
            }
            else if(i==0xfe) {
                buffer = bis.readNBytes(4);
                n = litteEndianBytesToInt(buffer).longValue();
                return n;
            }
            else if(i==0xff) {
                buffer = bis.readNBytes(8);
                n = litteEndianBytesToInt(buffer).longValue();
                return n;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;

    }

    public static long readVarint(byte[] bytes) {
        var bis = new ByteArrayInputStream(bytes);
        return readVarint(bis);
    }

    public static byte[] encodeVarint(long i) {
        byte[] buffer;
        byte[] result;
        byte prefix;
        var bos = new ByteArrayOutputStream();
        if (i<0xfd) {
            // single byte, not need to reorder little endian
            return BigInteger.valueOf(i).toByteArray();
        }
        else if (i<0x10000) {
            buffer = intToLittleEndianBytes(i);
            prefix = (byte)0xfd;
            bos.write(prefix);
            bos.write(buffer,0,2);
            result = bos.toByteArray();
            return result;
        }
        else if (i<0x100000000L) {
            buffer = intToLittleEndianBytes(i);
            prefix = (byte)0xfe;
            bos.write(prefix);
            bos.write(buffer,0,4);
            result = bos.toByteArray();
            return result;
        }
        // check if less than 2^64
        // cannot use long
        //else if (i<new BigInteger("10000000000000000",16).longValue()) {
        else if (BigInteger.valueOf(i).compareTo(BigInteger.TWO.pow(64))<0) {
            buffer = intToLittleEndianBytes(i);
            prefix = (byte)0xff;
            bos.write(prefix);
            bos.write(buffer,0,8);
            result = bos.toByteArray();
            return result;
        }
        return null;
    }

    // used for encoding stack nums
    public static byte[] encodeNum(BigInteger n) {
        byte[] res = null;
        var bos = new ByteArrayOutputStream();

        // empty byte if 0
        if (n.compareTo(BigInteger.ZERO)==0)
            return res;

        var abs_n = n.abs();
        boolean negative = n.compareTo(BigInteger.ZERO) <0;

        // we revert the bytes of the number (should be little endian)
        while (abs_n.compareTo(BigInteger.ZERO)!=0) {
            var absn_bytes =abs_n.toByteArray();
            bos.write(absn_bytes[absn_bytes.length-1]);
            abs_n = abs_n.shiftRight(8);
        }

        res = bos.toByteArray();

        // we must restore the sign, if necessary....
        // if the most significant byte (rightmost, since is little endian)
        // is like 1xxxxxxx we must be sure that the 1 is not considered as sign
        if ((res[res.length-1] & 0x80)!=0) {
            // we add a further 1xxxxxxxx byte on the right, to encode the sign
            if (negative)
                bos.write(0x80);
            else
                bos.write(0);
            res = bos.toByteArray();
        } // there was not 1xxxxxxxxx, so we can just add the 1 for the required sign
        else if (negative) {
            res = bos.toByteArray();
            res[res.length-1] |= 0x80;
        }
        return res;
    }

    public static byte[] encodeNum(long n) {
        return encodeNum(BigInteger.valueOf(n));
    }
}


