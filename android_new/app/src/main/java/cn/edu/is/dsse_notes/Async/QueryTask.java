package cn.edu.is.dsse_notes.Async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import cn.edu.is.dsse_notes.note.NoteContent;

public class QueryTask extends AsyncTask<Integer, Void, String> {

    // Listener interface for performing list update action
    public interface QueryCompleteListener {
        public void handleResult(ArrayList<NoteContent.NoteItem> resultList);
    }

    private QueryCompleteListener queryCompleteListener = null;
    private ArrayList<NoteContent.NoteItem> receivedNoteList = new ArrayList<NoteContent.NoteItem>();
    private String apiURL = "http://115.159.88.104:2118/ciphertext/";

    public void setQueryCompleteListener(QueryCompleteListener queryCompleteListener) {
        this.queryCompleteListener = queryCompleteListener;
    }

    @Override
    protected String doInBackground(Integer... ints) {

        if (ints.length == 0) {
            return "empty";
        }
        
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
            if (stringBuilder.charAt(stringBuilder.length() - 1) != '}') {
                stringBuilder.append('}');
            }
            JSONObject queryResultJsonObject = new JSONObject(stringBuilder.toString());
            JSONArray resultJsonArray = queryResultJsonObject.getJSONArray("results");
            for (int i = 0; i < resultJsonArray.length(); i++) {
                JSONObject childResultJsonObject = resultJsonArray.getJSONObject(i);
                String remoteID = String.valueOf(childResultJsonObject.getInt("id"));
                String tags = childResultJsonObject.getString("keys");
                ArrayList<Integer> tagList = new ArrayList<Integer>();
                for (int j = 0; i < tags.length(); i++) {
                    if (tags.charAt(i) == '1') {
                        tagList.add(j);
                    }
                }
                String contentJsonString = childResultJsonObject.getString("content");
                JSONObject contentJsonObject = new JSONObject(contentJsonString);
                String localID = contentJsonObject.getString("localId");
                String title = contentJsonObject.getString("title");
                String content = contentJsonObject.getString("content");
                NoteContent.NoteItem resultNoteItem = new NoteContent.NoteItem(localID, title, content);
                resultNoteItem.tags = tagList;
                resultNoteItem.remoteId = remoteID;
                receivedNoteList.add(resultNoteItem);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("QueryTask","Json exception");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        queryCompleteListener.handleResult(receivedNoteList);
    }
}
