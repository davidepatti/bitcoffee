import java.util.Arrays;

public class TestBlock {

    public static void main(String[] args) {
        var block_raw = "020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d";

        System.out.println("-----------------------------------------------------");
        System.out.println(">> Testing block parsin/serialization");
        System.out.println("block raw:");
        System.out.println(block_raw);

        System.out.println("Parsed:");
        var block = Block.parseSerial(Kit.hexStringToByteArray(block_raw));

        System.out.println(block);

        System.out.println("Reserialized:");

        System.out.println(Kit.bytesToHexString(block.serialize()));

        System.out.println(">> Test HASH:");
        var hash = block.getHashHexString();
        System.out.println(hash);
        var target_hash = "0000000000000000007e9e4c586439b0cdbe13b1370bdd9435d76a644d047523";
        System.out.println("Result: "+hash.equals(target_hash));

        System.out.println(">> Test BIP9 (should  be TRUE)");
        System.out.println(block.checkBIP9());

        block = Block.parseSerial(Kit.hexStringToByteArray("0400000039fa821848781f027a2e6dfabbf6bda920d9ae61b63400030000000000000000ecae536a304042e3154be0e3e9a8220e5568c3433a9ab49ac4cbb74f8df8e8b0cc2acf569fb9061806652c27"));

        System.out.println(">> Test BIP9 (should  be FALSE)");
        System.out.println(block.checkBIP9());

        System.out.println(">> Test BIP91 (should be TRUE");
        block = Block.parseSerial(Kit.hexStringToByteArray("1200002028856ec5bca29cf76980d368b0a163a0bb81fc192951270100000000000000003288f32a2831833c31a25401c52093eb545d28157e200a64b21b3ae8f21c507401877b5935470118144dbfd1"));
        System.out.println(block.checkBIP91());

        block = Block.parseSerial(Kit.hexStringToByteArray("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        System.out.println(">> Test BIP91 (should  be FALSE)");
        System.out.println(block.checkBIP91());

        block  = Block.parseSerial(Kit.hexStringToByteArray("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        System.out.println(">> Test BIP141 (should  be TRUE)");
        System.out.println(block.checkBIP141());

        block  = Block.parseSerial(Kit.hexStringToByteArray("0000002066f09203c1cf5ef1531f24ed21b1915ae9abeb691f0d2e0100000000000000003de0976428ce56125351bae62c5b8b8c79d8297c702ea05d60feabb4ed188b59c36fa759e93c0118b74b2618"));
        System.out.println(">> Test BIP141 (should  be FALSE)");
        System.out.println(block.checkBIP141());

        System.out.println("-----------------------------------------------------------");

        block = Block.parseSerial(Kit.hexStringToByteArray("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));

        var target = "13ce9000000000000000000000000000000000000000000";
        var computed_target = block.getTarget().toString(16);

        System.out.println(">> Testing bitsToTarget: "+target.equals(computed_target));
        System.out.println("Target: "+computed_target);
        var difficulty = block.difficulty();
        System.out.println("Difficulty: "+difficulty);
        System.out.println("Test difficulty: "+(difficulty==888171856257.0));

        block = Block.parseSerial(Kit.hexStringToByteArray("04000000fbedbbf0cfdaf278c094f187f2eb987c86a199da22bbb20400000000000000007b7697b29129648fa08b4bcd13c9d5e60abb973a1efac9c8d573c71c807c56c3d6213557faa80518c3737ec1'"));
        System.out.println(">> Checking PoW for block hash id:"+block.getHashHexString());
        System.out.println("Difficulty:"+block.difficulty());
        System.out.println("Target: "+block.getTargetHexString());
        System.out.println("Check PoW:"+block.checkPoW());


        block = Block.parseSerial(Kit.hexStringToByteArray("04000000fbedbbf0cfdaf278c094f187f2eb987c86a199da22bbb20400000000000000007b7697b29129648fa08b4bcd13c9d5e60abb973a1efac9c8d573c71c807c56c3d6213557faa80518c3737ec0"));
        System.out.println(">> Checking PoW for block hash id:"+block.getHashHexString());
        System.out.println("Difficulty:"+block.difficulty());
        System.out.println("Target: "+block.getTargetHexString());
        System.out.println("Check PoW:"+block.checkPoW());
        System.out.println("-------------------------------------------------------");

        System.out.println(">> Testing new bits");
        var prev_bits = Kit.hexStringToByteArray("54d80118");
        var time_diff = 302400;
        var target_bits = Kit.hexStringToByteArray("00157617");
        var new_bits = Block.computeNewBits(prev_bits,time_diff);
        System.out.println("Result:"+ Arrays.equals(target_bits,new_bits));

    }
}
