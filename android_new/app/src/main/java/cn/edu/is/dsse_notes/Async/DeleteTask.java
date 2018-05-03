package cn.edu.is.dsse_notes.Async;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DeleteTask extends AsyncTask<Integer, Void, String> {

    private String apiURL = "http://115.159.88.104:2118/ciphertext/";
    @Override
    protected String doInBackground(Integer... ints) {
        // Syntax: ints is an Integer list of remote IDs to delete

        for (Integer remoteID : ints) {
            String deleteURL = apiURL + remoteID.toString() + "/";
            try {
                URL url = new URL(deleteURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestProperty("encoding", "UTF-8");
                urlConnection.setRequestMethod("DELETE");
                urlConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream())
                );
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                Log.d("DeleteTask","Delete Task for " +
                        remoteID.toString() + "returned:\n" + stringBuilder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
