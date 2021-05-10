import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class PrivateKey {
    public final BigInteger secret;
    public final S256Point point;

    public PrivateKey(BigInteger secret) {
        this.secret = secret;
        var point = Secp256k1.G.multiply_bin(secret);
        this.point = new S256Point(point);
    }

    public Signature sign_random_k(BigInteger z) {
        int len = Secp256k1.N.bitLength();
        // TODO: use deterministic k (RFC 6979)
        // https://tools.ietf.org/html/rfc6979#appendix-A.3

        var k = new BigInteger(len,new Random());

        if (k.compareTo(Secp256k1.N)>=0)
            k = k.mod(Secp256k1.N);

        return sign(z,k);
    }

    public Signature sign_determinisk(BigInteger z) {
        var k = deterministic_k(z);
        return sign(z,k);
    }
    public Signature sign(BigInteger z,BigInteger k) {

        if (k.compareTo(Secp256k1.N)>=0)
            k = k.mod(Secp256k1.N);
        // r = x coordinate of (k*G)
        var r = (Secp256k1.G.multiply_bin(k)).getX().getNum();
        // k_inv = k^(N-2)
        var k_inv = k.modPow(Secp256k1.N.subtract(BigInteger.TWO),Secp256k1.N);
        // s = (z+r*secret)*k_inv%N

        var s = z.add(r.multiply(this.secret)).multiply(k_inv).mod(Secp256k1.N);
        // s > N/2 (for malleability better use lower values)
        if (s.compareTo(Secp256k1.N.divide(BigInteger.TWO))>0) {
            s = Secp256k1.N.subtract(s);
        }
        return new Signature(r,s);
    }

    public BigInteger deterministic_k(BigInteger z){
        byte[] k = new byte[32];
        byte[] v = new byte[32];
        byte zero = 0x00;
        byte one = 0x01;
        Arrays.fill(k,zero);
        Arrays.fill(v,one);

        if (z.compareTo(Secp256k1.N)>0) {
            z = z.subtract(Secp256k1.N);
        }

        var z_bytes = new byte[32];
        var ztob = z.toByteArray();
        for (int i=0;i<ztob.length;i++) {
            z_bytes[31-i] = ztob[ztob.length-1-i];
        }

        var secret_bytes = new byte[32];
        var stob = this.secret.toByteArray();
        for (int i=0;i<stob.length;i++) {
            secret_bytes[31-i] = stob[stob.length-1-i];
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.writeBytes(v);
        bos.write(zero);
        bos.writeBytes(secret_bytes);
        bos.writeBytes(z_bytes);

        byte[] all = bos.toByteArray();

        k = HMAC.calcHmacSha256(k,all);
        v = HMAC.calcHmacSha256(k,v);

        bos = new ByteArrayOutputStream();
        bos.writeBytes(v);
        bos.write(one);
        bos.writeBytes(secret_bytes);
        bos.writeBytes(z_bytes);

        all = bos.toByteArray();
        k = HMAC.calcHmacSha256(k,all);
        v = HMAC.calcHmacSha256(k,v);

        boolean go_on = true;

        while (go_on) {
            v = HMAC.calcHmacSha256(k,v);
            var candidate = new BigInteger(1,v);

            String hex_c = candidate.toString(16);

            if (candidate.compareTo(BigInteger.ONE)>=0 &&
                candidate.compareTo(Secp256k1.N)<0)
            return candidate;

            bos = new ByteArrayOutputStream();
            bos.writeBytes(v);
            bos.write(zero);
            k = HMAC.calcHmacSha256(k,bos.toByteArray());
            v = HMAC.calcHmacSha256(k,v);
        }

        return new BigInteger(z_bytes);
    }
}
