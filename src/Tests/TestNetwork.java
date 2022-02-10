package Tests;


import bitcoffee.*;

import java.util.Arrays;
import java.util.Objects;

public class TestNetwork {

    public static void main(String[] args) {
        /*\
        Tests.Test.__BEGIN_NOTES("Testing bitcoffee.NetworkEnvelope");

        var serialized_target = bitcoffee.Kit.hexStringToByteArray("f9beb4d976657273696f6e0000000000650000005f1a69d2721101000100000000000000bc8f5e5400000000010000000000000000000000000000000000ffffc61b6409208d010000000000000000000000000000000000ffffcb0071c0208d128035cbc97953f80f2f5361746f7368693a302e392e332fcf05050001");
        var envelope = bitcoffee.NetworkEnvelope.parse(serialized_target,false);
        System.out.println(envelope);
        var serialized_bytes = envelope.serialize();
        System.out.println("RESULT: "+ Arrays.equals(serialized_bytes,serialized_target));
        Tests.Test.__END_NOTES();

        Tests.Test.__BEGIN_NOTES("Testing VersionMessage");
        var version_msg = new bitcoffee.MessageVersion(0,bitcoffee.Kit.hexStringToByteArray("0000000000000000"));
        System.out.println("--> serializing:");
        System.out.println(version_msg);
        serialized_target = bitcoffee.Kit.hexStringToByteArray("7f11010000000000000000000000000000000000000000000000000000000000000000000000ffff00000000208d000000000000000000000000000000000000ffff00000000208d0000000000000000182f70726f6772616d6d696e67626974636f696e3a302e312f0000000000");
        serialized_bytes = version_msg.serialize();
        System.out.println("--> Result:");
        System.out.println(bitcoffee.Kit.bytesToHexString(serialized_bytes));
        System.out.println("RESULT: "+ Arrays.equals(serialized_bytes,serialized_target));
        Tests.Test.__END_NOTES();


        Tests.Test.__BEGIN_NOTES("Network Connection");

        String host = "testnet.programmingbitcoin.com";
        int port = 18333;

        var node = new bitcoffee.SimpleNode(host,port,true,false);
        var version = new bitcoffee.MessageVersion();
        node.send(version);
        var verack_received = false;
        var version_received = false;


        Set<String> messageSet = new HashSet<>();

        messageSet.add(bitcoffee.MessageVersion.command);
        messageSet.add(bitcoffee.MessageVerAck.command);

        while (!verack_received && !version_received) {
            var message = node.waitFor(messageSet);

            if (message.getCommand().equals("verack")) {
                System.out.println("Received verack");
                verack_received = true;
            }
            else {
                version_received = true;
                System.out.println("Received version message, sending verack");
                node.send(new bitcoffee.MessageVerAck());
            }
        }

        Tests.Test.__END_NOTES();
         */

        Test.__BEGIN_TEST("Get Headers");

        /*
        String header = "0000000000000000001237f46acddf58578a37e213d2a6edc4884a2fcad05ba3";
        String target_serial = "7f11010001a35bd0ca2f4a88c4eda6d213e2378a5758dfcd6af437120000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        var msg = new bitcoffee.MessageGetHeaders(header);

        var result_serial = bitcoffee.Kit.bytesToHexString(msg.serialize());

        Tests.Test.check("getheaders serial","header:"+header,target_serial,result_serial);

         */

        Test.__BEGIN_TEST("Downloading headers and checking PoW");

        var genesis = Block.parseSerial(Kit.hexStringToByteArray(Block.GENESIS_BLOCK));
        Block previous = genesis;
        var first_epoch_timestamp = Objects.requireNonNull(previous).getTimestamp();
        var expected_bits = Kit.hexStringToByteArray(Block.LOWEST_BITS);
        System.out.println("Starting from block "+genesis);

        int count = 1;
        var node2 = new SimpleNode("mainnet.programmingbitcoin.com",false);
        node2.Handshake();

        for (int i=0;i<1;i++) {
            var get_header_msg = new MessageGetHeaders(previous.getHashHexString());
            node2.send(get_header_msg);
            MessageHeaders headers = (MessageHeaders) node2.waitFor("headers");

            for (Block h:headers.getBlocks()) {

                System.out.println("Checking block: "+h.getHashHexString());

                if (!h.checkPoW()) {
                    System.out.println(h);
                    throw new RuntimeException("^^^^^^Bad PoW!");
                }

                if (!Arrays.equals(h.getPrev_block(),previous.hash())) {
                    throw new RuntimeException("Discontinuoss");
                }

                if (count%2016==0) {
                    var time_diff = previous.getTimestamp()-first_epoch_timestamp;
                    var computed_bits = Block.computeNewBits(previous.getBits(),time_diff);
                    System.out.println("Expected bits: "+ Kit.bytesToHexString(computed_bits));
                    first_epoch_timestamp = h.getTimestamp();
                }

                if (!Arrays.equals(h.getBits(),expected_bits)) {
                    throw new RuntimeException("Bad bits at block"+count);
                }

                previous = h;
                count++;
            }

        }
        Test.__END_TEST();
    }
}
