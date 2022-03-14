package wallet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Mnemonic {

    private static final String WORDLIST_FILE = "bip39_words.txt";
    private String[] seed_words = new String[32];
    private static final String[] BIP39_WORDS = new String[2048];

    private static Mnemonic INSTANCE = new Mnemonic();

    private boolean setBip39FirstWords(String[] words) {
        if (words.length!=11) {
            System.out.println("Wrong size in BIP39 words:"+words.length);
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
    private Mnemonic() {
        INSTANCE.loadBIP39Words();
    }

    public static Mnemonic getInstance() {
        return INSTANCE;
    }

    public static String[] getBip39Words() {
        return INSTANCE.seed_words;
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
            BIP39_WORDS[current++] = fs.nextLine();
            System.out.println("Loading "+BIP39_WORDS[current-1]);
        }
    }


}