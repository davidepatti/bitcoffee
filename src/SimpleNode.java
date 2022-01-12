import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SimpleNode {

    private final String host;
    private final int port;
    private final boolean testnet;
    private final boolean logging;
    private Socket socket;
    private InputStream bis;
    private OutputStream bos;
    private DataInputStream dis;


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
            dis = new DataInputStream(bis);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        var envelope = new NetworkEnvelope(message.getCommand(), message.serialize(), this.testnet);

        //if (logging)
        try {
            System.out.println("-----------------------------------------------------------");
            System.out.println("SENDING message: "+message);
            var bytes_to_send = envelope.serialize();
            System.out.println("SENDING BYTES: "+Kit.bytesToHexString(bytes_to_send));
            System.out.println("-----------------------------------------------------------");
            bos.write(bytes_to_send);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NetworkEnvelope read() {
        try {
            System.out.println("read(): waiting for data...");
            /*
            while (bis.available()==0) {
                System.out.println("%");
                Thread.sleep(1000);
                Scanner sc = new Scanner(bis);
                System.out.println("REMOVING "+sc.nextLine());
            }
            */
            //var received_bytes = bis.readAllBytes();


            var os = new ByteArrayOutputStream();

            byte[] buffer = new byte[102400]; // the well known size
            int nread = dis.read(buffer);
            os.write(buffer,0,nread);
            var received_bytes = os.toByteArray();

            System.out.println("LENGTH: "+nread);
            System.out.println("read(): data received "+Kit.bytesToHexString(received_bytes));
            var envelope = NetworkEnvelope.parse(received_bytes,this.testnet);
            System.out.println("Envelope :"+envelope);
            return envelope;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //catch (IOException e) { e.printStackTrace(); }
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
        System.out.println("Waiting for message(s): "+messageSet);

        while (!stop) {
            var env = this.read();
            var command = env.getCommand();

            System.out.println("Received command:"+command);

            switch (command) {
                case "version":
                    this.send(new MessageVerAck());
                    stop = true;
                    return new MessageVersion();
                case "ping":
                    var nonce = new BigInteger(env.getPayload()).longValue();
                    this.send(new MessagePong(nonce));
                    stop = true;
                    return new MessagePing(nonce);
                case "headers":
                    var payload = env.getPayload();
                    MessageHeaders msg = new MessageHeaders(payload);
                    stop = true;
                    return msg;
                default:
                    System.out.println("UNMANAGED COMMAND: "+command);
            }
        }
        return null;
    }
}
