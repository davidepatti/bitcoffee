import java.math.BigInteger;
import java.util.ArrayList;

public class TestBloomFilter {

    public static void main(String[] args) {

        var items = new ArrayList<String>();
        items.add("hello world");
        items.add("goodbye");

        int field_size = 10;
        var bit_field = MerkleBlock.bitStringToBitField("0000000000");

        for (String s:items)  {
            //var h = Kit.hash160(Kit.asciiStringToBytes(s));
            var h = Kit.hash256(s);
            var n = new BigInteger(1,h);
            int bit = n.mod(BigInteger.valueOf(field_size)).intValue();
            bit_field.set(bit,true);
        }


        field_size = 2;
        int num_functions = 2;
        int tweak = 42;
        int bit_field_size = field_size * 8;

        bit_field = new ArrayList<Boolean>();
        for (int i=0;i<bit_field_size;i++) {
            bit_field.add(false);
        }

        for (String phrase:items) {
            for (int i=0;i<num_functions;i++) {
                var c = new BigInteger(1,Kit.hexStringToByteArray(BloomFilter.BIP37_CONSTANT)).longValue();
                var seed = i*c+tweak;
                var data = Kit.asciiStringToBytes(phrase);
                var h = BigInteger.valueOf(Murmur3.hash_x86_32(data,data.length,seed));
                int bit = h.mod(BigInteger.valueOf(bit_field_size)).intValue();
                bit_field.set(bit,true);
            }
        }

        System.out.println(bit_field);
    }
}
