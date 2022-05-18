package bitcoffee;

import java.io.IOException;

public class SegwitPubKey extends ScriptPubKey {

    @Override
    public String getAddress(boolean testnet) {
        try {
            var witness_program = raw_serialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
