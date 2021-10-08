import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

public class S256Point extends FieldElementPoint{
    private final BigInteger x;
    private final BigInteger y;

    /* TODO: check if can be disabled
    public S256Point(BigInteger x, BigInteger y) {
        super(new FieldElement(x,Secp256k1.p),
              new FieldElement(y,Secp256k1.p),
              new FieldElement(Secp256k1.a,Secp256k1.p),
              new FieldElement(Secp256k1.b,Secp256k1.p));
        this.x = x;
        this.y = y;
    }
     */
    public S256Point(BigInteger x, BigInteger y) {
        super(new S256Field(x),new S256Field(y), new S256Field(Secp256k1.a), new S256Field(Secp256k1.b));
        this.x = x;
        this.y = y;
    }

    public S256Point(FieldElementPoint other) {
        super(other);

        this.x = other.getX().getNum();
        this.y = other.getY().getNum();
    }

    @Override
    public String toString() {
        return "S256Point("+x.toString(16)+","+y.toString(16)+")";
    }

    public boolean verify(BigInteger z, Signature sig) {
        if (z.compareTo(BigInteger.ZERO)<0) {
            System.out.println("FATAL: negative z value in S256Point verify()");
            System.out.println("Please use unsigned conversion when converting bytes to z BigInteger");
            System.exit(-1);
        }
        var s_inv = sig.s.modPow(Secp256k1.N.subtract(BigInteger.TWO), Secp256k1.N);
        var u = z.multiply(s_inv.mod(Secp256k1.N));
        var v = sig.r.multiply(s_inv.mod(Secp256k1.N));
        var total = Secp256k1.G.multiplyBin(u).add(this.multiplyBin(v));
        return  total.getX().getNum().equals(sig.r);
    }

    public String getSerialX() {
        StringBuilder x = new StringBuilder(this.getX().getNum().toString(16));
        // put missing leading zeros to reach 32bytes hex
        while (x.length()<64) {
            x.insert(0, "0");
        }
        return x.toString();
    }
    public String getSerialY() {
        StringBuilder y = new StringBuilder(this.getY().getNum().toString(16));
        // put missing leading zeros to reach 32bytes hex
        while (y.length()<64) {
            y.insert(0, "0");
        }
        return y.toString();
    }

    public String SEC65() {
        return "04"+this.getSerialX()+this.getSerialY();
    }
    public String SEC33() {
        if (this.getY().getNum().mod(BigInteger.TWO).equals(BigInteger.ZERO))
            return "02"+this.getSerialX();
        else
            return "03"+this.getSerialX();
    }


    public static S256Point parseSEC(byte[] sec_bytes) {
        //var sec = Hex.toHexString(sec_bytes);
        var sec = CryptoKit.bytesToHexString(sec_bytes);
        return parseSEC(sec);
    }

    public static S256Point parseSEC(String sec) {
        // TODO: is it necessary to convert to bytes?
        var sec_n = new BigInteger(sec,16);
        var sec_bytes = sec_n.toByteArray();
        if (sec_bytes[0] == 4) {
            var x = sec.substring(2,66);
            var y = sec.substring(66,130);
            var x_n = new BigInteger(x,16);
            var y_n = new BigInteger(y,16);
            return new S256Point(x_n,y_n);
        }
        boolean is_even = sec_bytes[0]==2;
        var x_s = sec.substring(2,66);
        var x = new S256Field(new BigInteger(x_s,16));
        // x^3+b
        var alpha = (x.pow(BigInteger.valueOf(3)).add(new S256Field(Secp256k1.b)));
        var beta = new S256Field(alpha.getNum()).sqrt();

        S256Field even_beta;
        S256Field odd_beta;

        if (beta.getNum().mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            even_beta = beta;
            odd_beta = new S256Field(Secp256k1.p.subtract(beta.getNum()));
        }
        else {
            even_beta = new S256Field(Secp256k1.p.subtract(beta.getNum()));
            odd_beta = beta;
        }

        if (is_even)
            return new S256Point(x.getNum(),even_beta.getNum());
        else
            return new S256Point(x.getNum(),odd_beta.getNum());
    }
    
    public byte[] getHash160(boolean compressed) {
        String sec;
        if (compressed) {
            sec = this.SEC33();
        }
        else {
            sec = this.SEC65();
        }

        var sec_bytes = CryptoKit.hexStringToByteArray(sec);
        var hash = CryptoKit.hash160(sec_bytes);
        return hash;
    }

    public String getAddress(boolean compressed) {
        var h160 = this.getHash160(compressed);
        byte prefix = 0;
        var bos = new ByteArrayOutputStream();
        bos.write(prefix);
        bos.writeBytes(h160);
        var res_bytes = bos.toByteArray();
        return CryptoKit.encodeBase58Checksum(res_bytes);
    }

    public String getTestnetAddress() {
        var h160 = this.getHash160(true);
        byte prefix = 0x6f;
        var bos = new ByteArrayOutputStream();
        bos.write(prefix);
        bos.writeBytes(h160);
        var res_bytes = bos.toByteArray();
        return CryptoKit.encodeBase58Checksum(res_bytes);

    }
    
    

}