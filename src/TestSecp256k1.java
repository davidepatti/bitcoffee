import java.math.BigInteger;

public class TestSecp256k1 {
    public static void main(String args[]) {
        var x = new FieldElement(Secp256k1.Gx,Secp256k1.p);
        var y = new FieldElement(Secp256k1.Gy,Secp256k1.p);
        var a = new FieldElement(Secp256k1.a,Secp256k1.p);
        var b = new FieldElement(Secp256k1.b,Secp256k1.p);
        FieldElementPoint G = new FieldElementPoint(x,y,a,b);
        System.out.println(G.add(G));
        //System.out.println(G.multiply(BigInteger.valueOf(2)));
        //System.out.println(G.add(G));
        System.out.println(G.multiply_bin(Secp256k1.n));
    }

}
