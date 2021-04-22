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
    }
}
