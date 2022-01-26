import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class bitcoffee {
    public static void main(String[] args) {

        if (args.length==0) {
            System.out.println("Usage bcoffecli command");
            System.exit(-1);
        }

        switch (args[0]) {
            case "sign":
                if (args.length!=3) {
                    System.out.println("Usage: bitcoffee sign <message> <secret>");
                    System.exit(-1);
                }
                cmd_sign(args[1],args[2]);
                break;
            case "parseblock":
                if (args.length!=2) {
                    System.out.println("Usage: bitcoffee parseblock <serialhex>");
                    System.exit(-1);
                }
                cmd_parsetx(args[1]);
                break;
            case "getp2pkaddr":
                if (args.length!=3) {
                    System.out.println("Usage: bitcoffee getp2pkaddr <secret> [testnet]");
                    System.exit(-1);
                }
                if (args[2].equals("testnet"))
                    cmd_getp2pkaddr(args[1],true);
                else
                    cmd_getp2pkaddr(args[1],false);
                break;
            case "difficulty":
                if (args.length!=3) {
                    System.out.println("Usage: difficulty <starting_serial_block_rawhex> <ending_serial_block_rawhex>");
                    System.exit(-1);
                }
                cmd_difficulty(args[1],args[2]);
                break;
            case "createtx":
                cmd_createTx();
                break;

            case "checktx":
                if (args.length!=2) {
                    System.out.println("Usage: checktx <rawtx>");
                    System.exit(-1);
                }
                cmd_checkTx(args[1]);
                break;
            case "verify":
                if (args.length!=4) {
                    System.out.println("Usage: verify <SEC> <DER> <Z>");
                    cmd_verifySignature(args[1],args[2],args[3]);
                    System.exit(-1);
                }
                cmd_checkTx(args[1]);
                break;

            case "help":
                cmd_help();
                break;

            default:
                System.out.println("Unknown command "+args[0]);
        }
    }

    private static void cmd_help() {
        System.out.println("sign");
            System.out.println("\tUsage sign <message> <secret>");
        System.out.println("parseblock");
            System.out.println("\tUsage parseblock <serialhex>");
        System.out.println("getp2pkaddr");
            System.out.println("\tUsage getp2pkaddr <secret> mainnet/testnet");
        System.out.println("difficulty");
            System.out.println("\tUsage difficulty <startingserialhex> <endingserialhex>");
        System.out.println("createtx");
        System.out.println("\tInteractively create a TX for broadcasting");
        System.out.println("checktx");
        System.out.println("\tCheck the validity of a raw transaction encoding");
    }

    private static void cmd_sign(String secret,String message) {
        var secret_bytes = Kit.hash256(secret);
        var secret_num = new BigInteger(1,secret_bytes);
        var msg_bytes = Kit.hash256(message);
        var msg_num = new BigInteger(1,msg_bytes);
        System.out.println("Signing (secret:"+secret+" message:"+message+")");

        System.out.println("secret hash: "+secret_num.toString(16));
        System.out.println("msg    hash: "+msg_num.toString(16));
        var pk = new PrivateKey(secret_bytes);
        var signature = pk.signDeterminisk(msg_bytes);
        System.out.println("signature: "+signature.toString());
    }

    private static void cmd_parsetx(String block_raw) {
        System.out.println("Parsing raw block: "+block_raw);

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
        System.out.println("block hash: "+block.getHashHexString());
        System.out.println("difficulty: "+block.difficulty());

        System.out.println();
    }

    private static void cmd_difficulty(String start_block, String end_block) {

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
        var time_diff = last_block.getTimestamp()-first_block.getTimestamp();
        System.out.println("Time differential: "+time_diff+", updating bits: "+Kit.bytesToHexString(first_block.getBits()));

        var new_bits = Block.computeNewBits(first_block.getBits(),time_diff);
        System.out.println("New bits: "+Kit.bytesToHexString(new_bits));

    }

    private static void cmd_getp2pkaddr(String secret, boolean testnet) {
        // brainwallet style, use text to derive private key (be careful to not share it!)
        var secret_bytes = Kit.hash256(secret);
        var mypk = new PrivateKey(secret_bytes);

        var myaddress = mypk.point.getP2pkhTestnetAddress();
        if (testnet)
            System.out.println("Testnet address for secret:" + secret);
        else
            System.out.println("Mainnet address for secret:" + secret);

        System.out.println("address: " + myaddress);
        var wif = mypk.getWIF(true, true);
        System.out.println("Use this WIF to import the private key into a wallet: " + wif);
        System.out.println("---------------------------------------------");

        System.out.println("---------------------------------------------");
    }

    private static void cmd_checkTx(String raw_tx) {

        System.out.println("--------------------------------------------------");
        var tx = Tx.parse(Kit.hexStringToByteArray(raw_tx),false);
        System.out.println(">> Checking fee for tx id:"+tx.getId());
        var fee = tx.calculateFee();
        if (fee>=0)
            System.out.println("Valid transactions fees:"+fee);
        else
            System.out.println("ERROR: not valid transaction fees:"+fee);
        System.out.println("--------------------------------------------------");
    }

    private static void cmd_createTx() {
        System.out.println("Tx creation - WARNING: experimental, testnet only!");
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
        var tx_in = new TxIn(prev_tx,prev_index,script_null);

        System.out.print("Specify change amount (in BTC):");
        double btc_change_amount;
        while (!sc.hasNextDouble());
        btc_change_amount = sc.nextDouble();
        sc.nextLine();

        //var btc_change_amount = 0.00069;

        // must multiply to express it in sats
        var change_amount = (int)(btc_change_amount*100000000);
        //var change_address = "mnwUykq9XxccVMuXgwrA97gSxYxFs4vNRW";
        System.out.print("Enter the change address:");
        var change_address = sc.nextLine();
        var change_h160 = Kit.decodeBase58(change_address);

        // not modify, creating script from the address above
        var change_script = Script.h160ToP2pkh(change_h160);
        var change_script_bytes = change_script.getBytes();
        var change_output = new TxOut(change_amount,change_script_bytes);

        System.out.print("Insert target address:");
        var target_address = sc.nextLine();
        // DEFAULT: send a tx to give back to faucet https://testnet-faucet.mempool.co/
        //var target_address = "mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt";
        System.out.print("Insert BTC amount:");
        var target_btc_amount = sc.nextDouble();
        //var target_btc_amount = 0.0001;
        var target_amount = (int)(target_btc_amount*100000000);

        var target_h160 = Kit.decodeBase58(target_address);
        var target_script = Script.h160ToP2pkh(target_h160);
        var target_output = new TxOut(target_amount,target_script.getBytes());

        var tx_ins = new ArrayList<TxIn>();
        var tx_outs = new ArrayList<TxOut>();
        tx_outs.add(change_output);
        tx_outs.add(target_output);
        tx_ins.add(tx_in);
        var tx_obj = new Tx(1,tx_ins,tx_outs,0,true);

        // creating the scriptsig to unlock the previous output
        var input_index = 0;
        var z = tx_obj.getSigHash(input_index);
        var der = mypk.signDeterminisk(z).DER();
        // DER + SIGHASH_ALL
        var sig = Kit.hexStringToByteArray(der+"01");
        var sec = Kit.hexStringToByteArray(mypk.point.SEC33());
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmdType.DATA,sec));
        cmds.push(new ScriptCmd(ScriptCmdType.DATA,sig));
        var scriptsig = new Script(cmds);
        ////////////////// end script

        var txins = tx_obj.getTxIns();
        // a new txin must be created to adde the signature by replacing the empty script one (input_index)
        var new_txin = new TxIn(txins.get(input_index).getPrevTxId(),txins.get(input_index).getPrevIndex(),scriptsig.getBytes());
        txins.set(input_index,new_txin);
        var newtx = new Tx(tx_obj.getVersion(),tx_ins,tx_obj.getTxOuts(),tx_obj.getLocktime(),tx_obj.isTestnet());

        System.out.println("Created tx with content:");
        System.out.println(newtx);
        System.out.println("Fees:"+newtx.calculateFee());
        System.out.println("Checking validity:" +newtx.verify());
        System.out.println(">>>>>> PLEASE USE THE RAW TEXT BELOW TO BROADCAST TX: ");
        System.out.println("-------------------------------------BEGIN-------------------------------------");
        System.out.println(newtx.getSerialString());
        System.out.println("-------------------------------------END-------------------------------------");

    }

    private static void cmd_verifySignature(String sec, String der, String z) {
        var z_num = new BigInteger(z,16);
        var point = S256Point.parseSEC(sec);
        var signature = Signature.parse(Kit.hexStringToByteArray(der));
        System.out.println(" >> Verify signature test: "+point.verify(z_num,signature));
        System.out.println("--------------------------------------------------");
    }
}
