import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestScript {

    public static void main(String args[]) {

        var bos = new ByteArrayOutputStream();

        bos.write(3);

        bos.write(0x54);
        bos.write(0);
        bos.write(79);


        try {
            Script script = Script.parse(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
