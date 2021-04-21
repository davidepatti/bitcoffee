import java.math.BigInteger;
import java.util.Objects;

// TODO: mutable?
public final class FieldElement {
    private final BigInteger num;
    private final BigInteger prime;

    public FieldElement(BigInteger num, BigInteger prime) {

        if (num.compareTo(prime)>=0 || num.compareTo(BigInteger.ZERO) <0) {
            System.out.println(" Element "+num+ " not in 0.."+(prime.subtract(BigInteger.ONE)));
            System.exit(-1);
        }
        this.num =num;
        this.prime = prime;
    }

    public FieldElement(FieldElement other) {
        this.num = other.num;
        this.prime = other.prime;
    }

    public FieldElement plus(FieldElement other) {
        if (this.prime.compareTo(other.prime)!=0) {
            System.out.println("Different fields "+this.prime+" and "+other.prime);
            System.exit(-1);
        }

        var sum = (this.num.add(other.num)).mod(this.prime);
        return new FieldElement(sum,this.prime);
    }

    public FieldElement mul(FieldElement other) {

        if (this.prime.compareTo(other.prime)!=0) {
            System.out.println("Different fields");
            System.exit(-1);
        }

        var product = (this.num.multiply(other.num)).mod(this.prime);

        return new FieldElement(product,this.prime);
    }

    public FieldElement pow2(int exponent) {
        var exp = BigInteger.valueOf(exponent);
        exp = exp.mod(this.prime.subtract(BigInteger.ONE));

        var num_res  = this.num.modPow(exp,this.prime);

        //long num =  (long) Math.pow(base,(double)n);
        //num = num%this.prime;

        return new FieldElement(num_res,this.prime);
    }

    @Override
    public String toString() {
        return "FieldElement{" + num + "," + prime + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldElement that = (FieldElement) o;
        return num.equals(that.num) && prime.equals(that.prime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, prime);
    }
}
