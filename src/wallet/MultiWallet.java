package wallet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MultiWallet {

    public static void main(String[] args) {

        String choice = "";
        var sc = new Scanner(System.in);

        while (!choice.equals("q")) {

            System.out.println("------------------------------------------------");
            System.out.println(" bitcofee toolkit");
            System.out.println("------------------------------------------------");
            System.out.println(" (g) generate seed from BIP39 ");
            System.out.println(" (t) generate with test seed ");
            System.out.println("------------------------------------------------");

            System.out.print(" -> enter choice: ");
            choice = sc.nextLine();

            switch (choice) {
                case "g":
                    generate_seed(false);
                    break;
                case "t":
                    generate_seed(true);
                    break;
                case "q":
                    System.exit(0);
            }
        }
    }

    private static void generate_seed(boolean test) {
        // get first 23 words
        // during now junior phrase tilt vivid today journey lend scorpion brief marble carry glass group rubber loop venue shrimp top place green drill


        Scanner sc = new Scanner(System.in);
        System.out.println("Enter first 23 words: ");
        //var first_words = "during now junior phrase tilt vivid today journey lend scorpion brief marble carry glass group rubber loop venue shrimp top place green drill";
        String first_words;
        if (test)
            first_words = "during baby junior phrase tilt vivid today journey lend scorpion brief marble carry glass group rubber loop venue shrimp top place green drill";
        else
            first_words = sc.nextLine();

        var first_words_list = new ArrayList<>(Arrays.asList(first_words.split(" ")));
        // circle
        if (first_words_list.size()!=23) {
            System.out.println("Wrong size in the first 23 BIP39 words:"+first_words_list.size());
            System.exit(-1);
        }

        var map = Mnemonic.getBip39Map();

        var seed_words = new String[24];
        int n = 0;

        for (String w:first_words_list) {
            if (!map.containsKey(w)) {
                System.out.println("Invalid BIP39 word:"+w);
                System.exit(-1);
            }
            else {
                seed_words[n] = w;
                n++;
            }
        }

        // default checksum: use the first valid word
        String checksum_word = Hd.nextValidChecksum(seed_words);

        System.out.println("Computed checksum word: "+checksum_word);
        System.out.println("Complete seed: "+seed_words);


        var password = "";
        boolean testnet = false;
        String path_to_use = null;
        boolean use_slip132_version_byte = true;


        seed_words[23] = checksum_word;

        var hd_priv = HDPrivateKey.fromMnemonic(seed_words, password, false);

    }
}
