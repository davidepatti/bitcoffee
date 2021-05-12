import java.math.BigInteger;

public class S256Point extends FieldElementPoint{
    private final BigInteger x;
    private final BigInteger y;

    public S256Point(BigInteger x, BigInteger y) {
        super(new FieldElement(x,Secp256k1.p),
              new FieldElement(y,Secp256k1.p),
              new FieldElement(Secp256k1.a,Secp256k1.p),
              new FieldElement(Secp256k1.b,Secp256k1.p));
        this.x = x;
        this.y = y;
    }

    public S256Point(FieldElementPoint other) {
        super(other);

        this.x = other.getX().getNum();
        this.y = other.getY().getNum();
    }

    @Override
    public String toString() {
        return "S256Point("+x.toString(16)+","+y.toString(16)+")";
    }

    public boolean verify(BigInteger z, Signature sig) {
        var s_inv = sig.s.modPow(Secp256k1.N.subtract(BigInteger.TWO), Secp256k1.N);
        var u = z.multiply(s_inv.mod(Secp256k1.N));
        var v = sig.r.multiply(s_inv.mod(Secp256k1.N));
        var total = Secp256k1.G.multiply_bin(u).add(this.multiply_bin(v));
        return  total.getX().getNum().equals(sig.r);
    }

    public String getSerialX() {
        String x = this.getX().getNum().toString(16);
        // put missing leading zeros to reach 32bytes hex
        while (x.length()<64) {
            x = "0"+x;
        }
        return x;
    }
    public String getSerialY() {
        String y = this.getY().getNum().toString(16);
        // put missing leading zeros to reach 32bytes hex
        while (y.length()<64) {
            y = "0"+y;
        }
        return y;
    }

    public String sec() {

        String unc = "04"+this.getSerialX()+this.getSerialY();
        return unc;
    }
}
