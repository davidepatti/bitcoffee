import org.bouncycastle.util.encoders.Hex;

import java.util.Stack;

public class ScriptCmd {
    public final byte[] value;
    public final OpCode type;


    public ScriptCmd(OpCode type, byte [] value) {
        this.type = type;
        this.value = value;
    }

    public ScriptCmd(OpCode type) {
        // special operations that need reading the next bytes cannot be
        // initialized with opcode only
        assert type!=OpCode.DATA;
        assert type!=OpCode.OP_PUSHDATA1;
        assert type!=OpCode.OP_PUSHDATA2;
        assert type!=OpCode.OP_PUSHDATA4;

        this.type = type;
        this.value = new byte[] { (byte)type.getOpcode() };
    }

    @Override
    public String toString() {
        return "\n{" + Hex.toHexString(value) + ", " + type + "}";
    }

}
