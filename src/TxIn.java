import java.io.ByteArrayInputStream;

public class TxIn {
    String prev_tx;
    int prev_index;
    boolean script_sig;
    int sequence;

    public TxIn(String prev_tx, int prev_index, boolean script_sig, int sequence) {
        this.prev_tx = prev_tx;
        this.prev_index = prev_index;
        this.script_sig = script_sig;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "TxIn{" +
                "prev_tx='" + prev_tx + '\'' +
                ", prev_index=" + prev_index +
                ", script_sig=" + script_sig +
                ", sequence=" + sequence +
                '}';
    }


    // parses the stream to create a TxIn instance
    public static TxIn parse(ByteArrayInputStream bis) {

        return (TxIn) null;
    }


}
