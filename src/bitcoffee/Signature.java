package bitcoffee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Signature {
    final BigInteger r;
    final BigInteger s;

    public Signature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    @Override
    public String toString() {
        return "Signature(" + r.toString(16) + "," + s.toString(16) + ')';
    }

    public static Signature parse(byte[] der_bytes) {
        Signature sig = null;
        var bis = new ByteArrayInputStream(der_bytes);

        BigInteger s = null,r = null;

        var compound = (byte)bis.read();
        if (compound!=0x30) {
            System.out.println("bad signature");
            assert false;
        }

        var length = (byte)bis.read();

        if (length+2!= der_bytes.length) {
            System.out.println("bad signature length");
            assert false;
        }

        var marker = (byte)bis.read();
        if (marker!=0x02) {
            System.out.println("bad signature ");
            assert false;
        }

        var rlength = bis.read();
        try {
            var r_bytes = Kit.to32bytes(bis.readNBytes(rlength));
            r = new BigInteger(r_bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ////////////////////////////////////////////////////////7

        marker = (byte)bis.read();
        if (marker!=0x02) {
            System.out.println("bad signature ");
            assert false;
        }
        var slength = bis.read();
        try {
            var s_bytes = Kit.to32bytes(bis.readNBytes(slength));
            s = new BigInteger(s_bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (der_bytes.length != 6 + rlength + slength) {
            System.out.println("bitcoffee.Signature too long");
            assert false;
        }

        return new Signature(r,s);
    }

    public String DER() {
        // TODO: fix with CrytoKit
        // this will used as a string, for formatting der
        var r_string = this.r.toString(16);
        // this will used as bytes, to make comparisons etc..
        var r_bytes = this.r.toByteArray();

        // if the string lengtht is odd, it means it lacks a leading 0
        if (r_string.length()%2!=0)
            r_string = "0"+r_string;

        // the first byte starts with a 1, negative number, prepend 00
        //if (r_bytes[0]>=0x80)
        if (r_bytes[0]==0)
            r_string = "00"+r_string;

        // one byte is represented by two chars
        var r_len = Integer.toHexString(r_string.length()/2);
        if (r_len.length()%2!=0)
            r_len = "0"+r_len;

        var result = "02"+r_len+r_string;

        // repeat the same for s
        var s_bytes = this.s.toByteArray();
        var s_string = this.s.toString(16);

        if (s_string.length()%2!=0)
            s_string = "0"+s_string;
        //if (s_bytes[0]>=0x80)
        if (s_bytes[0]==0)
            s_string = "00"+s_string;

        var s_len = Integer.toHexString(s_string.length()/2);
        if (s_len.length()%2!=0)
            s_len = "0"+s_len;

        result = result+"02"+s_len+s_string;

        var res_len = Integer.toHexString(result.length()/2);
        if (res_len.length()%2!=0)
            res_len = "0"+res_len;

        return "30"+res_len+result;
    }
}