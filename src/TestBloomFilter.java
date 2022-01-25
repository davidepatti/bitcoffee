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
                var c = BloomFilter.BIP37_CONSTANT;
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


        var hex_msg = "020300000030eb2540c41025690160a1014c577061596e32e426b712c7ca00000000000000030000001049847939585b0652fba793661c361223446b6fc41089b8be00000000000000";
        var getdata = new MessageGetData();

        var block1 = "00000000000000cac712b726e4326e596170574c01a16001692510c44025eb30";
        getdata.addData(MessageGetData.FILTERED_BLOCK_DATA_TYPE,block1);

        var block2 = "00000000000000beb88910c46f6b442312361c6693a7fb52065b583979844910";
        getdata.addData(MessageGetData.FILTERED_BLOCK_DATA_TYPE,block2);

        Test.check("messagegetdata serialize","",hex_msg,Kit.bytesToHexString(getdata.serialize()));

        //////////////////////////////////////////////////////////////////////////////////////////////

        var last_block_hex = "00000000000538d5c2246336644f9a4956551afb44ba47278759ec55ea912e19";
        var address = "mwJn1YPMq7y5F8J3LkC5Hxg9PHyZ5K4cFv";
        var h160 = Kit.decodeBase58(address);

        var node = new SimpleNode("testnet.programmingbitcoin.com",true);
        var bf = new BloomFilter(30,5,90210);
        bf.add(Kit.bytesToHexString(h160));







    }
}
