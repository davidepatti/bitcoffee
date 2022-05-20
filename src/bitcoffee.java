import java.math.BigInteger;
import java.util.*;

import bitcoffee.*;

public class bitcoffee {

    private static final String[] CMDS = { "help","sign","parseblock","getp2pkaddr","difficulty","createtx","checktx","verify","gettx","fetchtx"};
    public static final String VERSION = "v0.1";

    public static void main(String[] args) {

        System.out.println("----------------------------------------------------");
        System.out.println(" bitcoffee - from scratch java Bitcoin client "+VERSION);
        System.out.println("----------------------------------------------------");
        System.out.println(" by xedivad@gmail.com ");
        System.out.println("----------------------------------------------------");

        if (args.length == 0 || ! Arrays.asList(CMDS).contains(args[0])) {
            System.out.println("Usage: bitcoffee COMMAND [ARGS]");
            System.out.println("see bitcoffee help for help");
            System.exit(-1);
        }

        switch (args[0]) {
            case "sign":
                if (args.length != 3) {
                    System.out.println("Usage: bitcoffee sign <message> <secret>");
                    System.exit(-1);
                }
                cmd_sign(args[1], args[2]);
                break;
            case "parseblock":
                if (args.length != 2) {
                    System.out.println("Usage: bitcoffee parseblock <serialhex>");
                    System.exit(-1);
                }
                cmd_parseblock(args[1]);
                break;
            case "getp2pkaddr":
                if (args.length != 3) {
                    System.out.println("Usage: bitcoffee getp2pkaddr <secret> [testnet]");
                    System.exit(-1);
                }
                cmd_getp2pkaddr(args[1],args[2].equals("testnet"));
                break;
            case "difficulty":
                if (args.length != 3) {
                    System.out.println("Usage: bitcoffee difficulty <starting_serial_block_rawhex> <ending_serial_block_rawhex>");
                    System.exit(-1);
                }
                cmd_diffadj(args[1], args[2]);
                break;
            case "createtx":
                cmd_createTx();
                break;

            case "checktx":
                if (args.length != 2) {
                    System.out.println("Usage: bitcoffee checktx <rawtx>");
                    System.exit(-1);
                }
                cmd_checkTx(args[1]);
                break;
            case "verify":
                if (args.length != 4) {
                    System.out.println("Usage: bitcoffee verify <SEC> <DER> <Z>");
                    System.exit(-1);
                }
                cmd_verifySignature(args[1], args[2], args[3]);
                break;

            case "gettx":
                if (args.length != 5) {
                    System.out.println("Usage: bitcoffee gettx <host> <lasttx> <address> <testnet>");
                    System.exit(-1);
                }
                cmd_gettx(args[1], args[2], args[3], args[4].equals("testnet"));
                break;
            case "fetchtx":
                if (args.length != 2 && args.length!=3) {
                    System.out.println("Wrong number of args:"+args.length);
                    System.out.println("Usage: bitcoffee fetchtx <txid> <testnet>");
                    System.exit(-1);
                }

                if (args.length==3)
                    cmd_fetchtx(args[1], args[2].equals("testnet"));
                else
                    cmd_fetchtx(args[1],false);
                break;

            case "help":
                cmd_help();
                break;

            default:
                System.out.println("Unknown command " + args[0]);
        }
    }

    private static void cmd_help() {
        System.out.println("List of available commands:");
        for (String s:CMDS) System.out.println(s);
        System.out.println("type bitcoffee COMMAND to get usage instructions");
    }

    private static void cmd_sign(String secret, String message) {
        // input: any two strings are valid
        var secret_bytes = Kit.hash256(secret);
        var secret_num = new BigInteger(1, secret_bytes);
        var msg_bytes = Kit.hash256(message);
        var msg_num = new BigInteger(1, msg_bytes);
        System.out.println("Signing (secret:" + secret + " message:" + message + ")");

        System.out.println("secret hash: " + secret_num.toString(16));
        System.out.println("msg    hash: " + msg_num.toString(16));
        var pk = new PrivateKey(secret_bytes);
        var signature = pk.signDeterminisk(msg_bytes);
        System.out.println("signature: " + signature.toString());
    }

