package Tests;

import bitcoffee.*;

public class TestSegwit {
    public static void main(String[] args) {

        Test.__BEGIN_TEST("verify bitcoffee.Tx");
        var tx_id = "d869f854e1f8788bcff294cc83b280942a8c728de71eb709a2c29d10bfe21b7c";
        var tx = TxFetcher.fetch(tx_id,true,true);
        Test.check("p2wpk","tx id:"+tx_id,true,tx.verify());

        tx_id = "c586389e5e4b3acb9d6c8be1c19ae8ab2795397633176f5a6442a261bbdefc3a";
        tx = TxFetcher.fetch(tx_id,false,true);
        Test.check("p2wpk (mainnet)","tx id:"+tx_id,true,tx.verify());



    }
}
