package wallet;

import bitcoffee.Kit;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Mnemonic {

    private static final String WORDLIST_FILE = "bip39_words.txt";
    private ArrayList<String> seed_words = new ArrayList<>();
    private static final String[] BIP39_WORDS = new String[2048];
    private static HashMap<String,Integer> BIP39_MAP;

    private static Mnemonic INSTANCE = new Mnemonic();

    private Mnemonic() {
        INSTANCE.loadBIP39Words();
    }

    public static Mnemonic getInstance() {
        return INSTANCE;
    }

    private boolean setBip39FirstWords(ArrayList<String> words) {
        // TODO: add at least also 23
        if (words.size()!=11) {
            System.out.println("Wrong size in BIP39 words:"+words.size());
            return false;
        }

        var list = Arrays.asList(BIP39_WORDS);

        for (String w:words) {
            if (!list.contains(w)) {
                System.out.println("Invalid BIP39 word:"+w);
                return false;
            }
        }
        this.seed_words = words;
        return true;
    }

    public static String[] getBip39Words() {
        return BIP39_WORDS;
    }

    private void loadBIP39Words() {
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

            System.out.println("Loading "+BIP39_WORDS[current-1]);
        }
    }

    public byte[] mnemonicToBytes() {

        var all_bits = BigInteger.ZERO;

        if (this.seed_words.size()!=12) {
            System.out.println("Wrong mnemonic size:"+this.seed_words.size());
        }

        for (String word: seed_words) {
            all_bits = all_bits.shiftLeft(11).add(BigInteger.valueOf(BIP39_MAP.get(word)));
        }

        // TODO: support other sizes
        var num_checksum_bits = BigInteger.valueOf(12/3);
        var checksum = all_bits.and(BigInteger.ONE.shiftLeft(num_checksum_bits.intValue()).subtract(BigInteger.ONE));
        all_bits = all_bits.shiftRight(num_checksum_bits.intValue());

        var num_bytes = (12*11-num_checksum_bits.intValue())/8;

        var all_bytes = all_bits.toByteArray();

        var s = new byte[num_bytes];

        // must create a big endian of num_bytes
        // so copy from right last elements (less significant)
        // possibly removing zeros at the first elements
        for (int i=num_bytes;i>0;i--) {

            s[i-1] = all_bytes[i-1];
        }

        var computed_checksum = Kit.sha256(s)[0] >> (8-num_checksum_bits.intValue());

        if (computed_checksum!=checksum.intValue()) {
            System.out.println("Invalid checksum! ");
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

}