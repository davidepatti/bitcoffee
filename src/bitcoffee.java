import java.math.BigInteger;

public class bitcoffee {
    public static void main(String[] args) {
        if (args.length!=3) {
            System.out.println("Usage: bitcoffee sign \"secret\" \"message\"");
            System.exit(-1);
        }

        if (args[0].equals("sign")) {
            var secret = args[1];
            var message = args[2];
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

    }
}