    private static void cmd_parseblock(String block_raw) {
        // input: a string representing a block serialization in hex
        //block_raw = "020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d";
        System.out.println("Parsing raw block: " + block_raw);

        var block = Block.parseSerial(Kit.hexStringToByteArray(block_raw));
        System.out.println(block);
        System.out.println("------------------------------------------------");
        System.out.println(" details");
        System.out.println("------------------------------------------------");
        System.out.print("BIP: ");
        assert block != null;
        if (block.checkBIP9()) System.out.println("BIP9");
        if (block.checkBIP91()) System.out.println("BIP91");
        if (block.checkBIP141()) System.out.println("BIP91");
        System.out.println("block hash: " + block.getHashHexString());
        System.out.println("difficulty: " + block.difficulty());

        System.out.println();
    }

    private static void cmd_diffadj(String start_block, String end_block) {
        // input: two string representing the hex serial of the initial and final block to
        // calculate the difficulty adjustment
        //end_block = "000000203471101bbda3fe307664b3283a9ef0e97d9a38a7eacd8800000000000000000010c8aba8479bbaa5e0848152fd3c2289ca50e1c3e58c9a4faaafbdf5803c5448ddb845597e8b0118e43a81d3";
        //start_block = "02000020f1472d9db4b563c35f97c428ac903f23b7fc055d1cfc26000000000000000000b3f449fcbe1bc4cfbcb8283a0d2c037f961a3fdf2b8bedc144973735eea707e1264258597e8b0118e5f00474";

        var first_block = Block.parseSerial(Kit.hexStringToByteArray(start_block));
        var last_block = Block.parseSerial(Kit.hexStringToByteArray(end_block));
        System.out.println("Computing difficulty adjustment...");

        System.out.println("First block:");
        System.out.println("------------------------------------------------");
        System.out.println(first_block);
        System.out.println("Last block:");
        System.out.println("------------------------------------------------");
        System.out.println(last_block);
        assert last_block != null;
        assert first_block != null;
        var time_diff = last_block.getTimestamp() - first_block.getTimestamp();
        System.out.println("Time differential: " + time_diff + ", updating bits: " + Kit.bytesToHexString(first_block.getBits()));

        var new_bits = Block.computeNewBits(first_block.getBits(), time_diff);
        System.out.println("New bits: " + Kit.bytesToHexString(new_bits));

    }

    private static void cmd_getp2pkaddr(String secret, boolean testnet) {
        // input: a secret string
        // brainwallet style, use string text to derive private key (be careful to not share it!)
        var secret_bytes = Kit.hash256(secret);
        var mypk = new PrivateKey(secret_bytes);

        String myaddress;
        if (testnet) {
            System.out.println("Testnet address for secret:" + secret);
             myaddress = mypk.point.getP2pkhTestnetAddress();
        }
        else {
            myaddress = mypk.point.getP2pkhAddress(true);
            System.out.println("Mainnet address for secret:" + secret);
        }

        System.out.println("address: " + myaddress);
        var wif = mypk.getWIF(true, testnet);
        System.out.println("Use this WIF to import the private key into a wallet: " + wif);
        System.out.println("---------------------------------------------");

        System.out.println("---------------------------------------------");
    }

    private static void cmd_checkTx(String raw_tx) {
        // input: a string representing the hex of a serialized tx
        // for examples, fetch from online services e copy the resulting content:
        // https://blockstream.info/api/tx/716373514d1442f6e7f71719965936fc8df12fe581f5d4fb3a3fd038cbbe4f4c/hex

        System.out.println("--------------------------------------------------");
        var tx = Tx.parse(Kit.hexStringToByteArray(raw_tx), false);
        System.out.println(">> Checking fee for tx id:" + tx.getId());
        var fee = tx.calculateFee();
        if (fee >= 0)
            System.out.println("Valid transactions fees:" + fee);
        else
            System.out.println("ERROR: not valid transaction fees:" + fee);
        System.out.println("--------------------------------------------------");
    }

