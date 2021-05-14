import java.math.BigInteger;

public class S256Field extends FieldElement{
    public S256Field(BigInteger n) {
        super(n,Secp256k1.p);
    }

    public S256Field sqrt() {
        return new S256Field(this.pow(Secp256k1.p.add(BigInteger.ONE).divide(BigInteger.valueOf(4))).getNum());
    }
}
