package wallet;

import java.util.ArrayList;

public class Hd {
    // https://github.com/satoshilabs/slips/blob/master/slip-0132.md
    //Make default xpub/xpriv, but allow for other version bytes

    public static final String XPRV_mainnet = "0488ade4";
    public static final String XPRV_testnet = "04358394";
    public static final String XPRV_signet = "04358394";
    public static final String XPRV_regtest = "04358394";

    public static final String XPUB_mainnet = "0488b21e";
    public static final String XPUB_testnet = "043587cf";
    public static final String XPUB_signet = "043587cf";
    public static final String XPUB_regtest = "043587cf";

    //P2PKH or P2SH, P2WPKH in P2SH, P2WPKH, Multi-signature P2WSH in P2SH, Multi-signature P2WSH
    public static final String[] ALL_MAINNET_XPRVS = {"0488ade4", "049d7878", "04b2430c", "0295b005", "02aa7a99"};
    public static final String[] ALL_MAINNET_XPUBS = {"0488b21e", "049d7cb2", "04b24746", "0295b43f", "02aa7ed3"};

    public static final String[] ALL_TESTNET_XPRVS = {"04358394", "044a4e28", "045f18bc", "024285b5", "02575048"};
    public static final String[] ALL_TESTNET_XPUBS = {"043587cf", "044a5262", "045f1cf6", "024289ef", "02575483"};

    public static final String DEFAULT_P2WSH_PATH_mainnet = "m/48h/0h/0h/2h";
    public static final String DEFAULT_P2WSH_PATH_testnet = "m/48h/1h/0h/2h";
    public static final String DEFAULT_P2WSH_PATH_signet = "m/48h/1h/0h/2h";
    public static final String DEFAULT_P2WSH_PATH_regtest = "m/48h/1h/0h/2h";

    private static int start_word = 0;

    // TODO: generator-like implementation
    public static String nextValidChecksum(ArrayList<String> first_words) {
        // returns the first word that can be used as checksum if added to first words
        var words = Mnemonic.getBip39Words();

        for (int i = start_word; i < 2048; i++) {
            first_words.add(words[i]);
            if (HDPrivateKey.fromMnemonic(first_words) != null) {
                start_word = i+1;
                return words[i];
            }
            first_words.remove(first_words.size() - 1);
        }
        return null;
    }
}


