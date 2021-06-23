import org.bouncycastle.util.encoders.Hex;

public class ScriptCmd {
    public final byte[] value;
    public final OpCode type;


    public ScriptCmd(OpCode type, byte [] value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "ScriptCmd{" + Hex.toHexString(value) + ", " + type + '}';
    }
}
