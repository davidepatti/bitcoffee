package Tests;

import Tests.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import bitcoffee.*;

@SuppressWarnings("CommentedOutCode")
public class TestBloomFilter {

    public static void main(String[] args) {

        Test.__BEGIN_TEST("Bloom filter");
        var items = new ArrayList<String>();
        items.add("Hello World");
        items.add("Goodbye!");

        int field_size = 10;
        var bit_field = Kit.bitStringToBitField("0000000000");

        for (String s:items)  {
            //var h = bitcoffee.Kit.hash160(bitcoffee.Kit.asciiStringToBytes(s));
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
        Test.check("filterload()","", Kit.bytesToHexString(bloom.filterLoad().getPayload()),"0a4000600a080000010940050000006300000001");


        var hex_msg = "020300000030eb2540c41025690160a1014c577061596e32e426b712c7ca00000000000000030000001049847939585b0652fba793661c361223446b6fc41089b8be00000000000000";
        var getdata = new MessageGetData();

        var block1 = "00000000000000cac712b726e4326e596170574c01a16001692510c44025eb30";
        getdata.addData(MessageGetData.FILTERED_BLOCK_DATA_TYPE,block1);

        var block2 = "00000000000000beb88910c46f6b442312361c6693a7fb52065b583979844910";
        getdata.addData(MessageGetData.FILTERED_BLOCK_DATA_TYPE,block2);

        Test.check("messagegetdata serialize","",hex_msg, Kit.bytesToHexString(getdata.getPayload()));

        //////////////////////////////////////////////////////////////////////////////////////////////



        // example for tesnet
        var last_block_hex = "00000000000538d5c2246336644f9a4956551afb44ba47278759ec55ea912e19";
        var address = "mwJn1YPMq7y5F8J3LkC5Hxg9PHyZ5K4cFv";
        var host = "testnet.programmingbitcoin.com";
        var testnet = true;

        // example for mainnet
        //var last_block_hex = "0000000000000000000838497f627c016c2bb9097d6794c6aeac1a581bd26984";
        //var address = "3Ffi6K7abWQsVMXUQuUNGviNAghXrY9Bni";
        //var host = "mainnet.programmingbitcoin.com";
        //var testnet = false;


        var h160 = Kit.decodeBase58(address);

        var node = new SimpleNode(host,testnet);
        var bf = new BloomFilter(30,5,90210);
        bf.add(h160);

        node.Handshake();
        node.send(bf.filterLoad());

        // ask for the block headers starting from the last block specified
        var getheaders_msg = new MessageGetHeaders(last_block_hex);
        node.send(getheaders_msg);

        var headers_msg = (MessageHeaders)node.waitFor(MessageHeaders.COMMAND);
        var getdata_msg = new MessageGetData();

        for (Block b: headers_msg.getBlocks()) {
            if (!b.checkPoW()) {
                throw new RuntimeException("Not valid PoW");
            }
            getdata_msg.addData(MessageGetData.FILTERED_BLOCK_DATA_TYPE,b.getHashHexString());
        }

        node.send(getdata_msg);

        boolean found = false;

        var msg_to_wait = new HashSet<String>();
        msg_to_wait.add(MerkleBlock.COMMAND);
        msg_to_wait.add(Tx.COMMAND);

        while (!found) {

            var msg = node.waitFor(msg_to_wait);

            if (msg.getCommand().equals("merkleblock")) {

                if (!((MerkleBlock) msg).isValid())
                    throw new RuntimeException("Not valid Merkle proof");
                else System.out.println("Received valid Merkle block");
            }

            else {
                var receveived_tx = (Tx)msg;
                for (TxOut tout: receveived_tx.getTxOuts()) {
                    if (tout.getScriptPubKey().getAddress(true).equals(address)) {
                        System.out.println("Found address "+address+" in tx id: "+receveived_tx.getId());
                        found = true;
                    }

                }
            }
        } // while
    }
}
