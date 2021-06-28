import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;

public class Script {
    Stack<ScriptCmd> stack;

    public Script(Stack<ScriptCmd> stack) {
        this.stack = stack;
    }

    public byte[] raw_serialize() throws IOException {
        var bos = new ByteArrayOutputStream();

        for (ScriptCmd cmd : stack) {
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
    public boolean OP_DUP() {
        if (this.stack.size()<1) return false;
        this.stack.push(this.stack.peek());
        return true;
    }

    public boolean OP_HASH256() {
        if (this.stack.size()<1) return false;
        var element = this.stack.pop();
        var hashed = CryptoKit.hash256(element.value);
        stack.push(new ScriptCmd(element.type,hashed));
        return true;
    }

    public boolean OP_HASH160() {
        if (this.stack.size()<1) return false;
        var element = this.stack.pop();
        var hashed = CryptoKit.hash160(element.value);
        stack.push(new ScriptCmd(element.type,hashed));
        return true;

    }
}
