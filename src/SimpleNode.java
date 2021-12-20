import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
            System.out.println("Simple node, starting connecton to "+host);
            this.socket = new Socket(host,this.port);
            bis = socket.getInputStream();
            bos = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        var envelope = new NetworkEnvelope(message.getCommand(), message.serialize(), this.testnet);

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

    public void Handshake() {
        var message = new MessageVersion();
        this.send(message);
        this.waitFor(MessageVerAck.command);
    }

    public Message waitFor(String command) {
        Set<String> set = new HashSet<>();
        set.add(command);
        return waitFor(set);
    }


    public Message waitFor(Set<String> messageSet) {

        // TODO: support for message set

        boolean stop = false;

        while (!stop) {
            var env = this.read();
            var command = env.getCommand();

            if (command.equals("version")) {
                this.send(new MessageVerAck());
                stop = true;

                return new MessageVersion();
            }
            else
            if (command.equals("ping")) {
                var nonce = new BigInteger(env.getPayload()).longValue();
                this.send(new MessagePong(nonce));
                stop = true;
                return new MessagePing(nonce);
            }
        }

        return null;
    }

}
