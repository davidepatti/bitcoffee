public abstract class Message {

    public abstract String getCommand();

    public abstract byte[] serialize();
    public abstract Message parse(byte[] bytes);
    @Override
    public abstract String toString();
}
