import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {
	// write your code here

        // test mult  /////////////////////////////////
        var a = new FieldElement(BigInteger.valueOf(3),BigInteger.valueOf(13));
        var b = new FieldElement(BigInteger.valueOf(12),BigInteger.valueOf(13));
        var c = new FieldElement(BigInteger.valueOf(10),BigInteger.valueOf(13));

        var res = a.mul(b);
        // test ok 10,13
        System.out.println(res.equals(c));
        //////////////////////////////////////////////////

        // power to p-1 -> 1
        System.out.println(a+" pow 3 = "+a.pow2(151));


        a = new FieldElement(BigInteger.TWO,BigInteger.valueOf(19));
        b = new FieldElement(BigInteger.valueOf(7),BigInteger.valueOf(19));

        // 2/7 -> 2* 7 (19-2)
        c = a.mul(b.pow2(19-2));

        System.out.println(c);

        a = new FieldElement(BigInteger.valueOf(7),BigInteger.valueOf(19));
        b = new FieldElement(BigInteger.valueOf(5),BigInteger.valueOf(19));
        System.out.println(a.mul(b.pow2(19-2)));
    }
}
