import java.math.BigInteger;
import java.util.Random;

public class PrivateKey {
    public final BigInteger secret;
    public final S256Point point;

    public PrivateKey(BigInteger secret) {
        this.secret = secret;
        var point = Secp256k1.G.multiply_bin(secret);
        this.point = new S256Point(point);
    }

    public Signature sign(BigInteger z) {
        int len = Secp256k1.N.bitLength();
        // TODO: use deterministic k (RFC 6979)
        // https://tools.ietf.org/html/rfc6979#appendix-A.3

        var k = new BigInteger(len,new Random());

        if (k.compareTo(Secp256k1.N)>=0)
            k = k.mod(Secp256k1.N);
        // r = x coordinate of (k*G)
        var r = (Secp256k1.G.multiply_bin(k)).getX().getNum();
        // k_inv = k^(N-2)
        var k_inv = k.modPow(Secp256k1.N.subtract(BigInteger.TWO),Secp256k1.N);
        // s = (z+r*secret)*k_inv%N
        var s = (z.add(r.multiply(this.secret))).multiply(k_inv.mod(Secp256k1.N));
        // s > N/2 (for malleability better use lower values)
        if (s.compareTo(Secp256k1.N.divide(BigInteger.TWO))>0) {
            s = Secp256k1.N.subtract(s);
        }
        return new Signature(r,s);
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

}
