package bitcoffee;

public class MessageGeneric implements Message {

    private byte[] payload;
    private String COMMAND;


    public String getCommand() {
        return COMMAND;
    }

    public byte[] getPayload() {
        return payload;
    }

    public static MessageGeneric parse(byte[] bytes) {
        return new MessageGeneric("UNDEF",bytes);
    }

    public MessageGeneric(String command, byte[] payload) {
        this.COMMAND = command;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "MessageGeneric{" + "payload=" + Kit.bytesToHexString(payload) + ", COMMAND='" + COMMAND + '\'' + '}';
    }
}
