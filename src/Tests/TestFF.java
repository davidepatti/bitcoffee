package Tests;

import bitcoffee.FieldElement;
import bitcoffee.FieldElementPoint;

import java.math.BigInteger;

public class TestFF {
    // Finite Field tests

    public static void main(String[] args) {

        Test.__BEGIN_TEST("Finite Field");

        var n1 = new FieldElement(5,19);
        var n2 = new FieldElement(3,19);
        var res_n = n1.multiply(n2);
        var desc = n1+" * " +n2;
        Test.check("Finite Field multiplication:",desc,res_n,new FieldElement(15,19));

        Test.__BEGIN_TEST("some point printing");

        final long prime = 223;
        var a = new FieldElement(0,prime);
        var b = new FieldElement(7,prime);
        var x = new FieldElement(192,prime);
        var y = new FieldElement(105,prime);

        var p = new FieldElementPoint(x,y,a,b);
        System.out.println(p);

        x = new FieldElement(17,prime);
        y = new FieldElement(56,prime);
        p = new FieldElementPoint(x,y,a,b);
        System.out.println(p);

        x = new FieldElement(1,prime);
        y = new FieldElement(193,prime);
        p = new FieldElementPoint(x,y,a,b);
        System.out.println(p);
        Test.__END_TEST();

        Test.__BEGIN_TEST("Field Point");

        var x1 = new FieldElement(192,prime);
        var y1 = new FieldElement(105,prime);
        var x2 = new FieldElement(17,prime);
        var y2 = new FieldElement(56,prime);
        var p1 = new FieldElementPoint(x1,y1,a,b);
        var p2 = new FieldElementPoint(x2,y2,a,b);
        FieldElementPoint result = p1.add(p2);
        FieldElementPoint target = new FieldElementPoint(new FieldElement(170,prime), new FieldElement(142,prime), a,b);
        desc = p1.getCoordString()+"+"+p2.getCoordString();
        Test.check("Addition in prime "+prime,desc,result,target);
        Test.__END_TEST();

        // scalar mult test ////////////////////////////////////////
        a = new FieldElement(0,prime);
        b = new FieldElement(7,prime);
        x = new FieldElement(47,prime);
        y = new FieldElement(71,prime);
        FieldElementPoint point = new FieldElementPoint(x,y,a,b);

        Test.__BEGIN_TEST("Scalar mult");
        System.out.println("TEST: Scalar multitplication in prime "+prime);
        for (int s=1;s<22;s++) {
            var temp2 = point.multiplyBin(BigInteger.valueOf(s));
            System.out.println("(47,71)*"+s+" = "+ temp2.getCoordString());
        }
        Test.__END_TEST();

    }

}
