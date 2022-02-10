package bitcoffee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class TxFetcher {

    static final HashMap<String, Tx> cache = new HashMap<>();

    public static String getURL(boolean testnet) {

        if (testnet)
            return "https://blockstream.info/testnet/api";
        else
            return "https://blockstream.info/api";
    }

    public static Tx fetch(String tx_id, boolean testnet, boolean fresh) {
        Tx tx;
        String computed_id;

        try {
            if (fresh || !(cache.containsKey(tx_id))) {
                var url = new URL(getURL(testnet) + "/tx/" + tx_id + "/hex");
                System.out.println("Fetching TX at:" + url + ")");
                var con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                var content_string = content.toString();

                byte[] raw = Kit.hexStringToByteArray(content_string);
                tx = Tx.parse(raw, testnet);

                ///if (tx.isSegwit())
                computed_id = tx.getId();


                var serial = tx.getSerialString();
                //System.out.println("DEBUG: fetched raw tx: " + serial);

                // TODO: should be a warning when computation is confirmed correct
                if (!computed_id.equals(tx_id)) {
                    System.out.println("FATAL");
                    System.out.println("computed tx id:"+computed_id);
                    System.out.println("requested tx id:"+tx_id);
                    throw new RuntimeException("Mismatching serials");
                }
                else{
                    System.out.println("Ok, tx id matches requested id!");
                    // TODO: re-enable when properly dealing with witness data
                    cache.put(computed_id,tx);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Not corresponding tx_id");
            e.printStackTrace();
        }

        return cache.get(tx_id);
    }
}