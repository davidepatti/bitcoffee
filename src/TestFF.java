import java.math.BigInteger;

public class TestFF {
    // Finite Field tests

    public static void main(String[] args) {
        var n1 = new FieldElement(5,19);
        var n2 = new FieldElement(3,19);
        var res_n = n1.multiply(n2);
        System.out.println("Testing Finite Field multiplication:");
        System.out.print(n1+" * " +n2+ "="+res_n);
        System.out.println("--> "+res_n.equals(new FieldElement(15,19)));


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

        System.out.println("TEST: Point addition in prime "+prime);
        var x1 = new FieldElement(192,prime);
        var y1 = new FieldElement(105,prime);
        var x2 = new FieldElement(17,prime);
        var y2 = new FieldElement(56,prime);
        var p1 = new FieldElementPoint(x1,y1,a,b);
        var p2 = new FieldElementPoint(x2,y2,a,b);
        FieldElementPoint result = p1.add(p2);
        FieldElementPoint target = new FieldElementPoint(new FieldElement(170,prime), new FieldElement(142,prime), a,b);
        System.out.println(p1.getCoordString()+"+"+p2.getCoordString()+" = "+result.getCoordString()+" "+result.equals(target));

        // scalar mult test ////////////////////////////////////////
        a = new FieldElement(0,prime);
        b = new FieldElement(7,prime);
        x = new FieldElement(47,prime);
        y = new FieldElement(71,prime);
        FieldElementPoint point = new FieldElementPoint(x,y,a,b);

        System.out.println("point+point"+point.add(point));



        System.out.println("TEST: Scalar multitplication in prime "+prime);
        for (int s=1;s<22;s++) {
            //var temp = point.multiply(BigInteger.valueOf(s));
            var temp2 = point.multiplyBin(BigInteger.valueOf(s));
            //System.out.println("(47,71)*"+s+" = "+ temp.getCoordString());
            System.out.println("(47,71)*"+s+" = "+ temp2.getCoordString());
        }

    }

}
