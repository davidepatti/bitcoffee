package bitcoffee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class MessagePing implements Message {

    private final long nonce;
    public static final String COMMAND = "ping";

    public MessagePing(long nonce ) {
        this.nonce = nonce;
    }

    @Override
    public String getCommand() {
        return MessagePing.COMMAND;
    }

    @Override
    public byte[] getPayload() {
        return this.serialize();
    }

    private byte[] serialize() {
        return BigInteger.valueOf(nonce).toByteArray();
    }

    public static MessagePing parse(byte[] bytes) {
        var bis = new ByteArrayInputStream(bytes);

        try {
            var n = new BigInteger(bis.readNBytes(8));
            return new MessagePing(n.longValue());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return null;
    }

}
