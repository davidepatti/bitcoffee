
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class PrivateKey {
    public final byte[] secret_bytes;
    public final BigInteger secret_n;
    public final S256Point point;

    public PrivateKey(byte[] secret) {
        this.secret_n = new BigInteger(1,secret);
        this.secret_bytes = CryptoKit.to32bytes(secret);
        var point = Secp256k1.G.multiply_bin(secret_n);
        this.point = new S256Point(point);
    }
    public PrivateKey(BigInteger n) {
        var secret_bin = CryptoKit.hexStringToByteArray(n.toString(16));
        this.secret_bytes = CryptoKit.to32bytes(secret_bin);
        this.secret_n = n;
        var point = Secp256k1.G.multiply_bin(this.secret_n);
        this.point = new S256Point(point);
    }

    public PrivateKey(long n) {
        var secret_bin= CryptoKit.hexStringToByteArray(Long.toHexString(n));
        this.secret_bytes = CryptoKit.to32bytes(secret_bin);
        this.secret_n = BigInteger.valueOf(n);
        var point = Secp256k1.G.multiply_bin(this.secret_n);
        this.point = new S256Point(point);
    }

    public PrivateKey(String secret) {
        // no need to expand to 32bytes
        this.secret_bytes = CryptoKit.hash256(secret);
        this.secret_n = new BigInteger(1,this.secret_bytes);
        var point = Secp256k1.G.multiply_bin(this.secret_n);
        this.point = new S256Point(point);
    }

    public Signature sign_random_k(byte[] z_bytes) {
        int len = Secp256k1.N.bitLength();
        var k = new BigInteger(len,new Random());

        if (k.compareTo(Secp256k1.N)>=0)
            k = k.mod(Secp256k1.N);

        return sign(z_bytes,k);
    }

    public Signature sign_determinisk(byte[] z_bytes) {
        // use deterministic k (RFC 6979)
        var k = deterministic_k(z_bytes);
        return sign(z_bytes,k);
    }
    public Signature sign(byte[] z_bytes,BigInteger k) {

        var z = new BigInteger(1,z_bytes);

        if (k.compareTo(Secp256k1.N)>=0)
            k = k.mod(Secp256k1.N);
        // r = x coordinate of (k*G)
        var r = (Secp256k1.G.multiply_bin(k)).getX().getNum();
        // k_inv = k^(N-2)
        var k_inv = k.modPow(Secp256k1.N.subtract(BigInteger.TWO),Secp256k1.N);
        // s = (z+r*secret)*k_inv%N

        var s = z.add(r.multiply(this.secret_n)).multiply(k_inv).mod(Secp256k1.N);
        // s > N/2 (for malleability better use lower values)
        if (s.compareTo(Secp256k1.N.divide(BigInteger.TWO))>0) {
            s = Secp256k1.N.subtract(s);
        }
        return new Signature(r,s);
    }

    public BigInteger deterministic_k(byte[] z_bytes){
        byte[] k = new byte[32];
        byte[] v = new byte[32];
        byte zero = 0x00;
        byte one = 0x01;
        Arrays.fill(k,zero);
        Arrays.fill(v,one);

        var z_num = new BigInteger(1,z_bytes);

        if (z_num.compareTo(Secp256k1.N)>0) {
            z_num = z_num.subtract(Secp256k1.N);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.writeBytes(v);
        bos.write(zero);
        bos.writeBytes(secret_bytes);
        bos.writeBytes(z_bytes);

        byte[] all = bos.toByteArray();

        k = CryptoKit.calcHmacSha256(k,all);
        v = CryptoKit.calcHmacSha256(k,v);

        bos = new ByteArrayOutputStream();
        bos.writeBytes(v);
        bos.write(one);
        bos.writeBytes(secret_bytes);
        bos.writeBytes(z_bytes);

        all = bos.toByteArray();
        k = CryptoKit.calcHmacSha256(k,all);
        v = CryptoKit.calcHmacSha256(k,v);

        boolean go_on = true;

        while (go_on) {
            v = CryptoKit.calcHmacSha256(k,v);
            var candidate = new BigInteger(1,v);

            if (candidate.compareTo(BigInteger.ONE)>=0 &&
                candidate.compareTo(Secp256k1.N)<0)
            return candidate;

            bos = new ByteArrayOutputStream();
            bos.writeBytes(v);
            bos.write(zero);
            k = CryptoKit.calcHmacSha256(k,bos.toByteArray());
            v = CryptoKit.calcHmacSha256(k,v);
        }

        return new BigInteger(z_bytes);
    }

    public byte[] wif(boolean compressed, boolean testnet) {
        return null;
    }

}
