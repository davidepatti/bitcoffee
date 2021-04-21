import java.util.Objects;

// TODO: mutable?
public final class FieldElement {
    private final long num;
    private final long prime;

    public FieldElement(long num, long prime) {
        if (num>= prime || num <0) {
            System.out.println(" Element "+num+ " not in 0.."+(prime-1));
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
        if (this.prime!=other.prime) {
            System.out.println("Different fields "+this.prime+" and "+other.prime);
            System.exit(-1);
        }

        long sum = (this.num+ other.num)%this.prime;
        return new FieldElement(sum,this.prime);
    }

    public FieldElement mul(FieldElement other) {

        if (this.prime!=other.prime) {
            System.out.println("Different fields");
            System.exit(-1);
        }

        long product = (this.num* other.num) % this.prime;

        return new FieldElement(product,this.prime);
    }

    public FieldElement pow(int exponent) {
        long n = this.num;
        while (--exponent>0) n = n*this.num;
        n = n%this.prime;
        return new FieldElement(n,this.prime);
    }
    public FieldElement pow2(int exponent) {
        long n = exponent % (this.prime-1);
        double base = this.num;

        long num =  (long) Math.pow(base,(double)n);
        num = num%this.prime;

        return new FieldElement(num,this.prime);
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
        return num == that.num && prime == that.prime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, prime);
    }
}
