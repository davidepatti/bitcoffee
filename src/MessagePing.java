import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class MessagePing extends Message {

    private final long nonce;
    final static String command = "ping";

    public MessagePing(long nonce ) {
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

    @Override
    public String getCommand() {
        return command;
    }
}
