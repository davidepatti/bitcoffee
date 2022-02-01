import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessageGetData implements Message {

    public final static String COMMAND = "getdata";
    public static final int TX_DATA_TYPE = 1;
    public static final int BLOCK_DATA_TYPE = 2;
    public static final int FILTERED_BLOCK_DATA_TYPE = 3;
    public static final int COMPACT_BLOCK_DATA_TYPE = 4;

    private ArrayList<DataEntry> data = new ArrayList<>();


    public MessageGetData(String getdata, byte[] bytes) {
    }

    public MessageGetData() {
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public byte[] getPayload() {
        return this.serialize();
    }

    public static MessageGetData parse(byte[] bytes) {
        return new MessageGetData(COMMAND,bytes);

    }

    private class DataEntry {
        final int type;
        final String identifier;

        public DataEntry(int type, String identifier) {
            this.type = type;
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return "DataEntry{" +
                    "type=" + type +
                    ", identifier='" + identifier + '\'' +
                    '}';
        }
    }


    public void addData(int type, String identifier) {
       this.data.add(new DataEntry(type,identifier));
    }

    private byte[] serialize() {
        var bos = new ByteArrayOutputStream();

        try {
            bos.write(Kit.encodeVarint(data.size()));
            for (DataEntry d: data) {
                bos.write(Kit.intToLittleEndianBytes(d.type),0,4);
                bos.write(Kit.reverseBytes(Kit.hexStringToByteArray(d.identifier)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    @Override
    public String toString() {
        return "MessageGetData{" + "data size =" + data.size() + '}';
    }
}

