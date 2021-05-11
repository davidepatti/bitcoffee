import java.math.BigInteger;

public class TestSecp256k1 {

    public static void main(String args[]) {
        test_infinity();
        test_manual_signature();
        test_message_signature();
        test_verify_signature();
        test_signing();

    }

    public static void test_signing() {
        var e_bytes = Secp256k1.hash256("secret");
        var e_num = new BigInteger(1,e_bytes);
        var z_bytes = Secp256k1.hash256("Programming Bitcoin!");
        var z_num = new BigInteger(1,z_bytes);

        System.out.println("Signing message: Programming Bitcoin! with string secret");
        System.out.println("secret = "+e_num.toString(16));
        System.out.println("message = "+z_num.toString(16));
        var prefixed_k = BigInteger.valueOf(1234567890);
        var pk = new PrivateKey(e_bytes);
        var signature_prefixed = pk.sign(z_bytes,prefixed_k);
        System.out.println("signature prefixed k ="+signature_prefixed);
        var sig_detk = pk.deterministic_k(z_bytes);
        var k = sig_detk;
        System.out.println("deterministic k = "+sig_detk.toString(16));
        var target_k = new BigInteger("e32a28db452c56f30dc5019d7989e20efcd991cc5edb5ffc3063e83f9f055f8e",16);
        System.out.println("--> Test deterministic k: "+target_k.equals(target_k));

    }
    public static void test_infinity() {
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
    }

    public static void test_manual_signature() {
        // testing manual signature
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
        System.out.println("-->"+ Secp256k1.G.multiply_bin(u).add(point.multiply_bin(v)).getX().getNum().equals(r));

    }

    public static void test_message_signature() {
        /****************************************************************/
        //  testing message signature (page 69)
        String secret = "my secret";
        String message = "my message";

        var e_bytes = Secp256k1.hash256(secret);
        var e_num = new BigInteger(1,e_bytes);

        var z_bytes = Secp256k1.hash256(message);
        var z_num = new BigInteger(1,z_bytes);
        var k = BigInteger.valueOf(1234567890);
        var r = Secp256k1.G.multiply_bin(k).getX().getNum();
        var k_inv = k.modPow(Secp256k1.N.subtract(BigInteger.TWO),Secp256k1.N);
        var s = ((z_num.add(r.multiply(e_num))).multiply(k_inv)).mod(Secp256k1.N);
        var point2 = new S256Point(Secp256k1.G.multiply_bin(e_num));

        var x_target = new BigInteger("28d003eab2e428d11983f3e97c3fa0addf3b42740df0d211795ffb3be2f6c52",16);
        var y_target = new BigInteger("ae987b9ec6ea159c78cb2a937ed89096fb218d9e7594f02b547526d8cd309e2",16);
        var t_point = new S256Point(x_target,y_target);
        System.out.println("--> Testing message signature: "+point2.equals(t_point));
        System.out.println("z="+z_num.toString(16));
        System.out.println("r="+r.toString(16));
        System.out.println("s="+s.toString(16));
        System.out.println("e="+e_num.toString(16));
        System.out.println("e*G="+point2);
    }

    public static void test_verify_signature() {
        // Exercise 6 page 67
        var p_x = new BigInteger("887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c",16);
        var p_y = new BigInteger("61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34",16);
        var P = new S256Point(p_x,p_y);
        var z = new BigInteger("ec208baa0fc1c19f708a9ca96fdeff3ac3f230bb4a7ba4aede4942ad003c0f60",16);
        var r = new BigInteger("ac8d1c87e51d0d441be8b3dd5b05c8795b48875dffe00b7ffcfac23010d3a395",16);
        var s = new BigInteger("68342ceff8935ededd102dd876ffd6ba72d6a427a3edb13d26eb0781cb423c4",16);

        var sig = new Signature(r,s);
        System.out.println("--> Test Verify signature: "+P.verify(z,sig));
    }

}
