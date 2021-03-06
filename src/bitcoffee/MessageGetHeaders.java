package bitcoffee;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class MessageGetHeaders implements Message {
    final private int version;
    final private String start_header;
    final private String end_header;
    final int n_hashes;

    public final static String COMMAND = "getheaders";


    @Override
    public String getCommand() {
        return MessageGetHeaders.COMMAND;
    }

    @Override
    public byte[] getPayload() {
        return this.serialize();
    }


    public MessageGetHeaders(String start_block){
        this.version = 70015;
        this.n_hashes = 1;
        this.start_header = start_block;
        this.end_header = "0000000000000000000000000000000000000000000000000000000000000000";
    }


    public MessageGetHeaders(int version, int n_hashes, String start_header, String end_block ){
        this.version = version;
        this.n_hashes = n_hashes;
        this.start_header = start_header;
        this.end_header = end_block;
    }

    private byte[] serialize() {
        var bos = new ByteArrayOutputStream();

        try {
            bos.write(Kit.intToLittleEndianBytes(this.version),0,4);
            bos.write(Objects.requireNonNull(Kit.encodeVarint(this.n_hashes)));
            bos.write(Kit.reverseBytes(Kit.hexStringToByteArray(this.start_header)));
            bos.write(Kit.reverseBytes(Kit.hexStringToByteArray(this.end_header)));
            bos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    @Override
    public String toString() {
        return "MessageGetHeaders{" +
                "version=" + version +
                ", start_header='" + start_header + '\'' +
                ", end_header='" + end_header + '\'' +
                ", n_hashes=" + n_hashes +
                '}';
    }

}
