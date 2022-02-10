package bitcoffee;

public interface Message {
    public String getCommand();
    public byte[] getPayload();

}
