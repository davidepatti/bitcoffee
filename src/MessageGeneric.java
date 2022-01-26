public class MessageGeneric implements Message {

    private byte[] payload;
    public static String COMMAND;


    public String getCommand() {
        return MessageGeneric.COMMAND;
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

}
