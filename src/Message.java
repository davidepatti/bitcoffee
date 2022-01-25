public class Message {

    protected String command;
    protected byte[] payload;

    public String getCommand() {
        return command;
    }

    public byte[] serialize() {
        return payload;
    }
    public Message parse(byte[] bytes) {
        return new Message("UNSET_COMMAND",bytes);
    }

    public Message(String command, byte[] payload) {
        this.command = command;
        this.payload = payload;
    }

    public Message() {

    }
}
