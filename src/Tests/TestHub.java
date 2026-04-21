package Tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestHub {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final List<TestEntry> entries = buildEntries();

    public static void main(String[] args) throws IOException {
        new TestHub().run();
    }

    private void run() throws IOException {
        while (true) {
            printMenu();
            var choice = readLine("Select a test number or q to quit");
            if (choice == null) {
                return;
            }
            var normalizedChoice = choice.trim();
            if (normalizedChoice.equalsIgnoreCase("q")) {
                return;
            }
            if (normalizedChoice.isBlank()) {
                continue;
            }

            int index;
            try {
                index = Integer.parseInt(normalizedChoice) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection: " + normalizedChoice);
                pause();
                continue;
            }

            if (index < 0 || index >= entries.size()) {
                System.out.println("Selection out of range: " + normalizedChoice);
                pause();
                continue;
            }

            runEntry(entries.get(index));
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println(" " + TerminalStyle.emphasis("Bitcoffee Test Hub"));
        System.out.println("============================================================");
        System.out.println("Press Enter on prompted parameters to keep the default value.");
        System.out.println("Tests tagged " + TerminalStyle.dim("[network]") + " require external connectivity.");
        System.out.println();

        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            var menuIndex = String.format("%2d", i + 1);
            System.out.println(TerminalStyle.number(menuIndex) + ". " + TerminalStyle.bold(entry.title) + entry.tags());
            System.out.println("    " + entry.description);
        }
        System.out.println();
    }

    private void runEntry(TestEntry entry) throws IOException {
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println("Running: " + TerminalStyle.bold(entry.title));
        System.out.println("------------------------------------------------------------");
        var launchSpec = entry.configure(this);

        var command = new ArrayList<String>();
        command.add(getJavaBinary());
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(entry.className);
        command.addAll(launchSpec.args);

        var pb = new ProcessBuilder(command);
        pb.directory(new File(System.getProperty("user.dir")));
        pb.inheritIO();
        pb.environment().putAll(launchSpec.environment);

        int exitCode;
        try {
            var process = pb.start();
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for test process", e);
        }

        System.out.println();
        System.out.println("Exit code: " + TerminalStyle.number(String.valueOf(exitCode)));
        if (exitCode != 0) {
            System.out.println("The selected test finished with a non-zero status.");
        }
        pause();
    }

    private List<TestEntry> buildEntries() {
        var list = new ArrayList<TestEntry>();
        list.add(TestEntry.simple("Address Helpers", "Bech32/Base58 address helpers and regressions.", "Tests.TestAddresses"));
        list.add(TestEntry.simple("Block", "Block parsing, BIP flags, proof-of-work and difficulty vectors.", "Tests.TestBlock"));
        list.add(TestEntry.configurable(
                "Bloom Filter",
                "Bloom filter unit checks plus the SPV example with configurable network inputs.",
                "Tests.TestBloomFilter",
                true,
                hub -> {
                    boolean testnet = hub.promptBoolean("Use testnet", true);
                    String host = hub.promptLine("Peer host", testnet ? "testnet.programmingbitcoin.com" : "mainnet.programmingbitcoin.com");
                    String lastBlock = hub.promptLine(
                            "Last block hash",
                            testnet
                                    ? "00000000000538d5c2246336644f9a4956551afb44ba47278759ec55ea912e19"
                                    : "0000000000000000000838497f627c016c2bb9097d6794c6aeac1a581bd26984");
                    String address = hub.promptLine(
                            "Address to watch",
                            testnet
                                    ? "mwJn1YPMq7y5F8J3LkC5Hxg9PHyZ5K4cFv"
                                    : "3Ffi6K7abWQsVMXUQuUNGviNAghXrY9Bni");
                    return LaunchSpec.of(List.of(host, lastBlock, address, Boolean.toString(testnet)));
                }));
        list.add(TestEntry.configurable(
                "Broadcast Tx Builder",
                "Build and sign a testnet transaction with prompted defaults for the variable fields.",
                "Tests.TestBroadcastTx",
                true,
                hub -> {
                    String secret = hub.promptLine("Secret text", "REPLACE_WITH_YOUR_TEXT");
                    String prevTxId = hub.promptLine("Spendable tx id", "YOUR_SPENDABLE_TX_ID");
                    String prevIndex = hub.promptLine("Spendable output index", "1");
                    String changeBtc = hub.promptLine("Change amount (BTC)", "0.00069");
                    String changeAddress = hub.promptLine("Change address", "YOUR_CHANGE_ADDRESS");
                    String targetAddress = hub.promptLine("Target address", "mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt");
                    String targetBtc = hub.promptLine("Target amount (BTC)", "0.0001");
                    return LaunchSpec.of(List.of(secret, prevTxId, prevIndex, changeBtc, changeAddress, targetAddress, targetBtc));
                }));
        list.add(TestEntry.simple("Coinbase", "Genesis coinbase script and BIP34 height parsing.", "Tests.TestCoinbase"));
        list.add(TestEntry.simple("Finite Field", "Finite field and elliptic curve examples over a small field.", "Tests.TestFF"));
        list.add(TestEntry.simple("Field Math", "Field element arithmetic and integer-point exercises.", "Tests.TestFieldMath"));
        list.add(TestEntry.simple("Merkle", "Merkle tree and merkle block validation vectors.", "Tests.TestMerkle"));
        list.add(TestEntry.simple("Misc", "Small utility experiments used during development.", "Tests.TestMisc"));
        list.add(TestEntry.configurable(
                "Network Headers",
                "Download block headers from a peer and validate continuity and proof-of-work.",
                "Tests.TestNetwork",
                true,
                hub -> {
                    boolean testnet = hub.promptBoolean("Use testnet", false);
                    String host = hub.promptLine("Peer host", testnet ? "testnet.programmingbitcoin.com" : "mainnet.programmingbitcoin.com");
                    String batches = hub.promptLine("Header request batches", "1");
                    return LaunchSpec.of(List.of(host, Boolean.toString(testnet), batches));
                }));
        list.add(TestEntry.simple("P2SH", "P2SH signature and redeem-script verification vectors.", "Tests.TestP2SH"));
        list.add(TestEntry.simple("Script", "Script number encoding and script-evaluation samples.", "Tests.TestScript"));
        list.add(TestEntry.simple("Secp256k1 Suite", "Curve, signature and deterministic-k verification suite.", "Tests.TestSecp256k1"));
        list.add(TestEntry.configurable(
                "Secp256k1 Signing Demo",
                "Run the signing example with a custom secret, message and prefixed k.",
                "Tests.TestSecp256k1",
                false,
                hub -> {
                    String secret = hub.promptLine("Secret text", "secret");
                    String message = hub.promptLine("Message", "Programming Bitcoin!");
                    String prefixedK = hub.promptLine("Prefixed k (decimal or 0x...)", "1234567890");
                    return LaunchSpec.of(List.of("signing", secret, message, prefixedK));
                }));
        list.add(TestEntry.simple("Segwit", "Verify sample segwit transactions fetched from the network.", "Tests.TestSegwit", true));
        list.add(TestEntry.simple("Serialization", "SEC, DER, Base58, WIF, endian and varint vectors.", "Tests.TestSerialization"));
        list.add(TestEntry.simple("Transactions", "Transaction parsing, fee calculation and fetch example.", "Tests.TestTransactions", true));
        list.add(TestEntry.simple("Validation", "Transaction fee, sig-hash and transaction-building checks.", "Tests.TestValidation", true));
        list.add(TestEntry.simple("Wallet", "Wallet/Mnemonic bootstrap smoke test.", "Tests.TestWallet"));
        return Collections.unmodifiableList(list);
    }

    private String promptLine(String label, String defaultValue) throws IOException {
        var raw = readLine(label + " [" + defaultValue + "]");
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        return raw;
    }

    private boolean promptBoolean(String label, boolean defaultValue) throws IOException {
        while (true) {
            String suffix = defaultValue ? "Y/n" : "y/N";
            var raw = readLine(label + " [" + suffix + "]");
            if (raw == null || raw.isBlank()) {
                return defaultValue;
            }
            var normalized = raw.trim();
            if (normalized.equalsIgnoreCase("y") || normalized.equalsIgnoreCase("yes") || normalized.equalsIgnoreCase("true")) {
                return true;
            }
            if (normalized.equalsIgnoreCase("n") || normalized.equalsIgnoreCase("no") || normalized.equalsIgnoreCase("false")) {
                return false;
            }
            System.out.println("Please answer y or n.");
        }
    }

    private String readLine(String prompt) throws IOException {
        System.out.print(prompt + ": ");
        return reader.readLine();
    }

    private void pause() throws IOException {
        System.out.print("Press Enter to return to the menu...");
        reader.readLine();
    }

    private String getJavaBinary() {
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }

    @FunctionalInterface
    private interface Configurator {
        LaunchSpec configure(TestHub hub) throws IOException;
    }

    private static final class LaunchSpec {
        private final List<String> args;
        private final Map<String, String> environment;

        private LaunchSpec(List<String> args, Map<String, String> environment) {
            this.args = args;
            this.environment = environment;
        }

        private static LaunchSpec of(List<String> args) {
            return new LaunchSpec(args, Collections.emptyMap());
        }
    }

    private static final class TestEntry {
        private final String title;
        private final String description;
        private final String className;
        private final boolean network;
        private final boolean configurable;
        private final Configurator configurator;

        private TestEntry(String title, String description, String className, boolean network, boolean configurable, Configurator configurator) {
            this.title = title;
            this.description = description;
            this.className = className;
            this.network = network;
            this.configurable = configurable;
            this.configurator = configurator;
        }

        private static TestEntry simple(String title, String description, String className) {
            return new TestEntry(title, description, className, false, false, hub -> LaunchSpec.of(List.of()));
        }

        private static TestEntry simple(String title, String description, String className, boolean network) {
            return new TestEntry(title, description, className, network, false, hub -> LaunchSpec.of(List.of()));
        }

        private static TestEntry configurable(String title, String description, String className, boolean network, Configurator configurator) {
            return new TestEntry(title, description, className, network, true, configurator);
        }

        private LaunchSpec configure(TestHub hub) throws IOException {
            return configurator.configure(hub);
        }

        private String tags() {
            var tags = new ArrayList<String>();
            if (network) {
                tags.add("network");
            }
            if (configurable) {
                tags.add("config");
            }
            if (tags.isEmpty()) {
                return "";
            }
            return " " + TerminalStyle.dim("[" + String.join(", ", tags) + "]");
        }
    }
}