    private static void cmd_createTx() {
        System.out.println("bitcoffee.Tx creation - WARNING: experimental, testnet only!");
        System.out.println("------------------------------------------------------------------");
        System.out.print("Insert the source address:");
        String myad;
        var sc = new Scanner(System.in);
        var myaddress = sc.nextLine();
        System.out.print("Insert the secret (brainwallet) used to create the private key:");
        var secret_text = sc.nextLine();
        var secret_bytes = Kit.hash256(secret_text);
        var mypk = new PrivateKey(secret_bytes);

        System.out.print("Enter the previous tx id containing the UTXO:");
        var prev_tx_id = sc.nextLine();

        //var prev_tx_id = "1818136d9d0ca83c369a70c41fd2b5d25e286895e358a0bcd872c17534846659";
        var prev_tx = Kit.hexStringToByteArray(prev_tx_id);
        // replace with your prev index
        System.out.print("Insert the index of the UTXO to be spent:");
        var prev_index = sc.nextInt();
        // leave the script below empty
        byte[] script_null = {};
        var tx_in = new TxIn(prev_tx, prev_index, script_null);

        System.out.print("Specify change amount (in BTC):");
        double btc_change_amount;
        while (!sc.hasNextDouble()) ;
        btc_change_amount = sc.nextDouble();
        sc.nextLine();

        //var btc_change_amount = 0.00069;

        // must multiply to express it in sats
        var change_amount = (int) (btc_change_amount * 100000000);
        //var change_address = "mnwUykq9XxccVMuXgwrA97gSxYxFs4vNRW";
        System.out.print("Enter the change address:");
        var change_address = sc.nextLine();
        var change_h160 = Kit.decodeBase58(change_address);

        // not modify, creating script from the address above
        var change_script = new P2PKHScriptPubKey(change_h160);
        var change_script_bytes = change_script.rawSerialize();
        var change_output = new TxOut(change_amount, change_script_bytes);

        System.out.print("Insert target address:");
        var target_address = sc.nextLine();
        // DEFAULT: send a tx to give back to faucet https://testnet-faucet.mempool.co/
        //var target_address = "mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt";
        System.out.print("Insert BTC amount:");
        var target_btc_amount = sc.nextDouble();
        //var target_btc_amount = 0.0001;
        var target_amount = (int) (target_btc_amount * 100000000);

        var target_h160 = Kit.decodeBase58(target_address);
        var target_script = new P2PKHScriptPubKey(target_h160);
        var target_output = new TxOut(target_amount, target_script.rawSerialize());

        var tx_ins = new ArrayList<TxIn>();
        var tx_outs = new ArrayList<TxOut>();
        tx_outs.add(change_output);
        tx_outs.add(target_output);
        tx_ins.add(tx_in);
        var tx_obj = new Tx(1, tx_ins, tx_outs, 0, true);

        // creating the scriptsig to unlock the previous output
        var input_index = 0;
        var z = tx_obj.getSigHash(input_index);
        var der = mypk.signDeterminisk(z).DER();
        // DER + SIGHASH_ALL
        var sig = Kit.hexStringToByteArray(der + "01");
        var sec = Kit.hexStringToByteArray(mypk.point.SEC33());
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmd.Type.DATA, sec));
        cmds.push(new ScriptCmd(ScriptCmd.Type.DATA, sig));
        var scriptsig = new Script(cmds);
        ////////////////// end script

        var txins = tx_obj.getTxIns();
        // a new txin must be created to adde the signature by replacing the empty script one (input_index)
        var new_txin = new TxIn(txins.get(input_index).getPrevTxId(), txins.get(input_index).getPrevIndex(), scriptsig.rawSerialize());
        txins.set(input_index, new_txin);
        var newtx = new Tx(tx_obj.getVersion(), tx_ins, tx_obj.getTxOuts(), tx_obj.getLocktime(), tx_obj.isTestnet());

        System.out.println("Created tx with content:");
        System.out.println(newtx);
        System.out.println("Fees:" + newtx.calculateFee());
        System.out.println("Checking validity:" + newtx.verify());
        System.out.println(">>>>>> PLEASE USE THE RAW TEXT BELOW TO BROADCAST TX: ");
        System.out.println("-------------------------------------BEGIN-------------------------------------");
        System.out.println(newtx.getSerialString());
        System.out.println("-------------------------------------END-------------------------------------");

    }

    private static void cmd_verifySignature(String sec, String der, String z) {
        // inputs: hex strings representing sec,der,z
        //sec = "0349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a";
        //der = "3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed";
        //z = "27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6";

        var z_num = new BigInteger(z, 16);
        var point = S256Point.parseSEC(sec);
        var signature = Signature.parse(Kit.hexStringToByteArray(der));
        System.out.println(" >> Verify signature test: " + point.verify(z_num, signature));
        System.out.println("--------------------------------------------------");
    }

    private static void cmd_gettx(String host, String last_block, String address, boolean testnet) {
        // example for tesnet
        //last_block = "00000000000538d5c2246336644f9a4956551afb44ba47278759ec55ea912e19";
        //address = "mwJn1YPMq7y5F8J3LkC5Hxg9PHyZ5K4cFv";
        //host = "testnet.programmingbitcoin.com";
        //testnet = true;

        // example for mainnet
        //last_block = "0000000000000000000838497f627c016c2bb9097d6794c6aeac1a581bd26984";
        //address = "3Ffi6K7abWQsVMXUQuUNGviNAghXrY9Bni";
        //host = "mainnet.programmingbitcoin.com";
        //testnet = false;

        var h160 = Kit.decodeBase58(address);

        var node = new SimpleNode(host, testnet);
        var bf = new BloomFilter(30, 5, 90210);
        // add the address above to the filter
        bf.add(h160);

        node.Handshake();
        node.send(bf.filterLoad());

        // ask for the block headers starting from the last block specified
        var getheaders_msg = new MessageGetHeaders(last_block);
        node.send(getheaders_msg);

        var headers_msg = (MessageHeaders) node.waitFor(MessageHeaders.COMMAND);
        var getdata_msg = new MessageGetData();

        for (Block b : headers_msg.getBlocks()) {
            if (!b.checkPoW()) {
                throw new RuntimeException("Not valid PoW");
            }
            getdata_msg.addData(MessageGetData.FILTERED_BLOCK_DATA_TYPE, b.getHashHexString());
        }

        node.send(getdata_msg);

        boolean found = false;

        var msg_to_wait = new HashSet<String>();
        msg_to_wait.add(MerkleBlock.COMMAND);
        msg_to_wait.add(Tx.COMMAND);

        while (!found) {

            var msg = node.waitFor(msg_to_wait);

            if (msg != null) {

                if (msg.getCommand().equals("merkleblock")) {

                    if (!((MerkleBlock) msg).isValid())
                        throw new RuntimeException("Not valid Merkle proof");
                    else System.out.println("Received valid Merkle block");
                } else {
                    var receveived_tx = (Tx) msg;
                    for (TxOut tout : receveived_tx.getTxOuts()) {
                        if (ScriptPubKey.parse(tout.getScriptPubkeyBytes()).getAddress(testnet).equals(address)) {
                            System.out.println("Found address " + address + " in tx id: " + receveived_tx.getId());
                            found = true;
                        }
                    }
                }
            }
        } // while
    }

    private static void cmd_fetchtx(String txid, boolean testnet) {
        // inputs: the transaction id and the whether is testnet or mainnet
        // look at TestSegwit.java for example inputs
        var tx = TxFetcher.fetch(txid,testnet,true);
        System.out.println(tx);

        System.out.println("--------------------------------------------");
        System.out.println("Verifying transaction:");
        System.out.println("--------------------------------------------");
        if (tx.verify()) System.out.println("--> Transaction confirmed as valid");
        else System.out.println("--> Transaction is NOT valid!");

    }
}


