import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class TestNetwork {

    public static void main(String[] args) throws IOException {
        System.out.println("-------------------------------------------------------");
        System.out.println(">>Testing NetworkEnvelope");

        var serialized_target = Kit.hexStringToByteArray("f9beb4d976657273696f6e0000000000650000005f1a69d2721101000100000000000000bc8f5e5400000000010000000000000000000000000000000000ffffc61b6409208d010000000000000000000000000000000000ffffcb0071c0208d128035cbc97953f80f2f5361746f7368693a302e392e332fcf05050001");
        var envelope = NetworkEnvelope.parse(serialized_target,false);
        System.out.println(envelope);

        var serialized_bytes = envelope.serialize();
        System.out.println("RESULT: "+ Arrays.equals(serialized_bytes,serialized_target));

        System.out.println("-------------------------------------------------------");
        System.out.println(">>Testing VersionMessage");
        var version_msg = new MessageVersion(0,Kit.hexStringToByteArray("0000000000000000"));

        System.out.println("--> serializing:");
        System.out.println(version_msg);
        serialized_target = Kit.hexStringToByteArray("7f11010000000000000000000000000000000000000000000000000000000000000000000000ffff00000000208d000000000000000000000000000000000000ffff00000000208d0000000000000000182f70726f6772616d6d696e67626974636f696e3a302e312f0000000000");
        serialized_bytes = version_msg.serialize();
        System.out.println("--> Result:");
        System.out.println(Kit.bytesToHexString(serialized_bytes));
        System.out.println("RESULT: "+ Arrays.equals(serialized_bytes,serialized_target));
        System.out.println("-------------------------------------------------------");
        System.out.println(">>Testing Network Connection");

        String host = "testnet.programmingbitcoin.com";
        int port = 18333;

        /*
        System.out.println("CONNECTING TO "+host+" port "+port);
        var socket = new Socket(host,port);
        System.out.println("CONNECTED...");

        var is = socket.getInputStream();
        var os = socket.getOutputStream();
        version_msg = new MessageVersion(0,Kit.hexStringToByteArray("0000000000000000"));
        envelope = new NetworkEnvelope(version_msg.command, version_msg.serialize(), true);

        var env_bytes = envelope.serialize();

        System.out.println("SENDING: ");
        System.out.println(envelope);
        os.write(env_bytes);

        var sc = new Scanner(is);

        while (true) {
            System.out.println("WAITING RESPONSE....");
            while (is.available()==0);
            var rec = is.readAllBytes();

            //var rec = sc.nextLine();
            System.out.println("************ RECEIVED:");
            System.out.println(Kit.bytesToHexString(rec));
            var new_msg = NetworkEnvelope.parse(rec,true);
            System.out.println(new_msg);
        }

         */

        var node = new SimpleNode(host,port,true,false);
        var version = new MessageVersion();
        node.send(version);
        var verack_received = false;
        var version_received = false;


        Set<String> messageSet = new HashSet<>();

        messageSet.add(MessageVersion.command);
        messageSet.add(MessageVerAck.command);

        while (!verack_received && !version_received) {
            var message = node.waitFor(messageSet);

            if (message.getCommand().equals("verack")) {
                verack_received = true;
            }
            else {
                version_received = true;
                node.send(new MessageVerAck());
            }
        }

    }
}
