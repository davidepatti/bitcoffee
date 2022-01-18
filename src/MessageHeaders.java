import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessageHeaders extends Message {

    private ArrayList<Block> blocks;

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
        System.out.println("********************************************************");
        System.out.println("Parsing MessageHeader, size: "+bytes.length);
        System.out.println("Num Headers:"+num_headers);
        System.out.println("********************************************************");

        ArrayList<Block> blocks = new ArrayList<>();

        for (int i=0;i<num_headers;i++) {
            try {
                var ablock = Block.parseSerial(bis);
                blocks.add(ablock);
                var num_txs = Kit.readVarint(bis);
                if (num_txs!=0) {
                   throw new Exception("Number of tx not 0") ;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.blocks = blocks;
        System.out.println("********************************************************");
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

        return new MessageHeaders(bytes);
    }
}
