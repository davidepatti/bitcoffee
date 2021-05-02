public class TestSecp256k1 {
    public static void main(String args[]) {

        // testing sec256k1

        System.out.println("Tesing sec256k1, you should see three points at infinity");
        // test 1: manually creating G with lower level classes
        var x = new FieldElement(Secp256k1.Gx,Secp256k1.p);
        var y = new FieldElement(Secp256k1.Gy,Secp256k1.p);
        var a = new FieldElement(Secp256k1.a,Secp256k1.p);
        var b = new FieldElement(Secp256k1.b,Secp256k1.p);
        FieldElementPoint G = new FieldElementPoint(x,y,a,b);
        // this should be point at infinity
        System.out.println(G.multiply_bin(Secp256k1.N));


        // test 2: creating G with specialized constructor
        var G2 = new FieldElementPoint(Secp256k1.Gx,Secp256k1.Gy);
        // this should be point at infinity
        System.out.println(G2.multiply_bin(Secp256k1.N));

        // test 3: using static class member
        var G3 = Secp256k1.G;
        var res = G3.multiply_bin(Secp256k1.N);
        System.out.println(res);

    }

}
