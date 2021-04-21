public class Main {

    public static void main(String[] args) {
	// write your code here

        // test mult  /////////////////////////////////
        var a = new FieldElement(3,13);
        var b = new FieldElement(12,13);
        var c = new FieldElement(10,13);

        var res = a.mul(b);
        // test ok 10,13
        System.out.println(res.equals(c));
        //////////////////////////////////////////////////

        // ok if result 1,13
        System.out.println(a+" pow 3 = "+a.pow(4));

        // power to p-1 -> 1
        System.out.println(a+" pow 3 = "+a.pow2(101));



    }
}
