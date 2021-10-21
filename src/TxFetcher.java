import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class TxFetcher {

    static final HashMap<String,Tx> cache = new HashMap<>();

    public static String getURL(boolean testnet) {

        if (testnet)
            return "https://blockstream.info/testnet/api";
        else
            return "https://blockstream.info/api";
    }

    public static Tx fetch(String tx_id, boolean testnet, boolean fresh) {
        Tx tx;

        try {
            if (fresh || !(cache.containsKey(tx_id))) {
                var url = new URL(getURL(testnet) + "/tx/" + tx_id + "/hex");
                System.out.println("Fetching TX at:" + url + ")");
                var con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                var content_string = content.toString();

                byte[] raw = Kit.hexStringToByteArray(content_string);
                // check if bytes 4,5 are 00 01 for segwit
                if (raw[4] == 0) {
                    System.out.println("Warning SEGWIT detected, removing flag 00 01 (bytes 4 and 5)");
                    System.out.println("Original raw content:" + content_string);
                    var bos = new ByteArrayOutputStream();
                    bos.write(raw, 0, 4);
                    bos.write(raw, 6, raw.length - 6);
                    raw = bos.toByteArray();
                    tx = Tx.parse(raw, testnet);
                    byte[] lock_bytes = Arrays.copyOfRange(raw, raw.length - 4, raw.length);
                    tx.updateLockTime(Kit.litteEndianBytesToInt(lock_bytes).longValue());
                } else
                    tx = Tx.parse(raw, testnet);

                var serial = tx.getSerialString();
                //System.out.println("DEBUG: fetched raw tx: " + serial);


                // TODO: re-enable when properly dealing with witness data
                cache.put(tx_id,tx);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Not corresponding tx_id");
            e.printStackTrace();
        }

        return cache.get(tx_id);
    }
}