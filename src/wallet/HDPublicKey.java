package wallet;

import bitcoffee.*;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class HDPublicKey {


    private final byte[] chain_code;
    private final int depth;
    private final String parent_fingerprint;
    private final long child_number;
    private final boolean testnet;
    public final String pub_version;

    private byte[] raw = null;

    private final S256Point point;



    /*-------------------------------------------------------------------------------------------*/
    public HDPublicKey(S256Point point, byte[] chain_code, int depth, String parent_fingeprint, long child_number, boolean testnet, String pub_version) {

        this.point = point;
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
        return new P2WPKHScriptPubKey(this.hash160());
    }
    public Script get_p2sh_wpkh_script() {
        return new P2WSHScriptPubKey(this.hash160());
    }

    public String getAddress() {
        return this.point.getP2pkhAddress(true,this.testnet);
    }

    public String getP2pkhAddress(boolean testnet) {
        return this.point.getP2pkhAddress(testnet) ;
    }
    public String getP2wpkhAddress(boolean testnet) {
        return this.point.getP2wpkhAddress(testnet) ;
    }

    public String getP2shAddress(boolean testnet) {
        return this.point.getP2shAddress(testnet);
    }


    private byte[] raw_serialize() {

        if (this.raw==null)
            this.raw = serialize(pub_version);

        return raw;

    }
    public byte[] serialize(String pub_version) {

        var bos = new ByteArrayOutputStream();
        bos.write(Kit.hexStringToByteArray(pub_version),0,4);
        bos.write(this.depth);
        bos.write(Kit.hexStringToByteArray(this.parent_fingerprint),0,4);

        var childn = Kit.intToBigEndian(BigInteger.valueOf(this.child_number),4);
        bos.writeBytes(childn);

        bos.writeBytes(this.chain_code);

        bos.writeBytes(Kit.hexStringToByteArray(this.point.SEC33()));

        return bos.toByteArray();
    }

    public String xpub(String version) {
        if (version==null) {
            version = this.pub_version;
        }

        var raw = this.serialize(version);

        var b58 = Kit.encodeBase58Checksum(raw);
        return b58;
    }

    public byte[] fingerprint() {
        var h160 = this.hash160();
        return Arrays.copyOfRange(h160,0,4);
    }



    // TODO: add p2wpkh, p2sh_p2wpkh, p2tr


}
