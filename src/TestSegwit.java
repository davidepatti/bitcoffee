public class TestSegwit {
    public static void main(String[] args) {


        Test.__BEGIN_TEST("verify Tx");
        var tx_id = "d869f854e1f8788bcff294cc83b280942a8c728de71eb709a2c29d10bfe21b7c";
        var tx = TxFetcher.fetch(tx_id,true,true);
        Test.check("p2wpk","tx id:"+tx_id,true,tx.verify());


    }
}
