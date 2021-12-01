
public class VerAckMessage extends Message {


    public VerAckMessage() {

        this.command = Kit.stringToBytes("verack");
    }
    // short constructor for testing purposes

    public static VerAckMessage parse(VerAckMessage va) {

        return va;
    }

    public byte[] serialize() {
        return null;
    }

    @Override
    public Message parse(byte[] bytes) {
        return null;
    }

    @Override
    public String toString() {
        return "VersionMessage{" + '}';
    }
}
