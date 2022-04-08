package wallet;

import bitcoffee.Kit;
import bitcoffee.PrivateKey;

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
        this.testnet = testnet;
        this.chain_code = chain_code;
        this.depth = depth;
        this.parent_fingerprint = parent_fingeprint;
        this.child_number = child_number;

        if (priv_version==null) {
            if (!testnet) {
                priv_version = Hd.XPRV_mainnet;
            }
            else priv_version = Hd.XPRV_testnet;
        }

    }
    /*-------------------------------------------------------------------------------------------*/
    public HDPrivateKey(PrivateKey pk, byte[] chain_code,  boolean testnet, String priv_version, String pub_version) {

        this(pk,chain_code,0,"00000000",0,testnet,priv_version,pub_version);

    }

    /*-------------------------------------------------------------------------------------------*/
    public static byte[] hmac_sha152(String key, byte[] msg) {
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

        var h= hmac_sha152(key, seed);

        var h_hex = Kit.bytesToHexString(h);

        var first_32_bytes = Arrays.copyOfRange(h,0,32);

        var secret = new BigInteger(1,first_32_bytes);
        var pk = new PrivateKey(secret);

        var chain_code = Arrays.copyOfRange(h,32,64);

        return new HDPrivateKey(pk,chain_code,testnet,priv_version,pub_version);


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

        if (by != null) {

            var normalized = mnemonic.getSeedString();
            var pre_salt = "mnemonic" + password;
            var salt = pre_salt.getBytes(StandardCharsets.UTF_8);
            try {
                final byte[] hash = PBKDF2WithHmacSHA512.hash(normalized, salt);

                var hash_hex = Kit.bytesToHexString(hash);
                // TODO: add traverse
                return fromSeed(hash, testnet, null, null);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
