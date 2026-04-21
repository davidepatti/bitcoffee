package bitcoffee;

import java.io.IOException;

public class SegwitPubKey extends ScriptPubKey {

    @Override
    public String getAddress(boolean testnet) {
        var witness_program = rawSerialize();
        boolean bech32m = witness_program.length > 0 && witness_program[0] != 0;

        return Bech32.encode_bech32_checksum(witness_program,testnet,bech32m);
    }

    public String getP2SHAddress(boolean testnet) {
        return this.getRedeemScript().getAddress(testnet);
    }
}
