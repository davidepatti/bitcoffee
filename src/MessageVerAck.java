
public class MessageVerAck implements Message {

    public static final String COMMAND = "verack";

    public MessageVerAck() {

    }

    @Override
    public String getCommand() {
        return MessageVerAck.COMMAND;
    }

    @Override
    public byte[] getPayload() {
        return this.serialize();
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
