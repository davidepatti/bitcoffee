import java.util.ArrayList;
import java.util.Objects;

public class TestMerkle {

    public static void main(String[] args) {

        Test.__BEGIN_TEST("Merkle");

        var hash1 = "c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5";
        var hash2 = "c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5";

        var result = Kit.merkleParent(hash1,hash2);
        var target = "8b30c5ba100f6f2e5ad1e2a742e5020491240f8eb514fe97c713c31718ad7ecd";

        Test.check("merke parent","hash1,hash2"+hash1+","+hash2,target,result);

        var hashes = new ArrayList<String>();

        hashes.add("c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5");
        hashes.add("c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5");
        hashes.add("f391da6ecfeed1814efae39e7fcb3838ae0b02c02ae7d0a5848a66947c0727b0");
        hashes.add("3d238a92a94532b946c90e19c49351c763696cff3db400485b813aecb8a13181");
        hashes.add("10092f2633be5f3ce349bf9ddbde36caa3dd10dfa0ec8106bce23acbff637dae");

        System.out.println("Parent level of :");
        System.out.println(hashes);
        System.out.println("-----------------------------");
        System.out.println(Kit.merkleParentLevel(hashes));
        Test.__END_TEST();

        var hl = new ArrayList<String>();

        hl.add("9745f7173ef14ee4155722d1cbf13304339fd00d900b759c6f9d58579b5765fb");
        hl.add("5573c8ede34936c29cdfdfe743f7f5fdfbd4f54ba0705259e62f39917065cb9b");
        hl.add("82a02ecbb6623b4274dfcab82b336dc017a27136e08521091e443e62582e8f05");
        hl.add("507ccae5ed9b340363a0e6d765af148be9cb1c8766ccc922f83e4ae681658308");
        hl.add("a7a4aec28e7162e1e9ef33dfa30f0bc0526e6cf4b11a576f6c5de58593898330");
        hl.add("bb6267664bd833fd9fc82582853ab144fece26b7a8a5bf328f8a059445b59add");
        hl.add("ea6d7ac1ee77fbacee58fc717b990c4fcccf1b19af43103c090f601677fd8836");
        hl.add("457743861de496c429912558a106b810b0507975a49773228aa788df40730d41");
        hl.add("7688029288efc9e9a0011c960a6ed9e5466581abf3e3a6c26ee317461add619a");
        hl.add("b1ae7f15836cb2286cdd4e2c37bf9bb7da0a2846d06867a429f654b2e7f383c9");
        hl.add("9b74f89fa3f93e71ff2c241f32945d877281a6a50a6bf94adac002980aafe5ab");
        hl.add("b3a92b5b255019bdaf754875633c2de9fec2ab03e6b8ce669d07cb5b18804638");
        hl.add("b5c0b915312b9bdaedd2b86aa2d0f8feffc73a2d37668fd9010179261e25e263");
        hl.add("c9d52c5cb1e557b92c84c52e7c4bfbce859408bedffc8a5560fd6e35e10b8800");
        hl.add("c555bc5fc3bc096df0a0c9532f07640bfb76bfe4fc1ace214b8b228a1297a4c2");
        hl.add("f9dbfafc3af3400954975da24eb325e326960a25b87fffe23eef3e7ed2fb610e");

        var merk = new MerkleTree(hl.size());
        merk.setNodesLevel(4,hl);
        merk.setNodesLevel(3,Kit.merkleParentLevel(merk.getNodesLevel(4)));
        merk.setNodesLevel(2,Kit.merkleParentLevel(merk.getNodesLevel(3)));
        merk.setNodesLevel(1,Kit.merkleParentLevel(merk.getNodesLevel(2)));
        merk.setNodesLevel(0,Kit.merkleParentLevel(merk.getNodesLevel(1)));

        Test.check("merkleParentLevel",""+merk,merk.getRoot(),"597c4bafe3832b17cbbabe56f878f4fc2ad0f6a402cee7fa851a9cb205f87ed1");
        Test.__END_TEST();
        //////////////////////////////////////////////////////////////////////////////////////7

        Test.__BEGIN_TEST("Populate Tree with hashes");

        var mt1 = new MerkleTree(hl.size());
        mt1.populateTree(hl);
        Test.check("populateTree(hl)","hl"+hl,mt1.getRoot(),"597c4bafe3832b17cbbabe56f878f4fc2ad0f6a402cee7fa851a9cb205f87ed1");

        var flags = new ArrayList<Boolean>();
        for (int i=0;i<31;i++) flags.add(true);

        mt1 = new MerkleTree(hl.size());
        mt1.populateTree(flags,hl);
        Test.check("populateTree(with true flags)",""+mt1,mt1.getRoot(),"597c4bafe3832b17cbbabe56f878f4fc2ad0f6a402cee7fa851a9cb205f87ed1");
        Test.__END_TEST();
        //////////////////////////////////////////////////////////////////////////////////////7

        Test.__BEGIN_TEST("MerkleBlock");

        var hashes_list = new ArrayList<String>();
        hashes_list.add("f54cb69e5dc1bd38ee6901e4ec2007a5030e14bdd60afb4d2f3428c88eea17c1");
        hashes_list.add("c57c2d678da0a7ee8cfa058f1cf49bfcb00ae21eda966640e312b464414731c1");
        hashes_list.add("b027077c94668a84a5d0e72ac0020bae3838cb7f9ee3fa4e81d1eecf6eda91f3");
        hashes_list.add("8131a1b8ec3a815b4800b43dff6c6963c75193c4190ec946b93245a9928a233d");
        hashes_list.add("ae7d63ffcb3ae2bc0681eca0df10dda3ca36dedb9dbf49e33c5fbe33262f0910");
        hashes_list.add("61a14b1bbdcdda8a22e61036839e8b110913832efd4b086948a6a64fd5b3377d");
        hashes_list.add("fc7051c8b536ac87344c5497595d5d2ffdaba471c73fae15fe9228547ea71881");
        hashes_list.add("77386a46e26f69b3cd435aa4faac932027f58d0b7252e62fb6c9c2489887f6df");
        hashes_list.add("59cbc055ccd26a2c4c4df2770382c7fea135c56d9e75d3f758ac465f74c025b8");
        hashes_list.add("7c2bf5687f19785a61be9f46e031ba041c7f93e2b7e9212799d84ba052395195");
        hashes_list.add("08598eebd94c18b0d59ac921e9ba99e2b8ab7d9fccde7d44f2bd4d5e2e726d2e");
        hashes_list.add("f0bb99ef46b029dd6f714e4b12a7d796258c48fee57324ebdc0bbc4700753ab1");

        var block = Block.parseSerial(Kit.hexStringToByteArray("00000020fcb19f7895db08cadc9573e7915e3919fb76d59868a51d995201000000000000acbcab8bcc1af95d8d563b77d24c3d19b18f1486383d75a5085c4e86c86beed691cfa85916ca061a00000000"));
        Objects.requireNonNull(block).setTx_hashes(hashes_list);
        Test.check("Merkle root validation","block:"+block+"\ntx_hashes:"+hashes_list,block.validateMerkleRoot(),true);

        Test.__END_TEST();

        ///////////////////////////////////////////////////////////////////////////////////////
        //Test.__BEGIN_TEST("MerkleBlock Flags");
        hl = new ArrayList<>();

        hl.add(Kit.reverseByteString("ba412a0d1480e370173072c9562becffe87aa661c1e4a6dbc305d38ec5dc088a"));
        hl.add(Kit.reverseByteString("7cf92e6458aca7b32edae818f9c2c98c37e06bf72ae0ce80649a38655ee1e27d"));
        hl.add(Kit.reverseByteString("34d9421d940b16732f24b94023e9d572a7f9ab8023434a4feb532d2adfc8c2c2"));
        hl.add(Kit.reverseByteString("158785d1bd04eb99df2e86c54bc13e139862897217400def5d72c280222c4cba"));
        hl.add(Kit.reverseByteString("ee7261831e1550dbb8fa82853e9fe506fc5fda3f7b919d8fe74b6282f92763ce"));
        hl.add(Kit.reverseByteString("f8e625f977af7c8619c32a369b832bc2d051ecd9c73c51e76370ceabd4f25097"));
        hl.add(Kit.reverseByteString("c256597fa898d404ed53425de608ac6bfe426f6e2bb457f1c554866eb69dcb8d"));
        hl.add(Kit.reverseByteString("6bf6f880e9a59b3cd053e6c7060eeacaacf4dac6697dac20e4bd3f38a2ea2543"));
        hl.add(Kit.reverseByteString("d1ab7953e3430790a9f81e1c67f5b58c825acf46bd02848384eebe9af917274c"));
        hl.add(Kit.reverseByteString("dfbb1a28a5d58a23a17977def0de10d644258d9c54f886d47d293a411cb62261"));

        hl = new ArrayList<>();

        hl.add("9745f7173ef14ee4155722d1cbf13304339fd00d900b759c6f9d58579b5765fb");
        hl.add("5573c8ede34936c29cdfdfe743f7f5fdfbd4f54ba0705259e62f39917065cb9b");
        hl.add("82a02ecbb6623b4274dfcab82b336dc017a27136e08521091e443e62582e8f05");
        hl.add("507ccae5ed9b340363a0e6d765af148be9cb1c8766ccc922f83e4ae681658308");
        hl.add("a7a4aec28e7162e1e9ef33dfa30f0bc0526e6cf4b11a576f6c5de58593898330");
        hl.add("bb6267664bd833fd9fc82582853ab144fece26b7a8a5bf328f8a059445b59add");
        hl.add("ea6d7ac1ee77fbacee58fc717b990c4fcccf1b19af43103c090f601677fd8836");
        hl.add("457743861de496c429912558a106b810b0507975a49773228aa788df40730d41");
        hl.add("7688029288efc9e9a0011c960a6ed9e5466581abf3e3a6c26ee317461add619a");
        hl.add("b1ae7f15836cb2286cdd4e2c37bf9bb7da0a2846d06867a429f654b2e7f383c9");
        hl.add("9b74f89fa3f93e71ff2c241f32945d877281a6a50a6bf94adac002980aafe5ab");
        hl.add("b3a92b5b255019bdaf754875633c2de9fec2ab03e6b8ce669d07cb5b18804638");
        hl.add("b5c0b915312b9bdaedd2b86aa2d0f8feffc73a2d37668fd9010179261e25e263");
        hl.add("c9d52c5cb1e557b92c84c52e7c4bfbce859408bedffc8a5560fd6e35e10b8800");
        hl.add("c555bc5fc3bc096df0a0c9532f07640bfb76bfe4fc1ace214b8b228a1297a4c2");
        hl.add("f9dbfafc3af3400954975da24eb325e326960a25b87fffe23eef3e7ed2fb610e");
        hl.add("38faf8c811988dff0a7e6080b1771c97bcc0801c64d9068cffb85e6e7aacaf51");

        Test.__BEGIN_TEST("MerkleBlock isValid()");

        /*
        var tm = new MerkleTree(7);
        var ff = MerkleBlock.bitStringToBitField("10111000");

        tm.print();
        var sh = new ArrayList<String>();
        sh.add("9745f7173ef14ee4155722d1cbf13304339fd00d900b759c6f9d58579b5765fb");
        sh.add("5573c8ede34936c29cdfdfe743f7f5fdfbd4f54ba0705259e62f39917065cb9b");
        sh.add("82a02ecbb6623b4274dfcab82b336dc017a27136e08521091e443e62582e8f05");
        sh.add("507ccae5ed9b340363a0e6d765af148be9cb1c8766ccc922f83e4ae681658308");

        tm.populateTree(ff,sh);

         */
        var block_raw = "00000020df3b053dc46f162a9b00c7f0d5124e2676d47bbe7c5d0793a500000000000000ef445fef2ed495c275892206ca533e7411907971013ab83e3b47bd0d692d14d4dc7c835b67d8001ac157e670bf0d00000aba412a0d1480e370173072c9562becffe87aa661c1e4a6dbc305d38ec5dc088a7cf92e6458aca7b32edae818f9c2c98c37e06bf72ae0ce80649a38655ee1e27d34d9421d940b16732f24b94023e9d572a7f9ab8023434a4feb532d2adfc8c2c2158785d1bd04eb99df2e86c54bc13e139862897217400def5d72c280222c4cbaee7261831e1550dbb8fa82853e9fe506fc5fda3f7b919d8fe74b6282f92763cef8e625f977af7c8619c32a369b832bc2d051ecd9c73c51e76370ceabd4f25097c256597fa898d404ed53425de608ac6bfe426f6e2bb457f1c554866eb69dcb8d6bf6f880e9a59b3cd053e6c7060eeacaacf4dac6697dac20e4bd3f38a2ea2543d1ab7953e3430790a9f81e1c67f5b58c825acf46bd02848384eebe9af917274cdfbb1a28a5d58a23a17977def0de10d644258d9c54f886d47d293a411cb6226103b55635";
        var mb = MerkleBlock.parseSerial(Kit.hexStringToByteArray(block_raw));
        Test.check("Merkle Block validity",""+mb,true, Objects.requireNonNull(mb).isValid());

        var root = "ef445fef2ed495c275892206ca533e7411907971013ab83e3b47bd0d692d14d4";
        Test.check("getMerkleRoot","",root,(Kit.reverseByteString(mb.getMerkle_root())));
        var flag_bytes = Kit.hexStringToByteArray("b55635");
        Test.check("flags","",mb.getFlags(),"b55635");
        Test.__END_TEST();
    }
}
