import java.util.ArrayList;

public class TestMerkle {

    public static void main(String[] args) {

        var test = new Test<String>("Merkle");
        test.begin();

        var hash1 = "c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5";
        var hash2 = "c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5";

        var result = Kit.merkleParent(hash1,hash2);
        var target = "8b30c5ba100f6f2e5ad1e2a742e5020491240f8eb514fe97c713c31718ad7ecd";

        test.check("merke parent","hash1,hash2"+hash1+","+hash2,target,result);

        Test.__BEGIN_NOTES("Parent Level (see page 192, J. Song)");
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
        Test.__END_NOTES();

        test.end();

        var test2 = new Test<Boolean>("Merkled root");
        test2.begin();

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
        block.setTx_hashes(hashes_list);

        test2.check("Merkle root validation","block:"+block+"\ntx_hashes:"+hashes_list,block.validateMerkleRoot(),true);

        test2.end();
    }
}
