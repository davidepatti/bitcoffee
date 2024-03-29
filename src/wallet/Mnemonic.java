package wallet;

import bitcoffee.Kit;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Mnemonic {

    private static final String WORDLIST_FILE = "bip39_words.txt";
    private String[] seed_words = new String[24];
    private static final String[] BIP39_WORDS = new String[2048];
    private static final HashMap<String,Integer> BIP39_MAP = new HashMap<>();

    private final static Mnemonic INSTANCE = new Mnemonic();

    private static boolean bip39_ready = false;

    private Mnemonic() {
        loadBIP39Words();
    }

    public static Mnemonic getInstance() {
        return INSTANCE;
    }


    public String[] getSeedWords() {
        return this.seed_words;
    }


    public static String[] getBip39Words() {
        return BIP39_WORDS;
    }

    public static HashMap<String,Integer> getBip39Map() { return BIP39_MAP;}

    private static void loadBIP39Words() {
        Scanner fs = null;

        try {
            var file = new File(WORDLIST_FILE);
            fs = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int current = 0;
        while (fs.hasNextLine()) {
            String new_word = fs.nextLine();
            BIP39_WORDS[current] =  new_word;
            BIP39_MAP.put(new_word,current);
            current++;
        }

        bip39_ready = true;
    }

    public byte[] mnemonicToBytes() {

        var all_bits = BigInteger.ZERO;
        var num_words = seed_words.length;

        if (num_words!=24) {
            System.out.println("Wrong mnemonic size:"+num_words);
        }

        for (String word: this.seed_words) {
            all_bits = all_bits.shiftLeft(11);
            all_bits = all_bits.add(BigInteger.valueOf(BIP39_MAP.get(word)));
        }

        // TODO: support other sizes
        var num_checksum_bits = BigInteger.valueOf(num_words/3);
        var checksum = all_bits.and(BigInteger.ONE.shiftLeft(num_checksum_bits.intValue()).subtract(BigInteger.ONE));
        all_bits = all_bits.shiftRight(num_checksum_bits.intValue());

        var num_bytes = (num_words*11-num_checksum_bits.intValue())/8;

        var s = Kit.intToBigEndian(all_bits,num_bytes);

        /* TODO: remove, for debug
        var sx = Kit.bytesToHexString(s);
        var t1 = Kit.sha256(s);
        var x = Kit.bytesToHexString(t1);
        var t2 = t1[0];
        var t3 = t2 >> (8-num_checksum_bits.intValue());
         */

        // remove sign of byte before shifting, otherwise sign 1 is extented
        var computed_checksum = Kit.sha256(s) [0] & 0x00ff;
        //var ch = Kit.bytesToHexString(Kit.intToLittleEndianBytes(computed_checksum));
        computed_checksum = computed_checksum >> (8-num_checksum_bits.intValue());


        if (computed_checksum!=checksum.intValue()) {
            return null;
        }
        return s;
    }


    public ArrayList<String> bytesToMnemonic(byte[] bytes, int num_bits) {

        if (num_bits!=12) {
            System.out.println("Wrong num bits");
            System.exit(-1);
        }

        var pre_seed = new BigInteger(1,bytes);
        var num_checksum_bits = num_bits/32;

        var checksum = Kit.sha256(bytes)[0] >> (8-num_checksum_bits);

        var all_bits = (pre_seed.shiftLeft(num_checksum_bits)).or(BigInteger.valueOf(checksum));

        ArrayList<String> mnemonic = new ArrayList<>();

        for (int i=0;i< (num_bits+num_checksum_bits)/11;i++) {
            int current = all_bits.and(BigInteger.valueOf((1<<11)-1)).intValue();
            mnemonic.add(0,BIP39_WORDS[current]);
            all_bits = all_bits.shiftRight(11);
        }

        return mnemonic;
    }

    public String getSeedString() {
        String ret = this.seed_words[0];
        for (int c=1;c<seed_words.length;c++)
            ret+=" "+this.seed_words[c];

        return ret;
    }

    public void setSeedWords(String[] words) {
        this.seed_words = words;
    }
}