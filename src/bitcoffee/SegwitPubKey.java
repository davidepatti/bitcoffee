package bitcoffee;

import java.io.IOException;

public class SegwitPubKey extends ScriptPubKey {

    @Override
    public String getAddress(boolean testnet) {
        var witness_program = rawSerialize();

        return Bech32.encode_bech32_checksum(witness_program,testnet,false);
    }

    public String getP2SHAddress(boolean testnet) {
        return this.getRedeemScript().getAddress(testnet);
    }
}
