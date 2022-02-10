package bitcoffee;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class BloomFilter {
    final static String BIP37_CONSTANT_STRING = "fba4c795";
    public final static long BIP37_CONSTANT = new BigInteger(1, Kit.hexStringToByteArray(BIP37_CONSTANT_STRING)).longValue();

    private final int size;
    private final int function_count;
    private final int tweak;
    private final ArrayList<Boolean> bit_field;

    public BloomFilter(int size, int function_count, int tweak) {

        bit_field = new ArrayList<>();

        this.function_count = function_count;
        this.size = size;
        this.tweak = tweak;

        for (int i=0;i<size*8;i++)
            bit_field.add(false);
    }

    public void add(String item) {
        var item_bytes = Kit.asciiStringToBytes(item);
        this.add(item_bytes);
    }

    public void add(byte[] item) {

        for (int i=0;i<function_count;i++) {
            long seed = i*BIP37_CONSTANT+tweak;
            var h = Murmur3.hash_x86_32(item,item.length,seed);
            int bit = h.mod(BigInteger.valueOf(this.size*8)).intValue();
            bit_field.set(bit,true);
        }
    }

    public Message filterLoad() {
        var bos = new ByteArrayOutputStream();
        try {
            bos.write(Kit.encodeVarint(this.size));
            bos.write(Kit.bitFieldToBytes(this.bit_field));
            bos.write(Kit.intToLittleEndianBytes(this.function_count),0,4);
            bos.write(Kit.intToLittleEndianBytes(this.tweak),0,4);
            bos.write(Kit.intToLittleEndianBytes(1),0,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new MessageGeneric("filterload",bos.toByteArray());

    }

    public String getBitField() {
        return Kit.bytesToHexString(Kit.bitFieldToBytes(bit_field));
    }
}
