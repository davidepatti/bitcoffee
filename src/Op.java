import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Op {


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
}
