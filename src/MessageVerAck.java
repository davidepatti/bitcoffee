
public class MessageVerAck extends Message {

    public static final String COMMAND = "verack";

    public MessageVerAck() {

        command = COMMAND;
    }
    // short constructor for testing purposes

    public static MessageVerAck parse(MessageVerAck va) {

        return va;
    }

    public byte[] serialize() {
        return new byte[0];
    }


    @Override
    public String toString() {
        return "VersionMessage{" + '}';
    }

}
