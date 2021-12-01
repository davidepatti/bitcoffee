import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleNode {

    private final String host;
    private final int port;
    private final boolean testnet;
    private final boolean logging;
    private Socket socket;
    private InputStream bis;
    private OutputStream bos;


    public SimpleNode(String host, int port, boolean testnet, boolean logging) {

        this.host = host;
        this.port = port;
        this.testnet = testnet;
        this.logging = logging;
        this.initConnection();
    }
    public SimpleNode(String host,  boolean testnet) {

        this.host = host;
        if (testnet) this.port = 18333;
        else
            this.port = 8333;

        this.testnet = testnet;
        this.logging = false;
        this.initConnection();
    }

    private void initConnection() {
        try {
            this.socket = new Socket(host,this.port);
            bis = socket.getInputStream();
            bos = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        var envelope = new NetworkEnvelope(message.command, message.serialize(), this.testnet);

        //if (logging)
        System.out.println("Sending messsage : "+envelope);
        try {
            bos.write(envelope.serialize());
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NetworkEnvelope read() {
        try {
            while (bis.available()==0);
            var envelope = NetworkEnvelope.parse(bis.readAllBytes(),this.testnet);
            System.out.println("Receiving :"+envelope);
            return envelope;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
