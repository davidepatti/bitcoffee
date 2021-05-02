import java.math.BigInteger;
import java.util.Objects;

// TODO: abstract to common element
public class FieldElementPoint {
    private final FieldElement x,y,a,b;
    static final FieldElement BIGINF = null;

    private boolean inTheCurve() {
        // point at infinity should not be checked
        if (this.x==null && this.y==null) return true;

        var left = y.pow(BigInteger.TWO);
        var right = (x.pow(BigInteger.valueOf(3))).add(x.multiply(a)).add(b);

        return  (left.equals(right));
    }

    // when just two coords, assume is a default sec256k1 point
    public FieldElementPoint (BigInteger x, BigInteger y) {
        this.x = new FieldElement(x,Secp256k1.p);
        this.y = new FieldElement(y,Secp256k1.p);
        this.a = new FieldElement(Secp256k1.a,Secp256k1.p);
        this.b = new FieldElement(Secp256k1.b,Secp256k1.p);
    }

    public FieldElementPoint(FieldElement x, FieldElement y, FieldElement a, FieldElement b) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;

        if (!inTheCurve()){
            System.out.println("Not in the curve!" + this);
            System.exit(-1);
        }

    }
    public FieldElementPoint(FieldElementPoint other) {
        this.x = other.x;
        this.y = other.y;
        this.a = other.a;
        this.b = other.b;
        if (!inTheCurve()){
            System.out.println("Not in the curve!" + this);
            System.exit(-1);
        }
    }

    public FieldElementPoint add(FieldElementPoint other) {
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
            return new FieldElementPoint(BIGINF,BIGINF,this.a,this.b);
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
            var x3 = s.pow(BigInteger.TWO).subtract(this.x).subtract(other.x);

            // y3 = s(x1-x3)-y1
            var y3 = s.multiply(this.x.subtract(x3)).subtract(this.y);
            return new FieldElementPoint(x3,y3,this.a,this.b);
        }

        // p1=p2
        if (this.equals(other)) {
            BigInteger prime = a.getPrime();
            // Case 4: if we are tangent to the vertical line, we return the point at infinity
            if (this.y.equals(new FieldElement(BigInteger.ZERO,prime))) {
                return new FieldElementPoint(BIGINF,BIGINF,this.a,this.b);
            }

            // Case 3 this=other  Formula (x3,y3)=(x1,y1)+(x1,y1)
            // s=(3*x1^2+a)/(2*y1)
            // x3=s^2-2*x1
            // y3=s*(x1-x3)-y1
            var num = this.x.pow(BigInteger.TWO).multiply(new FieldElement(3,prime)).add(this.a);
            var den = this.y.multiply(new FieldElement(2,prime));
            var div = (num.divide(den));
            // slope
            var s = num.divide(this.y.multiply(new FieldElement(2,prime)));
            var x3 = s.pow(BigInteger.TWO).subtract(this.x.multiply(new FieldElement(2,prime)));
            var y3 = s.multiply(this.x.subtract(x3)).subtract(this.y);
            return new FieldElementPoint(x3,y3,this.a,this.b);

        }

        System.out.println("Unhandled point addition case!");
        System.exit(-1);
        return null;

    }

    // TODO: check binary expansion optimisation page 57
    // TODO: move to other implementation by default
    public FieldElementPoint multiply(BigInteger factor) {
        BigInteger n;
        FieldElementPoint sum = new FieldElementPoint(this);
        for (n = BigInteger.ONE; n.compareTo(factor)<0;n=n.add(BigInteger.ONE)) {
            sum = sum.add(this);
        }
        return sum;
    }

    // TODO: check for G point optimisation page 61
    public FieldElementPoint multiply_bin(BigInteger coefficient) {

        var result = new FieldElementPoint(null,null,this.a,this.b);
        var current = this;
        var coef = coefficient;

        while (coef.compareTo(BigInteger.ZERO)!=0) {
            if (coef.testBit(0)) {
                result = result.add(current);
            }
            current = current.add(current);
            coef = coef.shiftRight(1);
        }
        return result;
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
        FieldElementPoint point = (FieldElementPoint) o;
        return Objects.equals(x, point.x) && Objects.equals(y, point.y) && Objects.equals(a, point.a) && Objects.equals(b, point.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, a, b);
    }

    public FieldElement getX() {
        return x;
    }

    public FieldElement getY() {
        return y;
    }
}