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

    @Override
    public String toString() {
        return "S256Point("+x+","+y+")";
    }
}
