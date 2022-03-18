package wallet;

import bitcoffee.PrivateKey;

import java.util.ArrayList;

public class HDPrivateKey {

    private PrivateKey private_key;
    private String network;
    private String chain_code;
    private int depth;
    private String parent_fingerprint;
    private String priv_version;
    private String pub_version;
    private int child_number;



    public HDPrivateKey(PrivateKey pk, String chain_code, int depth, String parent_fingeprint, int child_number, String network, String priv_version, String pub_version) {

        this.private_key = pk;
        this.network = network;
        this.chain_code = chain_code;
        this.depth = depth;
        this.parent_fingerprint = parent_fingeprint;
        this.child_number = child_number;

        if (priv_version==null) {
            if (network.equals("mainnet")) {
                priv_version = Hd.XPRV_mainnet;
            }
            else priv_version = Hd.XPRV_testnet;
        }

    }
    public HDPrivateKey(PrivateKey pk, String chain_code,  String network, String priv_version, String pub_version) {

        this(pk,chain_code,0,"00000000",0,"mainnet",priv_version,pub_version);

    }

    public static HDPrivateKey fromMnemonic(ArrayList<String> words) {
        HDPrivateKey pk = null;

        return pk;
    }

}
