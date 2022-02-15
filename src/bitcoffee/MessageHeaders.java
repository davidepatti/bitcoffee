package bitcoffee;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class MessageHeaders implements Message {

    private final ArrayList<Block> blocks;
    public static final String COMMAND = "headers";


    public MessageHeaders(ArrayList<Block> blocks){

        this.blocks = blocks;
    }

    @Override
    public String getCommand() {
        return MessageHeaders.COMMAND;
    }

    @Override
    public byte[] getPayload() {
        throw new RuntimeException("Not implemented");
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public MessageHeaders(byte[] bytes) {
        var bis = new ByteArrayInputStream(bytes);

        var num_headers = Kit.readVarint(bis);
        System.out.println("-> Parsing MessageHeader, size: "+bytes.length+" num Headers:"+num_headers);

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
    }

    @Override
    public String toString() {
        return null;
    }


    public static MessageHeaders parse(byte[] bytes) {
        return new MessageHeaders(bytes);
    }
}
