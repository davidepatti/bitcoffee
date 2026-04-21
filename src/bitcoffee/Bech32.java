package bitcoffee;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bech32 {

    public static final String BECH32_ALPHABET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    private static final byte[] CHARSET_REV = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            15, -1, 10, 17, 21, 20, 26, 30,  7,  5, -1, -1, -1, -1, -1, -1,
            -1, 29, -1, 24, 13, 25,  9,  8, 23, -1, 18, 22, 31, 27, 19, -1,
            1,  0,  3, 16, 11, 28, 12, 14,  6,  4,  2, -1, -1, -1, -1, -1,
            -1, 29, -1, 24, 13, 25,  9,  8, 23, -1, 18, 22, 31, 27, 19, -1,
            1,  0,  3, 16, 11, 28, 12, 14,  6,  4,  2, -1, -1, -1, -1, -1
    };
    public static final long[] GEN = {0x3B6A57B2L, 0x26508E6DL, 0x1EA119FAL, 0x3D4233DDL, 0x2A1462B3L};
    public static final String BECH32_CHARS_RE = "^[qpzry9x8gf2tvdw0s3jn54khce6mua7l]*$";
    public static final long BECH32M_CONSTANT = 0x2BC830A3L;

    public static final String PREFIX_MAINNET = "bc";
    public static final String PREFIX_TESTNET = "tb";
    public static final String PREFIX_REGTEST = "bcrt";
    public static final String PREFIX_SIGNET = "tb";

    public static final class DecodedSegwit {
        public final String hrp;
        public final int version;
        public final byte[] program;
        public final boolean bech32m;

        public DecodedSegwit(String hrp, int version, byte[] program, boolean bech32m) {
            this.hrp = hrp;
            this.version = version;
            this.program = program;
            this.bech32m = bech32m;
        }
    }

    public static boolean usesOnlyBech32Chars(String s) {
        Pattern pattern = Pattern.compile(BECH32_CHARS_RE);
        Matcher matcher = pattern.matcher(s.toLowerCase());
        return matcher.find();
    }
    ///from: https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki
    public static long bech32_polymod(final byte[] values) {
        long c = 1;
        for (byte v:values ) {
            long c0 = c >> 25;
            c = ((c & 0x1ffffffL) << 5) ^ (v & 0xffL);

            for (int i = 0;i< GEN.length;i++) {
                if ( ((c0>>i) & 1)!=0 ) c ^= GEN[i];
            }
        }
        return c;
    }
    /** Expand a HRP for use in checksum computation. */

    public static byte[] bech32_hpr_expand(final String hrp) {

        var ret = new byte[hrp.length()*2+1];

        for (int i = 0;i < hrp.length(); ++i) {
            char c = hrp.charAt(i);
            ret[i] = (byte)(c >> 5);
            ret[i+hrp.length()+1] = (byte)(c & 0x1f);
        }
        ret[hrp.length()] = 0;
        return ret;
    }

    public static boolean bech32_verify_checksum(String hrp, byte[] values, boolean bech32m) {
        var hpr_expand =  bech32_hpr_expand(hrp);

        var concat = Kit.concatBytes(hpr_expand,values);

        var check = bech32_polymod(concat);

        if (bech32m)
            return check==BECH32M_CONSTANT;
        else
            return check==1;
    }


    public static byte[] bech32_create_checksum(final String hrp, byte[] values, boolean bech32m) {

        var hpr_expand = bech32_hpr_expand(hrp);
        var concat = Kit.concatBytes(hpr_expand,values);

        concat = Kit.concatBytes(concat,new byte[] {0,0,0,0,0,0});

        long mod;
        if (bech32m) mod = bech32_polymod(concat) ^ BECH32M_CONSTANT;
        else
                mod = bech32_polymod(concat) ^ 1;

        var ret = new byte[6];

        for (int i=0;i<6;++i) {
            ret[i] = (byte)((mod >> (5*(5-i))) & 31);
        }

        return ret;
    }

    public static String bc32_encode(final String hrp, final byte[] values,boolean bech32m) {

        if (!hrp.toLowerCase().equals(hrp)) throw new RuntimeException("Wrong hrp, only lower case allowed");

        var data_checksum  = bech32_create_checksum(hrp,values,bech32m);

        var combined= Kit.concatBytes(values,data_checksum);
        String ret = hrp+"1";

        for (byte c : combined) {
            ret+=BECH32_ALPHABET.charAt(c);
        }
        return ret;
    }

    public static byte[] bc32_decode(String str, boolean bech32m) {

        if (!str.toLowerCase().equals(str) && (!str.toUpperCase().equals(str))) return null;

        str = str.toLowerCase();
        var pos = str.lastIndexOf("1");

        if (str.length()>90 || pos==-1 || pos ==0 || pos+7>str.length()) return null;

        var values = new byte[str.length()-1-pos];

        for (int i=0;i<str.length()-1-pos;++i) {
            char c = str.charAt(i+pos+1);

            if (c >= CHARSET_REV.length) return null;
            byte rev = CHARSET_REV[c];
            if (rev==-1) return null;
            values[i] = rev;
        }

        String hrp = str.substring(0,pos).toLowerCase();

        boolean result = bech32_verify_checksum(hrp,values, bech32m);

        if (!result) return null;

        return Arrays.copyOfRange(values,0,values.length-6);
    }

    private static byte[] convertBits(byte[] data, int fromBits, int toBits, boolean pad) {
        int acc = 0;
        int bits = 0;
        int maxv = (1 << toBits) - 1;
        int maxAcc = (1 << (fromBits + toBits - 1)) - 1;
        var bos = new ByteArrayOutputStream();

        for (byte valueByte : data) {
            int value = valueByte & 0xff;
            if ((value >> fromBits) != 0) {
                throw new IllegalArgumentException("Invalid Bech32 data value: " + value);
            }
            acc = ((acc << fromBits) | value) & maxAcc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                bos.write((acc >> bits) & maxv);
            }
        }

        if (pad) {
            if (bits > 0) {
                bos.write((acc << (toBits - bits)) & maxv);
            }
        }
        else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new IllegalArgumentException("Invalid Bech32 padding");
        }

        return bos.toByteArray();
    }

     //   """Convert from 5-bit array of integers to bech32 format"""
    public static String encode_bech32(ArrayList<Integer> nums) {
        String result = "";

        for (int n:nums) {
            if (n < 0 || n >= BECH32_ALPHABET.length()) {
                throw new IllegalArgumentException("Invalid Bech32 character index: " + n);
            }
            result = result+BECH32_ALPHABET.charAt(n);
        }

        return result;
    }

    //Convert a segwit ScriptPubKey to a bech32 address
    public static String encode_bech32_checksum(byte[] script, boolean testnet,boolean bech32m) {
        String prefix;
        if (testnet) prefix = PREFIX_TESTNET;
        else prefix = PREFIX_MAINNET;

        var version = script[0];

        if (version >0) version-=0x50;

        var length = script[1] & 0xff;


        var data = new ArrayList<Integer>();

        data.add((int)version);
        var v2 = convertBits(Arrays.copyOfRange(script,2,2+length),8,5,true);
        for (byte b : v2) {
            data.add(b & 0xff);
        }

        var data_bytes = new byte[data.size()];
        for (int i=0;i<data.size();i++)
            data_bytes[i] = data.get(i).byteValue();

        var checksum = bech32_create_checksum(prefix, data_bytes,bech32m);

        for (byte b:checksum)
            data.add((int)b);

        return prefix+"1"+encode_bech32(data);


    }

    public static DecodedSegwit decodeSegwitAddress(String address) {
        var normalized = address.toLowerCase(Locale.ROOT);
        var separator = normalized.lastIndexOf('1');

        if (separator <= 0) {
            throw new IllegalArgumentException("Invalid Bech32 address: " + address);
        }

        var hrp = normalized.substring(0, separator);
        if (!hrp.equals(PREFIX_MAINNET) && !hrp.equals(PREFIX_TESTNET) && !hrp.equals(PREFIX_REGTEST)) {
            throw new IllegalArgumentException("Unsupported Bech32 human-readable part: " + hrp);
        }

        byte[] data = bc32_decode(normalized, false);
        boolean bech32m = false;
        if (data == null) {
            data = bc32_decode(normalized, true);
            bech32m = true;
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Invalid Bech32 checksum: " + address);
        }

        int version = data[0] & 0xff;
        if (version > 16) {
            throw new IllegalArgumentException("Unsupported segwit version: " + version);
        }
        if (version == 0 && bech32m) {
            throw new IllegalArgumentException("Segwit v0 addresses must use Bech32");
        }
        if (version != 0 && !bech32m) {
            throw new IllegalArgumentException("Segwit v1+ addresses must use Bech32m");
        }

        var program = convertBits(Arrays.copyOfRange(data, 1, data.length), 5, 8, false);
        if (program.length < 2 || program.length > 40) {
            throw new IllegalArgumentException("Invalid witness program length: " + program.length);
        }
        if (version == 0 && program.length != 20 && program.length != 32) {
            throw new IllegalArgumentException("Segwit v0 witness program must be 20 or 32 bytes");
        }

        return new DecodedSegwit(hrp, version, program, bech32m);
    }
}
