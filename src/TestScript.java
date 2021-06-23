import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestScript {

    public static void main(String args[]) {

        var bos = new ByteArrayOutputStream();

        bos.write(1);
        bos.write(0x54);

        try {
            Script script = Script.parse(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
