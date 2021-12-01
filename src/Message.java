import java.io.ByteArrayInputStream;

public abstract class Message {
    public byte[] command;

    public abstract byte[] serialize();
    public abstract Message parse(byte[] bytes);
    @Override
    public abstract String toString();
}
