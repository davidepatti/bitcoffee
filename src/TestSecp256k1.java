import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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


        // test 2: using static class member
        var G3 = Secp256k1.G;
        var res = G3.multiply_bin(Secp256k1.N);
        System.out.println(res);

        // test 3: using specialized subclass
        var G4 = new S256Point(Secp256k1.Gx,Secp256k1.Gy);
        System.out.println(G4.multiply_bin(Secp256k1.N));


        // testing manually a signature
        var z = new BigInteger("bc62d4b80d9e36da29c16c5d4d9f11731f36052c72401a76c23c0fb5a9b74423",16);
        var r = new BigInteger("37206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c6",16);
        var s = new BigInteger("8ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec",16);
        var px = new BigInteger("04519fac3d910ca7e7138f7013706f619fa8f033e6ec6e09370ea38cee6a7574",16);
        var py = new BigInteger("82b51eab8c27c66e26c858a079bcdf4f1ada34cec420cafc7eac1a42216fb6c4",16);

        var point = new S256Point(px,py);

        var s_inv = s.modPow(Secp256k1.N.subtract(BigInteger.TWO), Secp256k1.N);
        var u = z.multiply(s_inv.mod(Secp256k1.N));
        var v = r.multiply(s_inv.mod(Secp256k1.N));
        // u*G+v*point == r
        System.out.print("Testing manual signature: ");
        System.out.println(Secp256k1.G.multiply_bin(u).add(point.multiply_bin(v)).getX().getNum().equals(r));

        // signing a message

        String message = "my secret";

        System.out.println(Secp256k1.sha256(message));


    }

}
