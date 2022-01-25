import java.math.BigInteger;
import java.util.ArrayList;

public class TestBloomFilter {

    public static void main(String[] args) {

        Test.__BEGIN_TEST("Bloom filter");
        var items = new ArrayList<String>();
        items.add("Hello World");
        items.add("Goodbye!");

        int field_size = 10;
        var bit_field = Kit.bitStringToBitField("0000000000");

        for (String s:items)  {
            //var h = Kit.hash160(Kit.asciiStringToBytes(s));
            var h = Kit.hash256(s);
            var n = new BigInteger(1,h);
            int bit = n.mod(BigInteger.valueOf(field_size)).intValue();
            bit_field.set(bit,true);
        }

        field_size = 10;
        int num_functions = 5;
        int tweak = 99;
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
                var h = Murmur3.hash_x86_32(data,data.length,seed);
                int bit = h.mod(BigInteger.valueOf(bit_field_size)).intValue();
                bit_field.set(bit,true);
            }
        }

        var result = Kit.bytesToHexString(Kit.bitFieldToBytes(bit_field));

        Test.check("manual bitfield","","4000600a080000010940",result);

        var bloom = new BloomFilter(10,5,99);
        bloom.add("Hello World");

        Test.check("bloomfilter add()","","0000000a080000000140",""+bloom.getBitField());

        bloom.add("Goodbye!");
        Test.check("filterload()","",Kit.bytesToHexString(bloom.filterLoad().serialize()),"0a4000600a080000010940050000006300000001");


    }
}
