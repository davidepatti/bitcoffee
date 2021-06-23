enum OpCode {
    DATA(-1), OP_PUSHDATA1(76),OP_PUSHDATA2(77),OP_PUSHDATA4(78),
    OP_0(0x00),
    OP_1NEGATE(0x4f),
    OP_TRUE(0x51),
    OP_1(0x51),
    OP_2(0x51+1),
    OP_3(0x51+2),
    OP_4(0x51+3),
    OP_5(0x51+4),
    OP_6(0x51+5),
    OP_7(0x51+6),
    OP_8(0x51+7),
    OP_9(0x51+8),
    OP_10(0x51+9),
    OP_11(0x51+10),
    OP_12(0x51+11),
    OP_13(0x51+12),
    OP_14(0x51+13),
    OP_15(0x51+14),
    OP_16(0x51+15),
    OP_DUP(0x76),
    OP_ADD(0x93),
    OP_SHA256(170),
    OP_HASH160(0xa9),
    OP_CHECKSIG(0xac);

    private int opcode;

    private OpCode(int code) {
        this.opcode = code;
    }

    public static OpCode fromInt(int code ) {

        try {
            for (OpCode op : OpCode.values()) {
                if (op.opcode == code ) return op;
            }
            throw new Exception("Cannot find Opcode "+code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getOpcode() {
        return opcode;
    }
}
