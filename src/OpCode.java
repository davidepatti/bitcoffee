enum OpCode {
    // constants
    DATA(-1), OP_PUSHDATA1(76), OP_PUSHDATA2(77), OP_PUSHDATA4(78),
    OP_0(0x00),
    OP_1NEGATE(0x4f),
    OP_TRUE(0x51),
    OP_1(0x51),
    OP_2(0x51 + 1),
    OP_3(0x51 + 2),
    OP_4(0x51 + 3),
    OP_5(0x51 + 4),
    OP_6(0x51 + 5),
    OP_7(0x51 + 6),
    OP_8(0x51 + 7),
    OP_9(0x51 + 8),
    OP_10(0x51 + 9),
    OP_11(0x51 + 10),
    OP_12(0x51 + 11),
    OP_13(0x51 + 12),
    OP_14(0x51 + 13),
    OP_15(0x51 + 14),
    OP_16(0x51 + 15),

    // flow control
    OP_NOP(0x61),
    OP_IF(0x63),
    OP_NOTIF(0x64),
    OP_ELSE(0x67),
    OP_ENDIF(0x68),
    OP_VERIFY(0x69),
    OP_RETURN(0x6a),

    // stack
    OP_TOALTSTACK(0x6b),
    OP_FROMALTSTACK(0x6c),
    OP_IFDUP(0x73),
    OP_DEPTH(0x74),
    OP_DROP(0x75),
    OP_DUP(0x76),
    OP_NIP(0x77),
    OP_OVER(0x78),
    OP_PICK(0x79),
    OP_ROLL(0x7a),
    OP_ROT(0x7b),
    OP_SWAP(0x7c),
    OP_TUCK(0x7d),
    OP_2DROP(0x6d),
    OP_2DUP(0x6e),
    OP_3DUP(0x6f),
    OP_2OVER(0x70),
    OP_2ROT(0x71),
    OP_2SWAP(0x72),

    OP_EQUAL(0x87),
    OP_EQUALVERIFY(0x88),

    OP_1ADD(0x8b),
    OP_1SUB(0x8c),
    OP_NEGATE(0x8f),
    OP_ABS(0x90),
    OP_NOT(0x91),
    OP_0NOTEQUAL(0x92),
    OP_ADD(0x93),
    OP_SUB(0x94),
    OP_BOOLAND(0x9a),
    OP_BOOLOR(0x9b), // TODO: complete....

    // crypto
    OP_RIPEMB160(0xa6),
    OP_SHA1(0xa7),
    OP_SHA256(0xa8),
    OP_HASH160(0xa9),
    OP_HASH256(0xaa),
    OP_CODESEPARATOR(0xab),
    OP_CHECKSIG(0xac),
    OP_CHECKSIGVERIFY(0xad),
    OP_CHECKMULTISIG(0xae),
    OP_CHECKMULTISIGVERIFY(0xaf),

    OP_CHECKLOCKTIMEVERIFY(0xb1),
    OP_CHECKSEQUENCEVERIFY(0xb2),

    OP_PUBKEYHASH(0xfd),
    OP_PUBKEY(0xfe),
    OP_INVALIDOPCODE(0xff);

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
