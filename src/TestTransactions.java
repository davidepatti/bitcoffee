
public class TestTransactions {
    public static void main(String args[]) {

        System.out.println("--------------------------------------------------------");
        System.out.println(">> Test: Transaction decoding");
        // Exercise 5 of chapter 5

        String trans = "010000000456919960ac691763688d3d3bcea9ad6ecaf875df5339e148a1fc61c6ed7a069e0100"+
"00006a47304402204585bcdef85e6b1c6af5c2669d4830ff86e42dd205c0e089bc2a821657e951"+
"c002201024a10366077f87d6bce1f7100ad8cfa8a064b39d4e8fe4ea13a7b71aa8180f012102f0"+
"da57e85eec2934a82a585ea337ce2f4998b50ae699dd79f5880e253dafafb7feffffffeb8f51f4"+
"038dc17e6313cf831d4f02281c2a468bde0fafd37f1bf882729e7fd3000000006a473044022078"+
"99531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b84"+
"61cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba"+
"1f686f15f009ded7c62efe85a872e6a19b43c15a2937feffffff567bf40595119d1bb8a3037c35"+
"6efd56170b64cbcc160fb028fa10704b45d775000000006a47304402204c7c7818424c7f7911da"+
"6cddc59655a70af1cb5eaf17c69dadbfc74ffa0b662f02207599e08bc8023693ad4e9527dc42c3"+
"4210f7a7d1d1ddfc8492b654a11e7620a0012102158b46fbdff65d0172b7989aec8850aa0dae49"+
"abfb84c81ae6e5b251a58ace5cfeffffffd63a5e6c16e620f86f375925b21cabaf736c779f88fd"+
"04dcad51d26690f7f345010000006a47304402200633ea0d3314bea0d95b3cd8dadb2ef79ea833"+
"1ffe1e61f762c0f6daea0fabde022029f23b3e9c30f080446150b23852028751635dcee2be669c"+
"2a1686a4b5edf304012103ffd6f4a67e94aba353a00882e563ff2722eb4cff0ad6006e86ee20df"+
"e7520d55feffffff0251430f00000000001976a914ab0c0b2e98b1ab6dbf67d4750b0a56244948"+
"a87988ac005a6202000000001976a9143c82d7df364eb6c75be8c80df2b3eda8db57397088ac46430600";

        Tx tx = Tx.parse(CryptoKit.hexStringToByteArray(trans),false);
        System.out.println("Transaction parsed:");
        System.out.println(tx);


        var res_script_sig = CryptoKit.bytesToHexString(tx.tx_ins.get(1).getScript_sig());
        var res_script_pubkey = CryptoKit.bytesToHexString(tx.tx_outs.get(1).getScriptPubkey());
        var target_sig = "47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937";
        var target_pub = "76a9143c82d7df364eb6c75be8c80df2b3eda8db57397088ac";

        System.out.println(">> Testing TX ID:"+tx.getId());

        System.out.println(">> Testing ScriptSig second input: "+res_script_sig.equals(target_sig));
        System.out.println(">> ScriptPubKey second output:"+res_script_pubkey.equals(target_pub));

        var fee = tx.calculateFee();
        System.out.print(">> Test Calculated fee: "+fee);
        long target_fee = 140500;
        if (fee==target_fee) System.out.println(" Test fee OK!");
        else
            System.out.println(" Test fee ERROR");

        System.out.println("-----------------------------------------------------------");



        //https://blockstream.info/api/tx/716373514d1442f6e7f71719965936fc8df12fe581f5d4fb3a3fd038cbbe4f4c/hex
        String target_tx_id = "716373514d1442f6e7f71719965936fc8df12fe581f5d4fb3a3fd038cbbe4f4c";
        System.out.println(">> Test fetching Transaction");
        System.out.println("tx_id:"+target_tx_id);
        var tx_result = TxFetcher.fetch(target_tx_id,false,true);
        System.out.println("Tx result:"+tx_result);


    }
}
