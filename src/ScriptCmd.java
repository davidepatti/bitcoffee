import java.util.Arrays;
import java.util.Objects;

public class ScriptCmd {
    public final byte[] value;
    public final ScriptCmdType type;


    public ScriptCmd(ScriptCmdType type, byte [] value) {
        this.type = type;
        this.value = value;
    }

    public ScriptCmd(ScriptCmdType type) {
        // special operations that need reading the next bytes cannot be
        // initialized with opcode only
        assert type!= ScriptCmdType.DATA;
        assert type!= ScriptCmdType.OP_PUSHDATA1;
        assert type!= ScriptCmdType.OP_PUSHDATA2;
        assert type!= ScriptCmdType.OP_PUSHDATA4;

        this.type = type;
        this.value = new byte[] { (byte)type.getValue() };
    }

    @Override
    public String toString() {
        //return "\n{" + Hex.toHexString(value) + ", " + type + "}";
        return "\n{" + Kit.bytesToHexString(value) + ", " + type + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptCmd scriptCmd = (ScriptCmd) o;
        return Arrays.equals(value, scriptCmd.value) && type == scriptCmd.type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }
}
