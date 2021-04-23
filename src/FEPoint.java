import java.math.BigInteger;
import java.util.Objects;

// TODO: abstract to common element
public class FEPoint {
    private final FieldElement x,y,a,b;
    static final FieldElement BIGINF = null;

    private boolean inTheCurve() {
        // point at infinity should not be checked
        if (this.x==null && this.y==null) return true;

        var left = y.pow(2);
        var right = (x.pow(3)).add(x.multiply(a)).add(b);

        return  (left.equals(right));
    }

    public FEPoint(FieldElement x, FieldElement y, FieldElement a, FieldElement b) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;

        if (!inTheCurve()){
            System.out.println("Not in the curve!" + this);
            System.exit(-1);
        }

    }
    public FEPoint(FEPoint other) {
        this.x = other.x;
        this.y = other.y;
        this.a = other.a;
        this.b = other.b;
    }

    public FEPoint add(FEPoint other) {
        if (!this.a.equals(other.a) || !this.b.equals(other.b)) {
            System.out.println("Not in the same curve "+this+" and "+other);
            System.exit(-1);
        }

        // Case 0.0: this is the point at infinity, return other
        if (this.x==null) return other;
        // Case 0.1: other is the point at infinity, return this
        if (other.x==null) return this;

        // points are in vertical line, resulting in infinity
        //Case 1: this.x == other.x, this.y != other.y Result is point at infinity
        // TODO: check whether makes sense a unique inf point for all curves
        if (this.x.equals(other.x) && !this.y.equals(other.y)) {
            return new FEPoint(BIGINF,BIGINF,this.a,this.b);
        }

        // Case 2: self.x ≠ other.x
        // point are not in vertical, and are different
        // Formula (x3,y3)==(x1,y1)+(x2,y2)
        // s=(y2-y1)/(x2-x1)
        // x3=s^2-x1-x2
        // y3=s*(x1-x3)-y1
        if (!this.x.equals(other.x)) {
            // slope = (y2-y1)/(x2-x1)
            var s = (other.y.subtract(this.y)).divide(other.x.subtract(this.x));
            // x3 = s^2-x1-x2
            var x3 = s.pow(2).subtract(this.x).subtract(other.x);
            // y3 = s(x1-x3)-y1
            var y3 = s.multiply(this.x.subtract(x3)).subtract(this.y);
            return new FEPoint(x3,y3,this.a,this.b);
        }

        // p1=p2
        if (this.equals(other)) {
            BigInteger prime = a.getPrime();
            // Case 4: if we are tangent to the vertical line, we return the point at infinity
            if (this.y.equals(new FieldElement(BigInteger.ZERO,prime))) {
                return new FEPoint(BIGINF,BIGINF,this.a,this.b);
            }

            // Case 3 this=other  Formula (x3,y3)=(x1,y1)+(x1,y1)
            // s=(3*x1^2+a)/(2*y1)
            // x3=s^2-2*x1
            // y3=s*(x1-x3)-y1
            var num = this.x.pow(2).multiply(new FieldElement(3,prime)).add(this.a);
            // slope
            var s = num.divide(this.y.multiply(new FieldElement(2,prime)));
            var x3 = s.pow(2).subtract(this.x.multiply(new FieldElement(2,prime)));
            var y3 = s.multiply(this.x.subtract(x3)).subtract(this.y);
            return new FEPoint(x3,y3,this.a,this.b);

        }

        System.out.println("Unhandled point addition case!");
        System.exit(-1);
        return null;

    }

    public FEPoint multiply(BigInteger factor) {
        BigInteger n;
        FEPoint sum = new FEPoint(this);
        for (n = BigInteger.ONE; n.compareTo(factor)<0;n=n.add(BigInteger.ONE)) {
            sum = sum.add(this);
        }
        return sum;
    }
    @Override
    public String toString() {
        return "Point("+x+","+y+","+a+","+b+")";
    }

    public String getCoordString(){
        if (this.x==null || this.y==null) return "(INF,INF)";

        return "("+this.x.getNum()+","+this.y.getNum()+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FEPoint point = (FEPoint) o;
        return Objects.equals(x, point.x) && Objects.equals(y, point.y) && Objects.equals(a, point.a) && Objects.equals(b, point.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, a, b);
    }
}