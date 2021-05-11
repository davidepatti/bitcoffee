public class bitcoffee {
    public static void main(String args[]) {
        if (args.length!=3) {
            System.out.println("Usage: bitcoffee sign \"secret\" \"message\"");
            System.exit(-1);
        }

        if (args[0].equals("sign")) {
            var secret = args[1];
            var message = args[2];
            var h_secret = Secp256k1.hash256(secret);
            var h_message = Secp256k1.hash256(message);
            System.out.println("Signing (secret:"+secret+" message:"+message+")");

            /*
            System.out.println("secret hash: "+h_secret.toString(16));
            System.out.println("msg    hash: "+h_message.toString(16));
            var pk = new PrivateKey(h_secret);
            var signature = pk.sign_determinisk(h_message);
            System.out.println("signature: "+signature.toString());
             */
        }

    }
}
