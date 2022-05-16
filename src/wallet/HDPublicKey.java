package wallet;

import bitcoffee.P2PKHScriptPubKey;
import bitcoffee.S256Point;
import bitcoffee.Script;

public class HDPublicKey {


    S256Point point;
    private byte[] chain_code;
    private int depth;
    private String parent_fingerprint;
    private int child_number;
    private boolean testnet;
    private String pub_version;



    /*-------------------------------------------------------------------------------------------*/
    public HDPublicKey(S256Point point, byte[] chain_code, int depth, String parent_fingeprint, int child_number, boolean testnet, String pub_version) {

        this.testnet = testnet;
        this.chain_code = chain_code;
        this.depth = depth;
        this.parent_fingerprint = parent_fingeprint;
        this.child_number = child_number;

        if (pub_version==null) {
            if (!testnet) {
                pub_version = Hd.XPUB_mainnet;
            }
            else pub_version = Hd.XPUB_testnet;
        }
        this.pub_version = pub_version;
    }
    /*-------------------------------------------------------------------------------------------*/

    public String SEC() {
        return this.point.SEC33();
    }

    public byte[] hash160() {
        return this.point.getHash160(true);
    }

    public Script get_p2pkh_script() {
        return new P2PKHScriptPubKey(this.hash160());
    }
    public Script get_p2wpkh_script() {
        return Script.P2WPKHScriptPubKey(this.hash160());
    }
    public Script get_p2sh_wpkh_script() {
        return Script.P2WSHScriptPubKey(this.hash160());
    }

    public String getAddress() {
        if (testnet) return this.point.getP2pkhTestnetAddress();
        else return this.point.getP2pkhAddress(true);
    }

    // TODO: add p2wpkh, p2sh_p2wpkh, p2tr


}
