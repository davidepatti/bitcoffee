import org.bouncycastle.util.encoders.Hex;

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

    public void addTop(Stack<ScriptCmd> other) {
        this.commands.addAll(other);
    }

    public byte[] raw_serialize() throws IOException {
        var bos = new ByteArrayOutputStream();

        for (ScriptCmd cmd : commands) {
            var len = cmd.value.length;

            if (cmd.type==OpCode.DATA) {
                bos.write((byte)len);
                bos.write(cmd.value);
            }
            else if (cmd.type==OpCode.OP_PUSHDATA1) {
                bos.write((byte)OpCode.OP_PUSHDATA1.getOpcode());
                bos.write((byte)len);
                bos.write(cmd.value);
            }
            else if (cmd.type==OpCode.OP_PUSHDATA2) {
                bos.write((byte)OpCode.OP_PUSHDATA2.getOpcode());
                var len_bytes = CryptoKit.intToLittleEndianBytes(len);
                bos.write(len_bytes,0,2);
                bos.write(cmd.value);
            } // operation, not data
            else {
                bos.write((byte)cmd.type.getOpcode());
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

            // data element
            if (current_byte>=1 && current_byte <=75) {
                var n = current_byte;
                var cmd = new ScriptCmd(OpCode.DATA,bis.readNBytes(n));
                ops_stack.push(cmd);
                System.out.println("Script parse pushing: "+cmd);
                count+=n;
            }
            // OP_PUSHDATA_1 - the next byte indicate how many bytes to read
            else if (current_byte==76) {
                // TODO: why little endian over a single byte?
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(1)).intValue();
                var cmd = new ScriptCmd(OpCode.OP_PUSHDATA1, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                System.out.println("Script parse pushing: "+cmd);
                count+=data_len+1;
            }
            // OP_PUSHDATA_2 - the next two bytes indicate how many bytes to read for the element
            else if (current_byte==77) {
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(2)).intValue();
                var cmd = new ScriptCmd(OpCode.OP_PUSHDATA2, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                System.out.println("Script parse pushing: "+cmd);
                count+=data_len+2;
            }
            // OP_PUSHDATA_4 - the next four bytes indicate how many bytes to read for the element
            else if (current_byte==78) {
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
                var cmd = new ScriptCmd(OpCode.OP_PUSHDATA4, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                System.out.println("Script parse pushing: "+cmd);
                count+=data_len+4;
            }

            else {
                byte[] bytes = new byte[1];
                bytes[0] = (byte)current_byte;
                var cmd = new ScriptCmd(OpCode.fromInt(current_byte), bytes);
                System.out.println("Script parse pushing: "+cmd);
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
        var commands = this.commands;
        var stack = new Stack<ScriptCmd>();
        var altstack = new Stack<ScriptCmd>();

        while (commands.size()>0) {
            var cmd = commands.pop();

            // firstly, if it is data, just move it to the stack

            if (cmd.type==OpCode.DATA) stack.push(cmd);
            else {

                if (cmd.type == OpCode.OP_IF || cmd.type == OpCode.OP_NOTIF ) {

                    // require manipulation of commands using the top of stack
                    assert false;
                }
                else if (cmd.type == OpCode.OP_TOALTSTACK || cmd.type == OpCode.OP_FROMALTSTACK) {
                    // require movement to/from altstack
                    assert false;
                }
                else if (cmd.type.getOpcode() >= 172 && cmd.type.getOpcode() <= 175){
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

    public boolean OP_DUP(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        stack.push(stack.peek());
        return true;
    }

    public boolean OP_HASH256(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        assert element.type==OpCode.DATA;
        var hashed = CryptoKit.hash256(element.value);
        stack.push(new ScriptCmd(element.type,hashed));
        return true;
    }

    public boolean OP_HASH160(Stack<ScriptCmd> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        assert element.type==OpCode.DATA;
        var hashed = CryptoKit.hash160(element.value);
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
            stack.push(new ScriptCmd(OpCode.DATA,new byte[] {0x01}));
        }
        //
        // TODO: implement encode_num(0)
        else stack.push(new ScriptCmd(OpCode.OP_0));

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
