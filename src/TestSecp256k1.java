import java.math.BigInteger;

public class TestSecp256k1 {
    public static void main(String args[]) {

        // testing sec256k1

        System.out.println("Tesing sec256k1, you should see three points at infinity (null,null)");
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

        /****************************************************************/
        //  testing message signature (page 69)

        String secret = "my secret";
        String message = "my message";

        var e = Secp256k1.hash256(secret);

        z = Secp256k1.hash256(message);
        var k = BigInteger.valueOf(1234567890);
        r = Secp256k1.G.multiply_bin(k).getX().getNum();
        var k_inv = k.modPow(Secp256k1.N.subtract(BigInteger.TWO),Secp256k1.N);
        s = ((z.add(r.multiply(e))).multiply(k_inv)).mod(Secp256k1.N);
        var point2 = new S256Point(Secp256k1.G.multiply_bin(e));

        var x_target = new BigInteger("28d003eab2e428d11983f3e97c3fa0addf3b42740df0d211795ffb3be2f6c52",16);
        var y_target = new BigInteger("ae987b9ec6ea159c78cb2a937ed89096fb218d9e7594f02b547526d8cd309e2",16);
        var t_point = new S256Point(x_target,y_target);
        System.out.println("Testing message signature: "+point2.equals(t_point));
        System.out.println("z="+z.toString(16));
        System.out.println("r="+r.toString(16));
        System.out.println("s="+s.toString(16));
        System.out.println("e="+e.toString(16));
        System.out.println("e*G="+point2);

        // Exercise 6

        var p_x = new BigInteger("887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c",16);
        var p_y = new BigInteger("61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34",16);
        var P = new S256Point(p_x,p_y);
        z = new BigInteger("ec208baa0fc1c19f708a9ca96fdeff3ac3f230bb4a7ba4aede4942ad003c0f60",16);
        r = new BigInteger("ac8d1c87e51d0d441be8b3dd5b05c8795b48875dffe00b7ffcfac23010d3a395",16);
        s = new BigInteger("68342ceff8935ededd102dd876ffd6ba72d6a427a3edb13d26eb0781cb423c4",16);


        // test exercise 7
        var sig = new Signature(r,s);
        System.out.println("Verify signature: "+P.verify(z,sig));

        System.out.println("signing message: Programming Bitcoin! with e = 12345");
        z = Secp256k1.hash256("Programming Bitcoin!");
        System.out.println("message hash: "+z.toString(16));
        e = BigInteger.valueOf(12345);
        k = BigInteger.valueOf(1234567890);
        var privateKey = new PrivateKey(e);
        var signature = privateKey.sign(z,k);
        System.out.println("signature="+signature);

    }

}
