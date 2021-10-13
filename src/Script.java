import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Script {
    final Stack<ScriptCmd> commands;

    public Script(Stack<ScriptCmd> stack) {
        if (stack==null)
            commands = new Stack<ScriptCmd>();
        else
            this.commands = stack;
    }

    /*************************************************************************/
    // used for encoding stack nums
    public static byte[] encodeNum(BigInteger n) {
        // TOOD: check if better empty or null
        byte[] res = null;
        var bos = new ByteArrayOutputStream();

        // empty byte if 0
        if (n.compareTo(BigInteger.ZERO)==0)
            return null;

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

    /*************************************************************************/
    public static byte[] encodeNum(long n) {
        return encodeNum(BigInteger.valueOf(n));
    }

    /*************************************************************************/
    public static BigInteger decodeNum(byte[] element) {
        BigInteger res;
        boolean negative;
        if (element==null)
            return BigInteger.ZERO;
        var big_endian = CryptoKit.reverseBytes(element);

        if ((big_endian[0] & 0x80) !=0 ) {
            negative = true;
            res = BigInteger.valueOf(big_endian[0] & 0x7f);
        }
        else {
            negative = false;
            res = BigInteger.valueOf((int)(big_endian[0]));
        }

        for (int i=1; i<big_endian.length;i++) {
            res = res.shiftLeft(8);
            res = res.add(BigInteger.valueOf(big_endian[i]));
        }

        if (negative)
            return res.negate();

        return res;
    }

    /***************************************************************************/
    // Convert the 20 bytes hash in a ScriptPubKey
    public static Script hash160ToP2pkh(byte[] h160) {
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmdType.OP_CHECKSIG));
        cmds.push(new ScriptCmd(ScriptCmdType.OP_EQUALVERIFY));
        cmds.push(new ScriptCmd(ScriptCmdType.DATA,h160));
        cmds.push(new ScriptCmd(ScriptCmdType.OP_HASH160));
        cmds.push(new ScriptCmd(ScriptCmdType.OP_DUP));

        return new Script(cmds);
    }
    /*************************************************************************/
    public static Script hash160ToP2psh(byte[] h160) {
        var cmds = new Stack<ScriptCmd>();
        cmds.push(new ScriptCmd(ScriptCmdType.OP_EQUAL));
        cmds.push(new ScriptCmd(ScriptCmdType.DATA,h160));
        cmds.push(new ScriptCmd(ScriptCmdType.OP_HASH160));

        return new Script(cmds);
    }
    /*************************************************************************/
    // check for the pattern: OP_DUP OP_HASH160 <20 byte hash> OP_EQUALVERIFY OP_CHECKSIG
    public boolean isP2pkh() {
        return (this.commands.size()==5
                && commands.elementAt(0).type == ScriptCmdType.OP_CHECKSIG
                && commands.elementAt(1).type == ScriptCmdType.OP_EQUALVERIFY
                && commands.elementAt(2).type == ScriptCmdType.DATA
                && commands.elementAt(2).value.length == 20
                && commands.elementAt(3).type == ScriptCmdType.OP_HASH160
                && commands.elementAt(4).type == ScriptCmdType.OP_DUP);
    }

    /*************************************************************************/
    // check for the pattern: OP_HASH160 <20 byte hash> OP_EQUAL
    public boolean isP2sh() {
        return (this.commands.size()==3
                && commands.elementAt(0).type == ScriptCmdType.OP_EQUAL
                && commands.elementAt(1).type == ScriptCmdType.DATA
                && commands.elementAt(1).value.length == 20
                && commands.elementAt(2).type == ScriptCmdType.OP_HASH160);
    }


    /*************************************************************************/
    public void addTop(Stack<ScriptCmd> other) {
        this.commands.addAll(other);
    }

    public void addTop(Script other_script) {
        this.commands.addAll(other_script.commands);
    }
    /*************************************************************************/
    // bytes encoding ops, without length prefix (required in serialization)
    public byte[] getBytes() {
        try {
            return this.raw_serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*************************************************************************/
    private byte[] raw_serialize() throws IOException {
        var bos = new ByteArrayOutputStream();

        var copy_cmd = new Stack<ScriptCmd>();

        copy_cmd.addAll(commands);

        if (copy_cmd.empty()) {
            return new byte[]{};
        }


        while (!copy_cmd.empty()) {
            var cmd = copy_cmd.pop();
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
                //bos.write(cmd.value);
            }

        }
        return bos.toByteArray();
    }

    /*************************************************************************/
    public byte[] serialize() {
        var bos = new ByteArrayOutputStream();
        try {
            var result = this.raw_serialize();
            var len = result.length;
            var len_bytes = CryptoKit.encodeVarint(len);
            // serialization starts with the number script bytes that follows
            assert len_bytes != null;
            bos.write(len_bytes);
            bos.write(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    /*************************************************************************/
    public static Script parseSerial(byte[] serial) throws IOException {
        Stack<ScriptCmd> ops_stack = new Stack<>();
        var bis = new ByteArrayInputStream(serial);
        var hex = CryptoKit.bytesToHexString(serial);
        //System.out.println("DEBUG: Parsing script hex:"+hex);
        var len = CryptoKit.readVarint(bis);

        int count =0;
        while (count < len) {
            var current_byte = bis.read();
            count++;

            // if byte is between 0x01 e 0x4b it indicates the number of bytes
            // to read the data element
            if (current_byte>=1 && current_byte <=75) {
                var cmd = new ScriptCmd(ScriptCmdType.DATA,bis.readNBytes(current_byte));
                ops_stack.push(cmd);
                //System.out.println("DEBUG: Script parsing found element data: "+cmd);
                count+= current_byte;
            }
            // OP_PUSHDATA_1 - the next byte indicate how many bytes to read
            else if (current_byte==76) {
                // TODO: why little endian over a single byte?
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(1)).intValue();
                var cmd = new ScriptCmd(ScriptCmdType.OP_PUSHDATA1, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                //System.out.println("DEBUG: Script parse operation: "+cmd);
                count+=data_len+1;
            }
            // OP_PUSHDATA_2 - the next two bytes indicate how many bytes to read for the element
            else if (current_byte==77) {
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(2)).intValue();
                var cmd = new ScriptCmd(ScriptCmdType.OP_PUSHDATA2, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                // System.out.println("DEBUG: Script parse operation: "+cmd);
                count+=data_len+2;
            }
            // OP_PUSHDATA_4 - the next four bytes indicate how many bytes to read for the element
            else if (current_byte==78) {
                var data_len = CryptoKit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
                var cmd = new ScriptCmd(ScriptCmdType.OP_PUSHDATA4, bis.readNBytes(data_len));
                ops_stack.push(cmd);
                // System.out.println("DEBUG: Script parse operation: "+cmd);
                count+=data_len+4;
            }

            else {
                byte[] bytes = new byte[1];
                bytes[0] = (byte)current_byte;
                var cmd = new ScriptCmd(ScriptCmdType.fromInt(current_byte), bytes);
                //System.out.println("DEBUG: Script parse operation: "+cmd);
                ops_stack.push(cmd);
            }
        }
            try {
                if (count!=len)
                    throw new Exception("Script parsing error: Wrong length (count="+count+",len="+len);
            } catch (Exception e) {
                e.printStackTrace();
            }

            var reversed = new Stack<ScriptCmd>();

            while (!ops_stack.empty()) {
                reversed.push(ops_stack.pop());
            }

        return new Script(reversed);
    }

    /*************************************************************************/
    public boolean evaluate(byte[] z) {
        var cmds = new Stack<ScriptCmd>();
        cmds.addAll(this.commands); // make a copy

        var stack = new Stack<byte[]>();
        var altstack = new Stack<byte[]>();

        System.out.println("*********************************************************************");
        System.out.println("SCRIPT-> Starting evalutation of script:");
        System.out.println(this);


        while (cmds.size()>0) {
            var cmd = cmds.pop();

            // if it is data, just move it to the stack

            if (cmd.type== ScriptCmdType.DATA) {
                stack.push(cmd.value);

                //detect p2sh pattern

                if (cmds.size()==3
                        && cmds.elementAt(0).type==ScriptCmdType.OP_EQUAL
                        && cmds.elementAt(1).type==ScriptCmdType.DATA
                        && cmds.elementAt(1).value.length==20
                        && cmds.elementAt(2).type==ScriptCmdType.OP_HASH160)
                {
                    cmds.pop(); // we already know it's op_hash160
                    var h160 = cmds.pop(); // the hash value
                    cmds.pop(); // we already know it's op equal

                    if (!this.OP_HASH160(stack)) return false;
                    stack.push(h160.value);
                    if (!this.OP_EQUAL(stack)) return false;
                    if (!this.OP_VERIFY(stack)) {
                        System.out.println("******************* WARNING: bad p2sh h160");
                        return false;
                    }



                    var bos = new ByteArrayOutputStream();
                    try {
                        bos.write(CryptoKit.encodeVarint(cmd.value.length));
                        bos.write(cmd.value);
                        var redeem_script = Script.parseSerial(bos.toByteArray());
                        cmds.addAll(redeem_script.commands);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }

            }
            else { // not a data element, we must execute the opcode logic

                if (cmd.type == ScriptCmdType.OP_IF || cmd.type == ScriptCmdType.OP_NOTIF ) {
                    System.out.println("ERROR: OP_IF not implemented!");
                    System.exit(-1);

                    // require manipulation of commands using the top of stack
                    assert false;
                }
                else if (cmd.type.getValue() >= 172 && cmd.type.getValue() <= 175){
                    // require signature (CHECKSIG etc..)
                    switch (cmd.type) {
                        case OP_CHECKSIG:
                            this.OP_CHECKSIG(stack,z);
                            break;
                        case OP_CHECKMULTISIG:
                            this.OP_CHECKMULTISIG(stack,z);
                            break;
                        default:
                            assert false;
                    }
                }
                else {

                    switch (cmd.type) {
                        case OP_0:
                            this.OP_0(stack);
                            break;
                        case OP_1:
                            this.OP_1(stack);
                            break;
                        case OP_2:
                            this.OP_2(stack);
                            break;
                        case OP_3:
                            this.OP_3(stack);
                            break;
                        case OP_4:
                            this.OP_4(stack);
                            break;
                        case OP_5:
                            this.OP_5(stack);
                            break;
                        case OP_6:
                            this.OP_6(stack);
                            break;
                        case OP_7:
                            this.OP_7(stack);
                            break;
                        case OP_8:
                            this.OP_8(stack);
                            break;
                        case OP_9:
                            this.OP_9(stack);
                            break;
                        case OP_10:
                            this.OP_10(stack);
                            break;
                        case OP_11:
                            this.OP_11(stack);
                            break;
                        case OP_12:
                            this.OP_12(stack);
                            break;
                        case OP_13:
                            this.OP_13(stack);
                            break;
                        case OP_14:
                            this.OP_14(stack);
                            break;
                        case OP_15:
                            this.OP_15(stack);
                            break;
                        case OP_16:
                            this.OP_16(stack);
                            break;

                        case OP_VERIFY:
                            this.OP_VERIFY(stack);
                            break;

                        case OP_RETURN:
                            this.OP_RETURN(stack);
                            break;
                        case OP_TOALTSTACK:
                            this.OP_TOALTSTACK(stack,altstack);
                            break;
                        case OP_FROMALTSTACK:
                            this.OP_FROMALTSTACK(stack,altstack);
                            break;
                        case OP_2DUP:
                            this.OP_2DUP(stack);
                            break;
                        case OP_EQUAL:
                            this.OP_EQUAL(stack);
                            break;
                        case OP_EQUALVERIFY:
                            this.OP_EQUALVERIFY(stack);
                            break;
                        case OP_NOT:
                            this.OP_NOT(stack);
                            break;
                        case OP_ADD:
                            this.OP_ADD(stack);
                            break;
                        case OP_SUB:
                            this.OP_SUB(stack);
                            break;
                        case OP_MUL:
                            this.OP_MUL(stack);
                            break;
                        case OP_RIPEMD160:
                            this.OP_RIPEMD160(stack);
                            break;
                        case OP_SHA256:
                            this.OP_SHA256(stack);
                            break;
                        case OP_DUP:
                            this.OP_DUP(stack);
                            break;
                        case OP_HASH256:
                            this.OP_HASH256(stack);
                            break;

                        case OP_HASH160:
                            this.OP_HASH160(stack);
                            break;
                        default:
                            System.out.println("FATAL: unsupported Script command "+cmd);
                            System.exit(-1);
                    }
                    // require only stack
                }
            }

        }
        if (stack.size()==0) return false;
        return stack.pop() != null;
    }


    /*************************************************************************/
    public boolean OP_0(Stack<byte[]> stack) {
        stack.push(encodeNum(0));
        return true;
    }
    public boolean OP_1(Stack<byte[]> stack) {
        stack.push(encodeNum(1));
        return true;
    }
    public boolean OP_2(Stack<byte[]> stack) {
        stack.push(encodeNum(2));
        return true;
    }
    public boolean OP_3(Stack<byte[]> stack) {
        stack.push(encodeNum(3));
        return true;
    }
    public boolean OP_4(Stack<byte[]> stack) {
        stack.push(encodeNum(4));
        return true;
    }
    public boolean OP_5(Stack<byte[]> stack) {
        stack.push(encodeNum(5));
        return true;
    }
    public boolean OP_6(Stack<byte[]> stack) {
        stack.push(encodeNum(6));
        return true;
    }
    public boolean OP_7(Stack<byte[]> stack) {
        stack.push(encodeNum(7));
        return true;
    }
    public boolean OP_8(Stack<byte[]> stack) {
        stack.push(encodeNum(8));
        return true;
    }
    public boolean OP_9(Stack<byte[]> stack) {
        stack.push(encodeNum(9));
        return true;
    }
    public boolean OP_10(Stack<byte[]> stack) {
        stack.push(encodeNum(10));
        return true;
    }
    public boolean OP_11(Stack<byte[]> stack) {
        stack.push(encodeNum(11));
        return true;
    }
    public boolean OP_12(Stack<byte[]> stack) {
        stack.push(encodeNum(12));
        return true;
    }
    public boolean OP_13(Stack<byte[]> stack) {
        stack.push(encodeNum(13));
        return true;
    }
    public boolean OP_14(Stack<byte[]> stack) {
        stack.push(encodeNum(14));
        return true;
    }
    public boolean OP_15(Stack<byte[]> stack) {
        stack.push(encodeNum(15));
        return true;
    }
    public boolean OP_16(Stack<byte[]> stack) {
        stack.push(encodeNum(16));
        return true;
    }
    public boolean OP_1NEGATE(Stack<byte[]> stack) {
        stack.push(encodeNum(-1));
        return true;
    }
    // TODO add the other OP_N *********************************

    public boolean OP_IF(Stack<byte[]> stack) {
        // TODO: implement
        return stack.size() >= 1;
    }

    public boolean OP_VERIFY(Stack<byte[]> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        return (decodeNum(element).compareTo(BigInteger.ZERO)) != 0;
    }
    public boolean OP_RETURN(Stack<byte[]> stack) {
        return false;
    }

    public boolean OP_TOALTSTACK(Stack<byte[]> stack, Stack<byte[]> altstack) {
        if (stack.size()<1) return false;
        altstack.push(stack.pop());
        return true;
    }

    public boolean OP_FROMALTSTACK(Stack<byte[]> stack, Stack<byte[]> altstack) {
        if (stack.size()<1) return false;
        stack.push(altstack.pop());
        return true;
    }

    private boolean OP_2DUP(Stack<byte[]> stack) {
        if (stack.size()<2) return false;
        var top1 = stack.peek();
        var top2 = stack.elementAt(stack.size()-2);
        stack.push(top2);
        stack.push(top1);
        return true;
    }

    public boolean OP_EQUAL(Stack<byte[]> stack) {
        if (stack.size()<2) return false;
        var e1 = stack.pop();
        var e2 = stack.pop();



        if (Arrays.equals(e1,e2)) {
            stack.push(encodeNum(1));
        }
            else {
                stack.push(encodeNum(0));
        }
        return true;
    }

    public boolean OP_EQUALVERIFY(Stack<byte[]> stack) {
        return OP_EQUAL(stack) && OP_VERIFY(stack);
    }
    public boolean OP_NOT(Stack<byte[]> stack) {
        if (stack.size()<1) return false;

        var element = decodeNum(stack.pop());
        if (element.equals(BigInteger.ZERO))
            stack.push(encodeNum(1));
        else
            stack.push(encodeNum(0));

        return true;
    }

    public boolean OP_ADD(Stack<byte[]> stack) {
        if (stack.size()<2) return false;

        var e1 = decodeNum(stack.pop());
        var e2 = decodeNum(stack.pop());

        stack.push(encodeNum(e1.add(e2)));
        return true;
    }

    public boolean OP_SUB(Stack<byte[]> stack) {
        if (stack.size()<2) return false;

        var e1 = decodeNum(stack.pop());
        var e2 = decodeNum(stack.pop());

        stack.push(encodeNum(e2.subtract(e1)));
        return true;
    }

    public boolean OP_MUL(Stack<byte[]> stack) {
        if (stack.size()<2) return false;

        var e1 = decodeNum(stack.pop());
        var e2 = decodeNum(stack.pop());

        stack.push(encodeNum(e2.multiply(e1)));
        return true;
    }

    public boolean OP_RIPEMD160(Stack<byte[]> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();

        stack.push(CryptoKit.RIPEMD160(element));
        return true;
    }

    public boolean OP_SHA256(Stack<byte[]> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();

        stack.push(CryptoKit.sha256(element));
        return true;
    }

    public boolean OP_HASH160(Stack<byte[]> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        var hashed = CryptoKit.hash160(element);
        stack.push(hashed);
        return true;
    }

    public boolean OP_DUP(Stack<byte[]> stack) {
        if (stack.size()<1) return false;
        stack.push(stack.peek());
        return true;
    }

    public boolean OP_HASH256(Stack<byte[]> stack) {
        if (stack.size()<1) return false;
        var element = stack.pop();
        var hashed = CryptoKit.hash256(element);
        stack.push(hashed);
        return true;
    }


    public boolean OP_CHECKSIG(Stack<byte[]> stack, byte[] z) {
        if (stack.size()<2) return false;

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
            stack.push(encodeNum(1));
        }
        //
        // TODO: implement encode_num(0)
        else stack.push(encodeNum(0));

        return false;
    }
    /*************************************************************************/
    public boolean OP_CHECKMULTISIG(Stack<byte[]> stack,byte[] z) {

        if (stack.size()<2) return false;

        var n = decodeNum(stack.pop());
        if (stack.size() < n.longValue()+1)
            return false;

        var sec_pubkeys = new ArrayList<byte[]>();

        for (int i =0;i<n.longValue();i++) {
            sec_pubkeys.add(stack.pop());
        }

        var m = decodeNum(stack.pop());
        if (stack.size() < m.longValue()+1)
            return false;

        var der_signatures = new ArrayList<byte[]>();

        for (int i=0;i<m.longValue();i++) {
            var sig_with_SIGHASH_ALL = stack.pop();
            var sig = Arrays.copyOfRange(sig_with_SIGHASH_ALL,0,sig_with_SIGHASH_ALL.length-1);
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
            if (points.size()==0) return false;

            for (S256Point p: points) {

                if (p.verify(new BigInteger(1,z),sig)) {
                    points.remove(p);
                    break;
                }
            }
        }

        stack.push(encodeNum(1));
        return true;
    }


    /*************************************************************************/
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("Script Stack:");
        out.append("\n-----------------------------------------");

        for (int i=this.commands.size()-1; i>-1; i--) {
            out.append(this.commands.get(i));
        }
        out.append("\n-----------------------------------------\n");

        return out.toString();

    }
}
