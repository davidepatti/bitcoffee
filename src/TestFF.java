public class TestFF {
    // Finite Field tests

    public static void main(String args[]) {
        long prime = 223;
        var a = new FieldElement(0,prime);
        var b = new FieldElement(7,prime);
        var x = new FieldElement(192,prime);
        var y = new FieldElement(105,prime);

        var p = new FEPoint(x,y,a,b);
        System.out.println(p);

        x = new FieldElement(17,prime);
        y = new FieldElement(56,prime);
        p = new FEPoint(x,y,a,b);
        System.out.println(p);

        x = new FieldElement(1,prime);
        y = new FieldElement(193,prime);
        p = new FEPoint(x,y,a,b);
        System.out.println(p);

        var x1 = new FieldElement(192,prime);
        var y1 = new FieldElement(105,prime);
        var x2 = new FieldElement(17,prime);
        var y2 = new FieldElement(56,prime);
        var p1 = new FEPoint(x1,y1,a,b);
        var p2 = new FEPoint(x2,y2,a,b);
        FEPoint result = p1.add(p2);
        FEPoint target = new FEPoint(new FieldElement(170,prime), new FieldElement(142,prime), a,b);
        System.out.println(p1+"+"+p2+" = "+result+" "+result.equals(target));


        /*
        x = new FieldElement(42,prime);
        y = new FieldElement(99,prime);
        p = new FEPoint(x,y,a,b);
        System.out.println(p);

        x = new FieldElement(200,prime);
        y = new FieldElement(119,prime);
        p = new FEPoint(x,y,a,b);
        System.out.println(p);
         */

    }

}
