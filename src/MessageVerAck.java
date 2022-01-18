
public class MessageVerAck extends Message {

    final static String command = "verack";


    public MessageVerAck() {

    }
    // short constructor for testing purposes

    public static MessageVerAck parse(MessageVerAck va) {

        return va;
    }

    public byte[] serialize() {
        return new byte[0];
    }

    @Override
    public Message parse(byte[] bytes) {
        return null;
    }

    @Override
    public String toString() {
        return "VersionMessage{" + '}';
    }

    @Override
    public String getCommand() {
        return command;
    }
}
