import java.util.Arrays;

public class TestNetwork {

    public static void main(String[] args) {
        System.out.println("-------------------------------------------------------");
        System.out.println(">>Testing NetworkEnvelope");

        var serialized_target = Kit.hexStringToByteArray("f9beb4d976657273696f6e0000000000650000005f1a69d2721101000100000000000000bc8f5e5400000000010000000000000000000000000000000000ffffc61b6409208d010000000000000000000000000000000000ffffcb0071c0208d128035cbc97953f80f2f5361746f7368693a302e392e332fcf05050001");
        var envelope = NetworkEnvelope.parse(serialized_target,false);
        System.out.println(envelope);

        var serialized_bytes = envelope.serialize();
        System.out.println("RESULT: "+ Arrays.equals(serialized_bytes,serialized_target));

        System.out.println("-------------------------------------------------------");
        System.out.println(">>Testing VersionMessage");
        var version_msg = new VersionMessage(0,Kit.hexStringToByteArray("0000000000000000"));

        System.out.println("--> serializing:");
        System.out.println(version_msg);
        serialized_target = Kit.hexStringToByteArray("7f11010000000000000000000000000000000000000000000000000000000000000000000000ffff00000000208d000000000000000000000000000000000000ffff00000000208d0000000000000000182f70726f6772616d6d696e67626974636f696e3a302e312f0000000000");
        serialized_bytes = version_msg.serialize();
        System.out.println("--> Result:");
        System.out.println(Kit.bytesToHexString(serialized_bytes));
        System.out.println("RESULT: "+ Arrays.equals(serialized_bytes,serialized_target));
        System.out.println("-------------------------------------------------------");
        System.out.println(">>Testing Network Connection");


    }
}
