import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessageHeaders extends Message {

    private ArrayList<Block> blocks = new ArrayList<>();

    final static String command = "headers";

    public MessageHeaders(ArrayList<Block> blocks){
        this.blocks = blocks;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public MessageHeaders(byte[] bytes) {
        var bis = new ByteArrayInputStream(bytes);

        var num_headers = Kit.readVarint(bis);

        ArrayList<Block> blocks = new ArrayList<>();

        for (int i=0;i<num_headers;i++) {
            try {
                blocks.add(Block.parseSerial(bis.readNBytes(80)));
                var num_txs = Kit.readVarint(bis);
                if (num_txs!=0) {
                   throw new Exception("Number of tx not 0") ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.blocks = blocks;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    @Override
    public Message parse(byte[] bytes) {

        MessageHeaders msg = new MessageHeaders(bytes);
        return msg;
    }
}
