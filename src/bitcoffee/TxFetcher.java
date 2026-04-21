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

    public static Tx fetch(String tx_id) {
        return fetch(tx_id,false,false);
    }
    public static Tx fetch(String tx_id, boolean testnet) {
        return fetch(tx_id,testnet,false);
    }

    public static Tx fetch(String tx_id, boolean testnet, boolean fresh) {
        if (!fresh && cache.containsKey(tx_id)) {
            return cache.get(tx_id);
        }

        var urlString = getURL(testnet) + "/tx/" + tx_id + "/hex";

        try {
            var url = new URL(urlString);
            System.out.println("Fetching TX at:" + url);
            var con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                var errorBody = readResponseBody(con, true);
                throw new IllegalStateException("Failed to fetch tx " + tx_id + " from " + urlString
                        + " (HTTP " + status + ")" + (errorBody.isBlank() ? "" : ": " + errorBody));
            }

            var contentString = readResponseBody(con, false).trim();
            byte[] raw = Kit.hexStringToByteArray(contentString);
            var tx = Tx.parse(raw, testnet);
            var computedId = tx.getId();

            if (!computedId.equals(tx_id)) {
                throw new IllegalStateException("Fetched tx id mismatch: requested " + tx_id + " but got " + computedId);
            }

            cache.put(computedId, tx);
            return tx;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to fetch tx " + tx_id + " from " + urlString, e);
        }
    }

    private static String readResponseBody(HttpURLConnection connection, boolean errorStream) throws IOException {
        var stream = errorStream ? connection.getErrorStream() : connection.getInputStream();
        if (stream == null) {
            return "";
        }

        try (var in = new BufferedReader(new InputStreamReader(stream))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        }
    }
}
