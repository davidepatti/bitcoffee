import java.math.BigInteger;

public class S256Field extends FieldElement{
    public S256Field(BigInteger n) {
        super(n,Secp256k1.p);
    }

    public FieldElement sqrt(S256Field element) {
        return this.pow(Secp256k1.p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)));
    }
}
