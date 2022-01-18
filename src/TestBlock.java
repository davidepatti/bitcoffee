import java.util.Arrays;
import java.util.Objects;

public class TestBlock {

    public static void main(String[] args) {

        Test.__BEGIN_TEST("Block");

        var block_raw = "020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d";
        //parsed
        var block = Block.parseSerial(Kit.hexStringToByteArray(block_raw));
        var block_reserialized = Kit.bytesToHexString(Objects.requireNonNull(block).serializeHeader());

        Test.check("Parsing/Serialization","",block_raw,block_reserialized);

        var hash = block.getHashHexString();
        var target_hash = "0000000000000000007e9e4c586439b0cdbe13b1370bdd9435d76a644d047523";
        var desc = "raw:"+block_raw+"\nparsed:"+block;
        Test.check("Block Hashes",desc,target_hash,hash);
        Test.__END_TEST();

        ////////////////////////////////////////////////////////////////

        Test.__BEGIN_TEST("BIPs");
        Test.check("bip9","block hash:"+block.getHashHexString(),true,block.checkBIP9());

        // should be false
        block = Block.parseSerial(Kit.hexStringToByteArray("0400000039fa821848781f027a2e6dfabbf6bda920d9ae61b63400030000000000000000ecae536a304042e3154be0e3e9a8220e5568c3433a9ab49ac4cbb74f8df8e8b0cc2acf569fb9061806652c27"));
        Test.check("bip9","block hash:"+ Objects.requireNonNull(block).getHashHexString(),false,block.checkBIP9());

        // should be true
        block = Block.parseSerial(Kit.hexStringToByteArray("1200002028856ec5bca29cf76980d368b0a163a0bb81fc192951270100000000000000003288f32a2831833c31a25401c52093eb545d28157e200a64b21b3ae8f21c507401877b5935470118144dbfd1"));
        Test.check("bip91","block hash:"+ Objects.requireNonNull(block).getHashHexString(),true,block.checkBIP91());

        // should be false
        block = Block.parseSerial(Kit.hexStringToByteArray("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        Test.check("bip91","block hash:"+ Objects.requireNonNull(block).getHashHexString(),false,block.checkBIP91());

        block  = Block.parseSerial(Kit.hexStringToByteArray("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        Test.check("bip141","block hash:"+ Objects.requireNonNull(block).getHashHexString(),true,block.checkBIP141());

        block  = Block.parseSerial(Kit.hexStringToByteArray("0000002066f09203c1cf5ef1531f24ed21b1915ae9abeb691f0d2e0100000000000000003de0976428ce56125351bae62c5b8b8c79d8297c702ea05d60feabb4ed188b59c36fa759e93c0118b74b2618"));
        Test.check("bip141","block hash:"+ Objects.requireNonNull(block).getHashHexString(),false,block.checkBIP141());

        Test.__END_TEST();

        ///////////////////////////////////////////////////////////////////
        Test.__BEGIN_TEST("Proof-of-Work");

        block = Block.parseSerial(Kit.hexStringToByteArray("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        var target = "13ce9000000000000000000000000000000000000000000";
        assert block != null;
        var computed_target = block.getTarget().toString(16);
        Test.check("bitsToTarget","block:"+block.getSerialHeaderString(),target,computed_target);


        var difficulty = block.difficulty();
        System.out.println("Difficulty: "+difficulty);
        System.out.println("Test difficulty: "+(difficulty==888171856257.0));

        block = Block.parseSerial(Kit.hexStringToByteArray("04000000fbedbbf0cfdaf278c094f187f2eb987c86a199da22bbb20400000000000000007b7697b29129648fa08b4bcd13c9d5e60abb973a1efac9c8d573c71c807c56c3d6213557faa80518c3737ec1'"));
        System.out.println(">> Checking PoW for block hash id:"+ Objects.requireNonNull(block).getHashHexString());
        System.out.println("Difficulty:"+block.difficulty());
        System.out.println("Target: "+block.getTargetHexString());
        System.out.println("Check PoW:"+block.checkPoW());


        block = Block.parseSerial(Kit.hexStringToByteArray("04000000fbedbbf0cfdaf278c094f187f2eb987c86a199da22bbb20400000000000000007b7697b29129648fa08b4bcd13c9d5e60abb973a1efac9c8d573c71c807c56c3d6213557faa80518c3737ec0"));
        System.out.println(">> Checking PoW for block hash id:"+ Objects.requireNonNull(block).getHashHexString());
        System.out.println("Difficulty:"+block.difficulty());
        System.out.println("Target: "+block.getTargetHexString());
        System.out.println("Check PoW:"+block.checkPoW());
        ///////////////////////////////////////////////////////////////////////////

        System.out.println(">> Testing new bits");
        var prev_bits = Kit.hexStringToByteArray("54d80118");
        var time_diff = 302400;
        var target_bits = Kit.hexStringToByteArray("00157617");
        var new_bits = Block.computeNewBits(prev_bits,time_diff);
        System.out.println("Result:"+ Arrays.equals(target_bits,new_bits));

        Test.__END_TEST();

        Test.__BEGIN_TEST("Difficulty adjustment");

        var last_block = Block.parseSerial(Kit.hexStringToByteArray("000000203471101bbda3fe307664b3283a9ef0e97d9a38a7eacd8800000000000000000010c8aba8479bbaa5e0848152fd3c2289ca50e1c3e58c9a4faaafbdf5803c5448ddb845597e8b0118e43a81d3"));
        var first_block = Block.parseSerial(Kit.hexStringToByteArray("02000020f1472d9db4b563c35f97c428ac903f23b7fc055d1cfc26000000000000000000b3f449fcbe1bc4cfbcb8283a0d2c037f961a3fdf2b8bedc144973735eea707e1264258597e8b0118e5f00474"));

        System.out.println("First block:");
        System.out.println(first_block);
        System.out.println("Last block:");
        System.out.println(last_block);
        time_diff = Objects.requireNonNull(last_block).getTimestamp()- Objects.requireNonNull(first_block).getTimestamp();
        System.out.println("Time differential: "+time_diff+", updating bits: "+Kit.bytesToHexString(first_block.getBits()));

        new_bits = Block.computeNewBits(first_block.getBits(),time_diff);
        Test.check("ComputeNewBits","","80df6217",Kit.bytesToHexString(new_bits));
        Test.__END_TEST();

    }
}
