import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;


// the message that is transmitted is inside a network envelope
// the command field and the internal payload will differentiate among the different message types (version, ack etc...)

public class NetworkEnvelope {
    final public static String NETWORK_MAGIC = "f9beb4d9";
    final public static String TESTNET_NETWORK_MAGIC = "0b110907";
    final private String command;
    final private byte[] payload;
    final private boolean testnet;
    final private byte[] magic;


    public NetworkEnvelope(String command, byte[] payload, boolean testnet) {
        this.command = command;
        this.payload = payload;
        this.testnet = testnet;

        if (testnet)
            this.magic = Kit.hexStringToByteArray(TESTNET_NETWORK_MAGIC);
        else
                this.magic = Kit.hexStringToByteArray(NETWORK_MAGIC);
    }
    public static NetworkEnvelope parse(DataInputStream bis, boolean testnet)  {
        var magic = new byte[4];
        var command = new byte[12];

        System.out.println("Parsing network envelope");
        try {
            if (bis.available()==0) return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            magic = bis.readNBytes(4);
            String expected_magic;

            if (testnet) expected_magic = TESTNET_NETWORK_MAGIC;
            else
                expected_magic = NETWORK_MAGIC;
            if (!Kit.bytesToHexString(magic).equals(expected_magic)) {
                System.out.println("WRONG MAGIC");
                for (byte b: magic)  {
                    System.out.println(b);
                }
                return null;
                //throw new IOException(" Wrong magic:"+Kit.bytesToHexString(magic)+" Expected: "+expected_magic);
            }

            int count_end_zeros = 0;
            command = bis.readNBytes(12);
            int i = 11;
            while (command[i--]==0) count_end_zeros++;

            if (count_end_zeros!=0)
                command = Arrays.copyOfRange(command,0,12-count_end_zeros);

            var payload_len = Kit.litteEndianBytesToInt(bis.readNBytes(4)).intValue();
            var checksum = bis.readNBytes(4);
            var payload = bis.readNBytes(payload_len);
            var calculated_checksum = Arrays.copyOfRange(Kit.hash256(payload),0,4);
            if (!Arrays.equals(calculated_checksum,checksum)) {
                System.out.println("ERROR: Wrong calculated CHECKSUM "+Kit.bytesToHexString(calculated_checksum)+" expected: "+Kit.bytesToHexString(checksum));
                //throw new IOException("Wrong payload checksum");
            }
            return new NetworkEnvelope(Kit.bytesToAscii(command),payload,testnet);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public byte[] serialize() {
        byte[] serial = null;
        var bos = new ByteArrayOutputStream();

        try {
            String magic;
            if (this.testnet) magic = TESTNET_NETWORK_MAGIC;
            else
                magic = NETWORK_MAGIC;
            if (!Kit.bytesToHexString(this.magic).equals(magic)) {
                throw new IOException(" Wrong magic:"+Kit.bytesToHexString(this.magic));
            }
            bos.write(this.magic);
            bos.write(Kit.asciiStringToBytes(this.command));

            // pad with zeros at the end to reach 12 bytes
            for (int i =0;i<12-this.command.length();i++)
                bos.write(0);

            bos.write(Kit.intToLittleEndianBytes(this.payload.length),0,4);
            bos.write(Kit.hash256(this.payload),0,4);
            bos.write(this.payload);

            serial = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return serial;
    }

    @Override
    public String toString() {
        var str =  "NetworkEnvelope{" + "command=" + command;

        if (this.payload!=null) str+= ", payload=" + Kit.bytesToHexString(payload)+'}';
        return str;
    }

    public String getCommand() {
        return command;
    }

    public byte[] getPayload() {
        return payload;
    }
}
