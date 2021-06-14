import org.bouncycastle.util.encoders.Hex;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.HashSet;

public class TxFetcher {

    static HashMap<String,Tx> cache;

    public static String getURL(boolean testnet) {

        if (testnet)
            return "http://testnet.programmingbitcoin.com";
        else
            return "http://mainnet.programmingbitcoin.com";
    }

    public static Tx fetch(String tx_id, boolean testnet, boolean fresh) {
        Tx tx = null;

        try {
            if (fresh || !(cache.containsKey(tx_id))) {
                var url = new URL(getURL(testnet)+"/tx/"+tx_id+".hex");
                var con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                byte[] raw  = CryptoKit.hexStringToByteArray(content.toString());
                // no inputs, only coinbase?
                if (raw[4] ==0) {
                    var bos = new ByteArrayOutputStream();
                    bos.write(raw,0,4);
                    bos.write(raw,6,raw.length-6);
                    raw = bos.toByteArray();
                    tx = Tx.parse(raw,testnet);
                }
                tx = Tx.parse(raw,testnet);

                if (!tx.getId().equals(tx_id)) {
                    System.out.println("tx.getID:" + tx.getId());
                    System.out.println("tx_id:" + tx_id);
                    throw new Exception("my exception");
                }
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

        cache.put(tx_id,tx);
        return tx;
    }
}