import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class MessageVersion extends Message {
    final private int version;
    final private long services;
    final private long timestamp;
    final private long receiver_services;
    final private byte[] receiver_ip;
    final private int receiver_port;
    final private long sender_services;
    final private byte[] sender_ip;
    final private int sender_port;
    final private byte[] nonce;
    final private byte[] user_agent;
    final private int latest_block;
    final private boolean relay;
    final static String command = "version";


    public MessageVersion() {

        var timestamp = Instant.now().getEpochSecond();
        var rand = new Random();
        var nonce = Kit.intToLittleEndianBytes(rand.nextLong());

        this.version = 70015;
        this.services = 0;
        this.timestamp = timestamp;
        this.receiver_services = 0;
        this.receiver_ip = Kit.hexStringToByteArray("00000000");
        this.receiver_port = 8333;
        this.sender_services = 0;
        this.sender_ip = Kit.hexStringToByteArray("00000000");
        this.sender_port = 8333;
        this.nonce = nonce;
        //this.user_agent = Kit.stringToBytes("/programmingbitcoin:0.1/");
        this.user_agent = Kit.asciiStringToBytes("/bitcoffee:0.1/");
        this.latest_block = 0;
        this.relay = false;

    }
    // short constructor for testing purposes
    public MessageVersion(long timestamp, byte[] nonce) {

        this.version = 70015;
        this.services = 0;
        this.timestamp = timestamp;
        this.receiver_services = 0;
        this.receiver_ip = Kit.hexStringToByteArray("00000000");
        this.receiver_port = 8333;
        this.sender_services = 0;
        this.sender_ip = Kit.hexStringToByteArray("00000000");
        this.sender_port = 8333;
        this.nonce = nonce;
        this.user_agent = Kit.asciiStringToBytes("/programmingbitcoin:0.1/");
        this.latest_block = 0;
        this.relay = false;
    }

    public MessageVersion(int version, long services, long timestamp, long receiver_services, byte[] receiver_ip, int receiver_port, long sender_services, byte[] sender_ip, int sender_port, byte[] nonce, byte[] user_agent, int latest_block, boolean relay) {
        this.version = version;
        this.services = services;

        if (timestamp<=0)
            this.timestamp = Instant.now().getEpochSecond();
        else
            this.timestamp = timestamp;

        this.receiver_services = receiver_services;
        this.receiver_ip = receiver_ip;
        this.receiver_port = receiver_port;
        this.sender_services = sender_services;
        this.sender_ip = sender_ip;
        this.sender_port = sender_port;
        this.nonce = nonce;
        this.user_agent = user_agent;
        this.latest_block = latest_block;
        this.relay = relay;
    }

    @Override
    public byte[] serialize() {
        var bos = new ByteArrayOutputStream();

        try {
            bos.write(Kit.intToLittleEndianBytes(this.version),0,4);
            bos.write(Kit.intToLittleEndianBytes(this.services),0,8);
            bos.write(Kit.intToLittleEndianBytes(this.timestamp),0,8);
            bos.write(Kit.intToLittleEndianBytes(this.receiver_services),0,8);
            // assuming ipv4
            bos.write(Kit.hexStringToByteArray("00000000000000000000ffff"));
            bos.write(this.receiver_ip);

            if (this.receiver_port==8333)
                bos.write(Kit.hexStringToByteArray("208d"));
            else {
                System.out.println("ERROR: unsupported receiver port" +this.receiver_port);
            }

            bos.write(Kit.intToLittleEndianBytes(this.sender_services),0,8);
            bos.write(Kit.hexStringToByteArray("00000000000000000000ffff"));
            bos.write(this.sender_ip);
            if (this.sender_port==8333)
                bos.write(Kit.hexStringToByteArray("208d"));
            else {
                System.out.println("ERROR: unsupported sender port" +this.sender_port);
            }

            bos.write(nonce);
            bos.write(Objects.requireNonNull(Kit.encodeVarint(user_agent.length)));
            bos.write(user_agent);

            bos.write(Kit.intToLittleEndianBytes(latest_block),0,4);

            if (this.relay)
                bos.write(0x01);
            else
                bos.write(0x00);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();

    }

    @Override
    public String toString() {
        return "VersionMessage{" +
                "version=" + version +
                ", services=" + services +
                ", timestamp=" + timestamp +
                ", receiver_services=" + receiver_services +
                ", receiver_ip=" + Arrays.toString(receiver_ip) +
                ", receiver_port=" + receiver_port +
                ", sender_services=" + sender_services +
                ", sender_ip=" + Arrays.toString(sender_ip) +
                ", sender_port=" + sender_port +
                ", nonce=" + Arrays.toString(nonce) +
                ", user_agent=" + Arrays.toString(user_agent) +
                ", latest_block=" + latest_block +
                ", relay=" + relay +
                '}';
    }

    @Override
    public Message parse(byte[] bytes) {
        return null;
    }

    @Override
    public String getCommand() {
        return command;
    }
}
