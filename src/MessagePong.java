import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class MessagePong extends Message {

    private final long nonce;
    public static final String COMMAND = "pong";

    public MessagePong(long nonce ) {
        command = COMMAND;
        this.nonce = nonce;
    }

    @Override
    public byte[] serialize() {
        return BigInteger.valueOf(nonce).toByteArray();
    }

    @Override
    public Message parse(byte[] bytes) {
        var bis = new ByteArrayInputStream(bytes);

        try {
            var n = new BigInteger(bis.readNBytes(8));
            return new MessagePong(n.longValue());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public String getCommand() {
        return command;
    }
}
