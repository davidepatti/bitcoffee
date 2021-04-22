import java.math.BigInteger;

public class Test {

    public static void main(String[] args) {
	// write your code here

        // test equality
        var a = new FieldElement(3,13);
        var b = new FieldElement(3,13);
        System.out.println("Equality: "+a.equals(b));


        // test mult  /////////////////////////////////
        a = new FieldElement(3,13);
        b = new FieldElement(12,13);
        var target = new FieldElement(10,13);
        var res = a.mul(b);
        System.out.println(a+" * "+b+" = "+target+" mult:"+ res.equals(target));
        //////////////////////////////////////////////////

        // test pow p-1
        int prime = 13;
        res = a.pow(prime-1);
        target = new FieldElement(1,prime);
        System.out.println(a+" pow("+prime+"-1) = "+res+ " "+res.equals(target));


        // test negative exp
        a = new FieldElement(7,13);
        target = new FieldElement(8,13);
        int exp = -3;
        res = a.pow(exp);
        System.out.println(a+" pow("+exp+") = "+res+" "+res.equals(target));


        // test division
        a = new FieldElement(2,19);
        b = new FieldElement(7,19);
        // 2/7 -> 2* 7 (19-2)
        res = a.div(b);
        target = new FieldElement(3,19);
        System.out.println(a+" div "+b+" = "+res+ " "+res.equals(target));

        a = new FieldElement(7,19);
        b = new FieldElement(5,19);
        target = new FieldElement(9,19);
        res = a.div(b);
        System.out.println(a+" div "+b+" = "+res+ " "+res.equals(target));


        ///////////// Elliptic curve

        // infinity point is also identity point, result should not change
        var p1 = new Point(-1,-1,5,7);
        var inf = new Point(Point.BIGINF,Point.BIGINF, BigInteger.valueOf(5),BigInteger.valueOf(7));
        var point_target = p1;
        var point_result = p1.add(inf);
        System.out.println(p1+" + Inf = "+point_result+" "+point_result.equals(point_target));

        var p2 = new Point(-1,1,5,7);
        point_result = p2.add(inf);
        point_target = p2;
        System.out.println(p2+" + Inf = "+point_result+" "+point_result.equals(point_target));

        point_result = p1.add(p2);
        point_target = inf;
        System.out.println(p1+"+"+p2+" = "+point_result+" "+point_result.equals(point_target));


        p1 = new Point(3,7,5,7);
        p2 = new Point(-1,-1,5,7);
        point_target = new Point(2,-5,5,7);
        point_result = p1.add(p2);
        System.out.println(p1+" + "+p2+" = "+point_result+ " "+point_result.equals(point_target));

        point_result = p2.add(p2);
        point_target = new Point(18,77,5,7);
        System.out.println(p2+" + "+p2+" = "+point_result+ " "+point_result.equals(point_target));


    }
}
