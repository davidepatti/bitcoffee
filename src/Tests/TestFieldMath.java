package Tests;
import bitcoffee.*;


import java.math.BigInteger;

public class TestFieldMath {

    public static void main(String[] args) {
	// write your code here

        Test.__BEGIN_TEST("FIELD ELEMENT");

        // test equality
        var a = new FieldElement(3,13);
        var b = new FieldElement(3,13);
        Test.check("contructor equality","",a,b);


        a = new FieldElement(3,13);
        b = new FieldElement(12,13);
        var target = new FieldElement(10,13);
        var res = a.multiply(b);
        String description = a+" * "+b;
        Test.check("multiplication",description,target,res);

        // test pow p-1
        var prime = BigInteger.valueOf(13);
        res = a.pow(prime.subtract(BigInteger.ONE));
        target = new FieldElement(1,prime);
        description = a+" pow("+prime+"-1)";
        Test.check("pow",description,target,res);

        // test negative exp
        a = new FieldElement(7,13);
        target = new FieldElement(8,13);
        var exp = BigInteger.valueOf(-3);
        res = a.pow(exp);
        description = a+" pow("+exp+")";
        Test.check("negative exp",description,target,res);

        // test division
        a = new FieldElement(2,19);
        b = new FieldElement(7,19);
        // 2/7 -> 2* 7 (19-2)
        res = a.divide(b);
        target = new FieldElement(3,19);
        description = a+" div "+b;
        Test.check("division",description,target,res);

        a = new FieldElement(7,19);
        b = new FieldElement(5,19);
        target = new FieldElement(9,19);
        res = a.divide(b);
        description = a+" div "+b;
        Test.check("division",description,target,res);

        Test.__END_TEST();

        Test.__BEGIN_TEST("INT POINT");

        ///////////// Elliptic curve
        // infinity point is also identity point, result should not change
        var p1 = new IntPoint(-1,-1,5,7);
        var inf = new IntPoint(IntPoint.BIGINF, IntPoint.BIGINF, BigInteger.valueOf(5),BigInteger.valueOf(7));
        var point_target = p1;
        var point_result = p1.add(inf);
        description = p1+" + Inf";
        Test.check("Inf addition",description,point_target,point_result);

        var p2 = new IntPoint(-1,1,5,7);
        point_result = p2.add(inf);
        point_target = p2;
        description = p2+" + Inf";
        Test.check("Inf addition",description,point_target,point_result);

        point_result = p1.add(p2);
        point_target = inf;
        description = p1+"+"+p2;
        Test.check("Y symmetric point addition",description,point_target,point_result);

        p1 = new IntPoint(3,7,5,7);
        p2 = new IntPoint(-1,-1,5,7);
        point_target = new IntPoint(2,-5,5,7);
        point_result = p1.add(p2);
        description = p1+" + "+p2;
        Test.check("Point addition",description,point_target,point_result);

        point_result = p2.add(p2);
        point_target = new IntPoint(18,77,5,7);
        description = p2+" + "+p2;
        Test.check("Self point addition",description,point_target,point_result);
        Test.__END_TEST();
    }
}
