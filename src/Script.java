import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Stack;

public class Script {
    Stack<ScriptCmd> commands;

    public Script(Stack<ScriptCmd> stack) {
        this.commands = stack;
    }

    // used for encoding stack nums
    public static byte[] encodeNum(BigInteger n) {
        // TOOD: check if better empty or null
        byte[] res = null;
        var bos = new ByteArrayOutputStream();

        // empty byte if 0
        if (n.compareTo(BigInteger.ZERO)==0)
            return res;

        var abs_n = n.abs();
        boolean negative = n.compareTo(BigInteger.ZERO) <0;

        // we revert the bytes of the number (should be little endian)
        while (abs_n.compareTo(BigInteger.ZERO)!=0) {
            var absn_bytes =abs_n.toByteArray();
            bos.write(absn_bytes[absn_bytes.length-1]);
            abs_n = abs_n.shiftRight(8);
        }

        res = bos.toByteArray();

        // we must restore the sign, if necessary....
        // if the most significant byte (rightmost, since is little endian)
        // is like 1xxxxxxx we must be sure that the 1 is not considered as sign
        if ((res[res.length-1] & 0x80)!=0) {
            // we add a further 1xxxxxxxx byte on the right, to encode the sign
            if (negative)
                bos.write(0x80);
            else
                bos.write(0);
            res = bos.toByteArray();
        } // there was not 1xxxxxxxxx, so we can just add the 1 for the required sign
        else if (negative) {
            res[res.length-1] |= 0x80;
        }
        return res;
    }

    public static byte[] encodeNum(long n) {
        return encodeNum(BigInteger.valueOf(n));
    }

    public static BigInteger decodeNum(byte[] element) {
        BigInteger res = null;
        if (element==null) return BigInteger.ZERO;


        return res;
    }

    public void addTop(Stack<ScriptCmd> other) {
        this.commands.addAll(other);
    }

    public byte[] raw_serialize() throws IOException {
        var bos = new ByteArrayOutputStream();

        for (ScriptCmd cmd : commands) {
            var len = cmd.value.length;

            if (cmd.type== ScriptCmdType.DATA) {
                bos.write((byte)len);
                bos.write(cmd.value);
            }
            else if (cmd.type== ScriptCmdType.OP_PUSHDATA1) {
                bos.write((byte) ScriptCmdType.OP_PUSHDATA1.getValue());
                bos.write((byte)len);
                bos.write(cmd.value);
            }
            else if (cmd.type== ScriptCmdType.OP_PUSHDATA2) {
                bos.write((byte) ScriptCmdType.OP_PUSHDATA2.getValue());
                var len_bytes = CryptoKit.intToLittleEndianBytes(len);
                bos.write(len_bytes,0,2);
                bos.write(cmd.value);
            } // operation, not data
            else {
                bos.write((byte)cmd.type.getValue());
                bos.write(cmd.value);
            }

        }
        return bos.toByteArray();
    }

