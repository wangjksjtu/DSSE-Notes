package cn.edu.is.dsse_notes.Async;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class QueryTask extends AsyncTask<Integer, Void, String> {

    // Listener interface for performing list update action
    public interface QueryCompleteListener {

    }

    private String apiURL = "http://115.159.88.104:2118/ciphertext/";
    @Override
    protected String doInBackground(Integer... ints) {
        // Syntax: ints is a list of all the key words (their locations)
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Integer index : ints) {
            if (first) {
                first = false;
                sb.append(index.toString());
            } else {
                sb.append("|").append(index.toString());
            }
        }
        String queryUrl = apiURL + "?key=" + sb.toString();
        Log.d("QueryTask", queryUrl);
        try {
            URL url = new URL(queryUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("encoding","UTF-8");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.d("QueryTask","Response Code is" + urlConnection.getResponseCode());
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream())
            );
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            Log.d("QueryTask", "Query Result is: " + stringBuilder.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
