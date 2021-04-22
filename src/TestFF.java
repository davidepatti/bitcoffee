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

        x = new FieldElement(200,prime);
        y = new FieldElement(119,prime);
        p = new FEPoint(x,y,a,b);
        System.out.println(p);

    }

}
