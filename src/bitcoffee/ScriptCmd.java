package bitcoffee;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

public class ScriptCmd {
    public final byte[] value;
    public final Type type;


    public ScriptCmd(Type type, byte [] value) {
        this.type = type;
        this.value = value;
    }

    public ScriptCmd(Type type) {
        // special operations that need reading the next bytes cannot be
        // initialized with opcode only
        assert type!= Type.DATA;
        assert type!= Type.OP_PUSHDATA1;
        assert type!= Type.OP_PUSHDATA2;
        assert type!= Type.OP_PUSHDATA4;

        this.type = type;
        this.value = new byte[] { (byte)type.getValue() };
    }

    public enum Type {
        // constants
        DATA(-1),  // values from 1 to 75, indicating how many next bytes to read as data
        OP_PUSHDATA1(76), OP_PUSHDATA2(77), OP_PUSHDATA4(78),
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
        OP_MUL(0x95),
        OP_BOOLAND(0x9a),
        OP_BOOLOR(0x9b), // TODO: complete....

        // crypto
        OP_RIPEMD160(0xa6),
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

        private final int value;

        Type(int code) {
            this.value = code;
        }

        public static Type fromInt(int code ) {

            try {
                for (Type op : Type.values()) {
                    if (op.value == code ) return op;
                }
                System.out.println("**************WARNING: UNKNOWN OPCODE "+code);
                return OP_INVALIDOPCODE;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public int getValue() {
            return value;
        }
    }

    /*************************************************************************/
    public static void OP_0(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(0));
    }

    public static void OP_1(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(1));
    }

