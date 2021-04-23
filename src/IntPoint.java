import java.math.BigInteger;
import java.util.Objects;

public class IntPoint {
    private final BigInteger x,y,a,b;
    static final BigInteger BIGINF = null;

    private boolean inTheCurve() {
        // point at infinity should not be checked
        if (this.x==BIGINF && this.y==BIGINF) return true;

        var left = y.pow(2);
        var right = (x.pow(3)).add(x.multiply(a)).add(b);

        return  (left.compareTo(right)==0);
    }
    public IntPoint(BigInteger x, BigInteger y, BigInteger a, BigInteger b) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;

        if (!inTheCurve()){
            System.out.println("Not in the curve!" + this);
            System.exit(-1);
        }

    }
    public IntPoint(long x, long y, long a, long b) {
        this.x = BigInteger.valueOf(x);
        this.y = BigInteger.valueOf(y);
        this.a = BigInteger.valueOf(a);
        this.b = BigInteger.valueOf(b);
        if (!inTheCurve()){
            System.out.println("Not in the curve!"+this);
            System.exit(-1);
        }
    }

    public IntPoint add(IntPoint other) {
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
            return new IntPoint(BIGINF,BIGINF,this.a,this.b);
        }

        // point are not in vertical, and are different
        if (!this.x.equals(other.x)) {
            // slope = (y2-y1)/(x2-x1)
            var s = (other.y.subtract(this.y)).divide(other.x.subtract(this.x));
            // x3 = s^2-x1-x2
            var x3 = s.pow(2).subtract(this.x).subtract(other.x);
            // y3 = s(x1-x3)-y1
            var y3 = s.multiply(this.x.subtract(x3)).subtract(this.y);
            return new IntPoint(x3,y3,this.a,this.b);
        }

        // p1=p2
        if (this.equals(other)) {
            // special case of vertical tangent line
            if (this.y.equals(BigInteger.ZERO)) {
                return new IntPoint(BIGINF,BIGINF,this.a,this.b);
            }
            // 3*x1^2+a
            var num = this.x.pow(2).multiply(BigInteger.valueOf(3)).add(this.a);
            // slope
            var s = num.divide(this.y.multiply(BigInteger.TWO));
            var x3 = s.pow(2).subtract(this.x.multiply(BigInteger.TWO));
            var y3 = s.multiply(this.x.subtract(x3)).subtract(this.y);
            return new IntPoint(x3,y3,this.a,this.b);

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
        IntPoint point = (IntPoint) o;
        return Objects.equals(x, point.x) && Objects.equals(y, point.y) && Objects.equals(a, point.a) && Objects.equals(b, point.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, a, b);
    }
}