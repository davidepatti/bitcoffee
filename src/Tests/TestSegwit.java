package Tests;

import bitcoffee.*;

public class TestSegwit {
    public static void main(String[] args) {

        Test.__BEGIN_TEST("verify Tx");
        var tx_id = "452c629d67e41baec3ac6f04fe744b4b9617f8f859c63b3002f8684e7a4fee03";
        var tx = TxFetcher.fetch(tx_id);
        System.out.println(tx);
        Test.check("p2pkh (mainnet)","tx id:"+tx_id,true,tx.verify());

        tx_id = "5418099cc755cb9dd3ebc6cf1a7888ad53a1a3beb5a025bce89eb1bf7f1650a2";
        tx = TxFetcher.fetch(tx_id,true);
        System.out.println(tx);
        Test.check("p2pkh (testnet)","tx id:"+tx_id,true,tx.verify());

        tx_id = "46df1a9484d0a81d03ce0ee543ab6e1a23ed06175c104a178268fad381216c2b";
        tx = TxFetcher.fetch(tx_id);
        System.out.println(tx);
        Test.check("p2sh (mainnet)","tx id:"+tx_id,true,tx.verify());

        tx_id = "d869f854e1f8788bcff294cc83b280942a8c728de71eb709a2c29d10bfe21b7c";
        tx = TxFetcher.fetch(tx_id,true);
        System.out.println(tx);
        Test.check("p2wpk (testnet)","tx id:"+tx_id,true,tx.verify());

        tx_id = "c586389e5e4b3acb9d6c8be1c19ae8ab2795397633176f5a6442a261bbdefc3a";
        tx = TxFetcher.fetch(tx_id);
        Test.check("p2sh-p2wpk (mainnet)","tx id:"+tx_id,true,tx.verify());

        tx_id = "78457666f82c28aa37b74b506745a7c7684dc7842a52a457b09f09446721e11c";
        tx = TxFetcher.fetch(tx_id,true);
        Test.check("p2wsh (testnet)","tx id:"+tx_id,true,tx.verify());

        tx_id = "954f43dbb30ad8024981c07d1f5eb6c9fd461e2cf1760dd1283f052af746fc88";
        tx = TxFetcher.fetch(tx_id,true);
        Test.check("p2sh-p2wsh (testnet)","tx id:"+tx_id,true,tx.verify());


        tx_id = "edd566225dcc0aecffea39ebb137b07e35853d9c307d8bddc59373e66c9511c5";
        tx = TxFetcher.fetch(tx_id);
        Test.check("p2sh-p2wsh (testnet)","tx id:"+tx_id,true,tx.verify());

    }
}
