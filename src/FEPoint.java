import java.math.BigInteger;
import java.util.Objects;

// TODO: abstract to common element
public class FEPoint {
    private final FieldElement x,y,a,b;
    static final FieldElement BIGINF = null;

    private boolean inTheCurve() {
        // point at infinity should not be checked
        if (this.x.equals(BIGINF) && this.y.equals(BIGINF)) return true;

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

    public FEPoint add(FEPoint other) {
        if (!this.a.equals(other.a) || !this.b.equals(other.b)) {
            System.out.println("Not in the same curve "+this+" and "+other);
            System.exit(-1);
        }

        // one of the points it's at infinity (identity)
        if (this.x==null) return other;
        if (other.x==null) return this;

        // points are in vertical line, resulting in infinity
        // TODO: check whether makes sense a unique inf point for all curves
        if (this.x.equals(other.x) && this.y.equals(other.y.negate())) {
            return new FEPoint(BIGINF,BIGINF,this.a,this.b);
        }

        // point are not in vertical, and are different
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
            // special case of vertical tangent line
            if (this.y.equals(new FieldElement(BigInteger.ZERO,prime))) {
                return new FEPoint(BIGINF,BIGINF,this.a,this.b);
            }
            // 3*x1^2+a
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

    @Override
    public String toString() {
        return "Point("+x+","+y+","+a+","+b+")";
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