    public byte[] serialize() {
        var bos = new ByteArrayOutputStream();
        try {
            var result = this.raw_serialize();
            var len = result.length;
            var len_bytes = CryptoKit.encodeVarint(len);
            bos.write(len_bytes);
            bos.write(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public static Script parse(byte[] serial) throws IOException {
        Stack<ScriptCmd> ops_stack = new Stack<>();
        var bis = new ByteArrayInputStream(serial);
        var len = CryptoKit.readVarint(bis);
        int count =0;
        while (count < len) {
            var current_byte = bis.read();
            count++;

            // if byte is between 0x01 e 0x4b it indicates the number of bytes
            // to read the data element
            if (current_byte>=1 && current_byte <=75) {
                var n = current_byte;
                var cmd = new ScriptCmd(ScriptCmdType.DATA,bis.readNBytes(n));
                ops_stack.push(cmd);
                System.out.println("Script parsing found element data: "+cmd);
                count+=n;
            }
            // OP_PUSHDATA_1 - the next byte indicate how many bytes to read
            else if (current_byte==76) {
                // TODO: why little endian over a single byte?
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(1)).intValue();
                var cmd = new ScriptCmd(ScriptCmdType.OP_PUSHDATA1, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                System.out.println("Script parse operation: "+cmd);
                count+=data_len+1;
            }
            // OP_PUSHDATA_2 - the next two bytes indicate how many bytes to read for the element
            else if (current_byte==77) {
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(2)).intValue();
                var cmd = new ScriptCmd(ScriptCmdType.OP_PUSHDATA2, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                System.out.println("Script parse operation: "+cmd);
                count+=data_len+2;
            }
            // OP_PUSHDATA_4 - the next four bytes indicate how many bytes to read for the element
            else if (current_byte==78) {
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
                var cmd = new ScriptCmd(ScriptCmdType.OP_PUSHDATA4, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                System.out.println("Script parse operation: "+cmd);
                count+=data_len+4;
            }

            else {
                byte[] bytes = new byte[1];
                bytes[0] = (byte)current_byte;
                var cmd = new ScriptCmd(ScriptCmdType.fromInt(current_byte), bytes);
                System.out.println("Script parse operation: "+cmd);
                ops_stack.push(cmd);
            }
        }
            try {
                if (count!=len)
                    throw new Exception("Script parsin error");
            } catch (Exception e) {
                e.printStackTrace();
            }

        return new Script(ops_stack);
    }

    public boolean evaluate(byte[] z) {
        // TODO: check if would be better to make it immutable
        // TODO: decode/encode_num for OP_0 OP_16 and many others, as in:
        // https://github.com/jimmysong/programmingbitcoin/blob/master/code-ch06/op.py
        var cmds = new Stack<ScriptCmd>();
        cmds.addAll(this.commands); // make a copy

        var stack = new Stack<ScriptCmd>();
        var altstack = new Stack<ScriptCmd>();

        while (cmds.size()>0) {
            var cmd = cmds.pop();

            // firstly, if it is data, just move it to the stack

            if (cmd.type== ScriptCmdType.DATA)
                stack.push(cmd);
            else { // not data element

                if (cmd.type == ScriptCmdType.OP_IF || cmd.type == ScriptCmdType.OP_NOTIF ) {

                    // require manipulation of commands using the top of stack
                    assert false;
                }
                else if (cmd.type == ScriptCmdType.OP_TOALTSTACK || cmd.type == ScriptCmdType.OP_FROMALTSTACK) {
                    // require movement to/from altstack
                    assert false;
                }
                else if (cmd.type.getValue() >= 172 && cmd.type.getValue() <= 175){
                    // require signature (CHECKSIG etc..)
                    switch (cmd.type) {
                        case OP_CHECKSIG:
                            this.OP_CHECKSIG(stack,z);
                            break;
                        default:
                            assert false;
                    }
                }
                else {

                    switch (cmd.type) {
                        case OP_HASH160:
                            this.OP_HASH160(stack);
                            break;
                        default:
                            assert false;
                    }
                    // require only stack
                }
            }

        }
        if (stack.size()==0) return false;
        if (stack.pop().value[0] == 0) return false;

        return true;
    }

    public boolean OP_0(Stack<ScriptCmd> stack) {
        var cmd = new ScriptCmd(ScriptCmdType.OP_0,this.encodeNum(0));
        stack.push(cmd);
        return true;
    }
    public boolean OP_1(Stack<ScriptCmd> stack) {
        var cmd = new ScriptCmd(ScriptCmdType.OP_1,this.encodeNum(1));
        stack.push(cmd);
        return true;
    }
    public boolean OP_1NEGATE(Stack<ScriptCmd> stack) {
        var cmd = new ScriptCmd(ScriptCmdType.OP_1NEGATE,this.encodeNum(-1));
        stack.push(cmd);
        return true;
    }
    // TODO add the other OP_N *********************************

    public boolean OP_IF(Stack<ScriptCmd> stack) {
        // TODO: implement
        if (stack.size()<1) return false;

        return true;
    }

    public boolean OP_VERIFY(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        if (decodeNum(element.value).compareTo(BigInteger.ZERO)==0) {
            return false;
        }
        return true;
    }
    public boolean OP_RETURN(Stack<ScriptCmd> stack) {
        return false;
    }

    public boolean OP_TOALTSTACK(Stack<ScriptCmd> stack, Stack<ScriptCmd> altstack) {
        if (stack.size()<1) return false;
        altstack.push(stack.pop());
        return true;
    }

    public boolean OP_FROMALTSTACK(Stack<ScriptCmd> stack, Stack<ScriptCmd> altstack) {
        if (stack.size()<1) return false;
        stack.push(altstack.pop());
        return true;
    }

    public boolean OP_EQUAL(Stack<ScriptCmd> stack) {
        if (stack.size()<2) return false;
        var e1 = stack.pop();
        var e2 = stack.pop();

        if (e1.equals(e2)) {
            stack.push(new ScriptCmd(ScriptCmdType.DATA, encodeNum(0)));
        }
            else {
                stack.push(new ScriptCmd(ScriptCmdType.DATA, encodeNum(1)));
        }
        return true;
    }

    public boolean OP_EQUALVERIFY(Stack<ScriptCmd> stack) {
        return OP_EQUAL(stack) && OP_VERIFY(stack);
    }

    public boolean OP_ADD(Stack<ScriptCmd> stack) {
        if (stack.size()<2) return false;

        var e1 = decodeNum(stack.pop().value);
        var e2 = decodeNum(stack.pop().value);

        stack.push(new ScriptCmd(ScriptCmdType.DATA,encodeNum(e1.add(e2))));
        return true;
    }

    public boolean OP_SUB(Stack<ScriptCmd> stack) {
        if (stack.size()<2) return false;

        var e1 = decodeNum(stack.pop().value);
        var e2 = decodeNum(stack.pop().value);

        stack.push(new ScriptCmd(ScriptCmdType.DATA,encodeNum(e2.subtract(e1))));
        return true;
    }

    public boolean OP_MUL(Stack<ScriptCmd> stack) {
        if (stack.size()<2) return false;

        var e1 = decodeNum(stack.pop().value);
        var e2 = decodeNum(stack.pop().value);

        stack.push(new ScriptCmd(ScriptCmdType.DATA,encodeNum(e2.multiply(e1))));
        return true;
    }

    public boolean OP_RIPEMD160(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();

        stack.push(new ScriptCmd(element.type,CryptoKit.RIPEMD160(element.value)));
        return true;
    }

    public boolean OP_SHA256(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();

        stack.push(new ScriptCmd(element.type,CryptoKit.sha256(element.value)));
        return true;
    }

    public boolean OP_HASH160(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        assert element.type== ScriptCmdType.DATA;
        var hashed = CryptoKit.hash160(element.value);
        stack.push(new ScriptCmd(element.type,hashed));
        return true;
    }

    public boolean OP_DUP(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        stack.push(stack.peek());
        return true;
    }

    public boolean OP_HASH256(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        assert element.type== ScriptCmdType.DATA;
        var hashed = CryptoKit.hash256(element.value);
        stack.push(new ScriptCmd(element.type,hashed));
        return true;
    }


    public boolean OP_CHECKSIG(Stack<ScriptCmd> stack, byte[] z) {
        if (stack.size()<2) return false;

        var sec_pubkey_cmd = stack.pop();
        var der_signature = stack.pop();

        // take off the last byte of the signature as that's the hash_type
        // see: https://en.bitcoin.it/wiki/OP_CHECKSIG
        var der_bytes = Arrays.copyOf(der_signature.value,der_signature.value.length-1);
        var point = S256Point.parseSEC(sec_pubkey_cmd.value);
        // 1) check parse der signature
        // 2) check
        var sig = Signature.parse(der_bytes);

        if (point.verify(new BigInteger(z),sig)) {
            // TODO: implement encode_num(1)
            stack.push(new ScriptCmd(ScriptCmdType.DATA,new byte[] {0x01}));
        }
        //
        // TODO: implement encode_num(0)
        else stack.push(new ScriptCmd(ScriptCmdType.OP_0));

        return false;
    }

    @Override
    public String toString() {
        String out = "Script{";
        for (ScriptCmd cmd: this.commands) out = out+cmd;

        out+="}\n";

        return out;

    }
}
