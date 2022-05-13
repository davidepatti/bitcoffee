package wallet;

import bitcoffee.Kit;
import bitcoffee.PrivateKey;
import bitcoffee.Secp256k1;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class HDPrivateKey {

    private PrivateKey private_key;
    private boolean testnet;
    private byte[] chain_code;
    private int depth;
    private String parent_fingerprint;
    private String priv_version;
    private String pub_version;
    private int child_number;



    /*-------------------------------------------------------------------------------------------*/
    public HDPrivateKey(PrivateKey pk, byte[] chain_code, int depth, String parent_fingeprint, int child_number, boolean testnet, String priv_version, String pub_version) {

        this.private_key = pk;
        this.private_key.setTestnet(testnet); // TODO: check if required...
        this.testnet = testnet;
        this.chain_code = chain_code;
        this.depth = depth;
        this.parent_fingerprint = parent_fingeprint;
        this.child_number = child_number;

        // TODO: not supported signet/regtest
        if (priv_version==null) {
            if (!testnet) {
                priv_version = Hd.XPRV_mainnet;
            }
            else priv_version = Hd.XPRV_testnet;
        }
        this.priv_version = priv_version;

        // TODO: keep a copy of the corresponding pubkey (line 97 hd.py)
    }
    /*-------------------------------------------------------------------------------------------*/
    public HDPrivateKey(PrivateKey pk, byte[] chain_code,  boolean testnet, String priv_version, String pub_version) {

        this(pk,chain_code,0,"00000000",0,testnet,priv_version,pub_version);

    }

    /*-------------------------------------------------------------------------------------------*/
    public static byte[] hmac_sha512(String key, byte[] msg) {
        String algo = "HmacSHA512";
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algo);
        Mac mac = null;
        try {
            mac = Mac.getInstance(algo);
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return mac.doFinal(msg);
    }

    public static HDPrivateKey fromSeed(byte[] seed, boolean testnet, String priv_version, String pub_version) {
        String key = "Bitcoin seed";

        var h= hmac_sha512(key, seed);

        var h_hex = Kit.bytesToHexString(h);

        var first_32_bytes = Arrays.copyOfRange(h,0,32);

        var secret = new BigInteger(1,first_32_bytes);
        var pk = new PrivateKey(secret);

        var chain_code = Arrays.copyOfRange(h,32,64);

        return new HDPrivateKey(pk,chain_code,testnet,priv_version,pub_version);
    }

    public HDPrivateKey traverse(String path) {
        path = path.toLowerCase().replace('h','\'');

        if (!path.startsWith("m"))
            throw new RuntimeException("Invalid path:"+path);

        var current = this;
        var components = path.split("/");
        // ignore the first
        components = Arrays.copyOfRange(components,1,components.length);

        for (String child:components) {
            int index;
            if (child.endsWith("\'")) {
                var sub = child.substring(0,child.length()-1);
                index = Integer.parseInt(sub)+0x80000000;
            }
            else index = Integer.parseInt(child);

            current = current.child(index);

        }
        return current;
    }

    /*Returns the child HDPrivateKey at a particular index.
    Hardened children return for indices >= 0x8000000. */
    public HDPrivateKey child(int index) {
        byte[] data;

        if (index>= 0x80000000) {

            var data1 = Kit.intToBigEndian(this.private_key.secret_n,33);
            var data2 = Kit.intToBigEndian(BigInteger.valueOf(index),4);

            data = Kit.concatBytes(data1,data2);
        }
        else {
            var data1 = Kit.hexStringToByteArray(this.private_key.point.SEC33());
            var data2 = Kit.intToBigEndian(BigInteger.valueOf(index),4);
            data = Kit.concatBytes(data1,data2);
        }

        var h = hmac_sha512(Kit.bytesToAscii(this.chain_code),data);

        var data1 = Arrays.copyOfRange(h,0,32);
        var secret = new BigInteger(1,data1).add(this.private_key.secret_n).mod(Secp256k1.N);

        var privatekey = new PrivateKey(secret);

        var chain_code = Arrays.copyOfRange(h,32,h.length);
        var depth = this.depth+1;
        var parent_fingeprint = this.parent_fingerprint;
        var child_number = index;

        return new HDPrivateKey(privatekey,chain_code,depth,parent_fingeprint,child_number,this.testnet,this.priv_version,this.pub_version);

    }


    /*-------------------------------------------------------------------------------------------*/
    public static HDPrivateKey fromMnemonic(ArrayList<String> words) {

        return fromMnemonic(words,"","m",false);
    }

    /*-------------------------------------------------------------------------------------------*/
    public static HDPrivateKey fromMnemonic(ArrayList<String> words, String password, String path, boolean testnet) {

        var mnemonic = Mnemonic.getInstance();
        mnemonic.setSeedWords(words);

        var by = mnemonic.mnemonicToBytes();
        //var by_hex = Kit.bytesToHexString(by);

        if (by != null) {

            var normalized = mnemonic.getSeedString();
            var pre_salt = "mnemonic" + password;
            var salt = pre_salt.getBytes(StandardCharsets.UTF_8);
            try {
                final byte[] hash = PBKDF2WithHmacSHA512.hash(normalized, salt);

                var hash_hex = Kit.bytesToHexString(hash);

                // TODO: CHECK traverse
                return fromSeed(hash, testnet, null, null).traverse(path);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
