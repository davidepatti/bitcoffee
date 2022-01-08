import java.math.BigInteger;

public class bitcoffee {
    public static void main(String[] args) {

        if (args.length==0) {
            System.out.println("Usage bcoffecli command");
            System.exit(-1);
        }

        switch (args[0]) {
            case "sign":
                if (args.length!=3) {
                    System.out.println("Usage sign <message> <secret>");
                    System.exit(-1);
                }
                cmd_sign(args[1],args[2]);
                break;
            case "parseblock":
                if (args.length!=2) {
                    System.out.println("Usage parseblock <serialhex>");
                    System.exit(-1);
                }
                cmd_parsetx(args[1]);
                break;
            case "difficulty":
                if (args.length!=3) {
                    System.out.println("Usage difficulty <startingserialhex> <endingserialhex>");
                    System.exit(-1);
                }
                cmd_difficulty(args[1],args[2]);
                break;
            default:
                System.out.println("Unknown command "+args[0]);
        }


        if (args[0].equals("sign")) {
            cmd_sign(args[1],args[2]);
        }

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
        var time_diff = last_block.getTimestamp()-first_block.getTimestamp();
        System.out.println("Time differential: "+time_diff+", updating bits: "+Kit.bytesToHexString(first_block.getBits()));

        var new_bits = Block.computeNewBits(first_block.getBits(),time_diff);
        System.out.println("New bits: "+Kit.bytesToHexString(new_bits));

    }
}
