package bitcoffee;

public interface Message {
    String getCommand();
    byte[] getPayload();

}