    public static void OP_2(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(2));
    }

    public static void OP_3(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(3));
    }

    public static void OP_4(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(4));
    }

    public static void OP_5(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(5));
    }

    public static void OP_6(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(6));
    }

    public static void OP_7(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(7));
    }

    public static void OP_8(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(8));
    }

    public static void OP_9(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(9));
    }

    public static void OP_10(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(10));
    }

    public static void OP_11(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(11));
    }

    public static void OP_12(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(12));
    }

    public static void OP_13(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(13));
    }

    public static void OP_14(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(14));
    }

    public static void OP_15(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(15));
    }

    public static void OP_16(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(16));
    }

    public static boolean OP_VERIFY(Stack<byte[]> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        return (Script.decodeNum(element).compareTo(BigInteger.ZERO)) != 0;
    }

    public static void OP_TOALTSTACK(Stack<byte[]> stack, Stack<byte[]> altstack) {
        if (stack.size()<1) return;
        altstack.push(stack.pop());
    }

    public static void OP_FROMALTSTACK(Stack<byte[]> stack, Stack<byte[]> altstack) {
        if (stack.size()<1) return;
        stack.push(altstack.pop());
    }

    public static void OP_2DUP(Stack<byte[]> stack) {
        if (stack.size()<2) return;
        var top1 = stack.peek();
        var top2 = stack.elementAt(stack.size()-2);
        stack.push(top2);
        stack.push(top1);
    }

    public static boolean OP_EQUAL(Stack<byte[]> stack) {
        if (stack.size()<2) return false;
        var e1 = stack.pop();
        var e2 = stack.pop();



        if (Arrays.equals(e1,e2)) {
            stack.push(Script.encodeNum(1));
        }
            else {
                stack.push(Script.encodeNum(0));
        }
        return true;
    }

    public static void OP_EQUALVERIFY(Stack<byte[]> stack) {
        if (OP_EQUAL(stack)) {
            OP_VERIFY(stack);
        }
    }

    public static void OP_NOT(Stack<byte[]> stack) {
        if (stack.size()<1) return;

        var element = Script.decodeNum(stack.pop());
        if (element.equals(BigInteger.ZERO))
            stack.push(Script.encodeNum(1));
        else
            stack.push(Script.encodeNum(0));

    }

    public static void OP_ADD(Stack<byte[]> stack) {
        if (stack.size()<2) return;

        var e1 = Script.decodeNum(stack.pop());
        var e2 = Script.decodeNum(stack.pop());

        stack.push(Script.encodeNum(e1.add(e2)));
    }

    public static void OP_SUB(Stack<byte[]> stack) {
        if (stack.size()<2) return;

        var e1 = Script.decodeNum(stack.pop());
        var e2 = Script.decodeNum(stack.pop());

        stack.push(Script.encodeNum(e2.subtract(e1)));
    }

    public static void OP_MUL(Stack<byte[]> stack) {
        if (stack.size()<2) return;

        var e1 = Script.decodeNum(stack.pop());
        var e2 = Script.decodeNum(stack.pop());

        stack.push(Script.encodeNum(e2.multiply(e1)));
    }

    public static void OP_RIPEMD160(Stack<byte[]> stack) {
        if (stack.size()<1) return;
        var element = stack.pop();

        stack.push(Kit.RIPEMD160(element));
    }

    public static void OP_SHA256(Stack<byte[]> stack) {
        if (stack.size()<1) return;
        var element = stack.pop();

        stack.push(Kit.sha256(element));
    }

    public static boolean OP_HASH160(Stack<byte[]> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        var hashed = Kit.hash160(element);
        stack.push(hashed);
        return true;
    }

    public static void OP_DUP(Stack<byte[]> stack) {
        if (stack.size()<1) return;
        stack.push(stack.peek());
    }

    public static void OP_HASH256(Stack<byte[]> stack) {
        if (stack.size()<1) return;
        var element = stack.pop();
        var hashed = Kit.hash256(element);
        stack.push(hashed);
    }

    public static void OP_CHECKSIG(Stack<byte[]> stack, byte[] z) {
        if (stack.size()<2) return;

        var sec_pubkey_cmd = stack.pop();
        var der_signature = stack.pop();

        // take off the last byte of the signature as that's the hash_type
        // see: https://en.bitcoin.it/wiki/OP_CHECKSIG
        var der_bytes = Arrays.copyOf(der_signature,der_signature.length-1);
        var point = S256Point.parseSEC(sec_pubkey_cmd);
        // 1) check parse der signature
        // 2) check
        var sig = Signature.parse(der_bytes);

        var z_pos = new BigInteger(1,z);

        if (point.verify(z_pos,sig)) {
            stack.push(Script.encodeNum(1));
        }
        //
        // TODO: implement encode_num(0)
        else stack.push(Script.encodeNum(0));

    }

    /*************************************************************************/
    public static void OP_CHECKMULTISIG(Stack<byte[]> stack, byte[] z) {

        if (stack.size()<2) return;

        var n = Script.decodeNum(stack.pop());
        if (stack.size() < n.longValue()+1)
            return;

        var sec_pubkeys = new ArrayList<byte[]>();

        for (int i =0;i<n.longValue();i++) {
            sec_pubkeys.add(stack.pop());
        }

        var m = Script.decodeNum(stack.pop());
        if (stack.size() < m.longValue()+1)
            return;

        var der_signatures = new ArrayList<byte[]>();

        for (int i=0;i<m.longValue();i++) {
            var sig_with_SIGHASH_ALL = stack.pop();
            var sig = Arrays.copyOfRange(sig_with_SIGHASH_ALL,0,sig_with_SIGHASH_ALL.length-1);
            der_signatures.add(sig);
        }

        // due to the off-by-one OP_CHECKMULTISIG bug
        stack.pop();

        var points = new ArrayList<S256Point>();

        for (byte[] sec: sec_pubkeys) {
            points.add(S256Point.parseSEC(sec));
        }

        var sigs = new ArrayList<Signature>();

        for (byte[] sig: der_signatures) {
            sigs.add(Signature.parse(sig));
        }

        for (Signature sig: sigs) {
            if (points.size()==0) return;

            for (S256Point p: points) {

                if (p.verify(new BigInteger(1,z),sig)) {
                    points.remove(p);
                    break;
                }
            }
        }

        stack.push(Script.encodeNum(1));
    }

    public static boolean OP_1NEGATE(Stack<byte[]> stack) {
        stack.push(Script.encodeNum(-1));
        return true;
    }

    public static boolean OP_IF(Stack<byte[]> stack) {
        // TODO: implement
        return stack.size() >= 1;
    }

    public static void OP_RETURN(Stack<byte[]> stack) {
    }

    @Override
    public String toString() {
        //return "\n{" + Hex.toHexString(value) + ", " + type + "}";
        if (type== Type.DATA) return "\n{"+ Kit.bytesToHexString(value)+"}";
        else return "\n{"+type+"}";
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
