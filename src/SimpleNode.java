import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SimpleNode {

    private final String host;
    private final int port;
    private final boolean testnet;
    private final boolean logging;
    private OutputStream os;
    private DataInputStream dis;
    private ByteArrayInputStream bis;


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
            Socket socket = new Socket(host, this.port);
            InputStream is = socket.getInputStream();
            os = socket.getOutputStream();
            dis = new DataInputStream(new BufferedInputStream(is));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        var envelope = new NetworkEnvelope(message.getCommand(), message.getPayload(), this.testnet);

        //if (logging)
        try {
            System.out.println("-----------------------------------------------------------");
            System.out.println("SENDING message: "+message);
            var bytes_to_send = envelope.serialize();
            //System.out.println("SENDING BYTES: "+Kit.bytesToHexString(bytes_to_send));
            System.out.println("-----------------------------------------------------------");
            os.write(bytes_to_send);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NetworkEnvelope read() {
        try {
            System.out.println("------------------------------------------------------");
            System.out.println("read(): waiting for data...");

            var envelope = NetworkEnvelope.parse(dis,this.testnet);
            System.out.println("Read Envelope with Command :"+ Objects.requireNonNull(envelope).getCommand());
            System.out.println("------------------------------------------------------");
            return envelope;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void Handshake() {
        var message = new MessageVersion();
        this.send(message);
        this.waitFor(MessageVerAck.COMMAND);
    }

    public Message waitFor(String command) {
        Set<String> set = new HashSet<>();
        set.add(command);
        return waitFor(set);
    }


    public Message waitFor(Set<String> messageSet) {

        boolean matching_message = false;
        System.out.println("Waiting for message(s): "+messageSet);

        while (!matching_message) {
            var env = this.read();
            var command = env.getCommand();

            if (messageSet.contains(command)) {
                System.out.println("Received Matching message: "+command);
                matching_message = true;
            }
            else System.out.println("Received Not matching message: "+command);

            switch (command) {
                case "version":
                    this.send(new MessageVerAck());
                    if (matching_message) return new MessageVersion();
                    break;
                case "ping":
                    var nonce = new BigInteger(env.getPayload()).longValue();
                    this.send(new MessagePong(nonce));
                    if (matching_message) return new MessagePong(nonce);
                    break;
                case "headers":
                    var payload = env.getPayload();
                    MessageHeaders msg = new MessageHeaders(payload);
                    if (matching_message) return msg;
                    break;
                case "verack":
                    break;
                default:
                    System.out.println("UNMANAGED COMMAND: "+command);
            }
        }
        return null;
    }
}
