import java.math.BigInteger;

public class Signature {
    final BigInteger r;
    final BigInteger s;

    public Signature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    @Override
    public String toString() {
        return "Signature(" + r.toString(16) + "," + s.toString(16) + ')';
    }
}
