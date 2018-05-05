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
import java.util.ArrayList;

import cn.edu.is.dsse_notes.Encryption.Encrypter;
import cn.edu.is.dsse_notes.note.NoteContent;

public class SyncTask extends AsyncTask<Integer, Void, String> {

    // Listener interface for performing list update action
    public interface SyncCompleteListener {
        public void handleSyncResult(ArrayList<NoteContent.NoteItem> resultList);
    }

    private SyncCompleteListener syncCompleteListener = null;
    private ArrayList<NoteContent.NoteItem> receivedNoteList = new ArrayList<NoteContent.NoteItem>();
    private String apiURL = "http://115.159.88.104:2118/ciphertext/";

    public void setSyncCompleteListener(SyncCompleteListener syncCompleteListener) {
        this.syncCompleteListener = syncCompleteListener;
    }

    @Override
    protected String doInBackground(Integer... ints) {

        try {
            URL url = new URL(apiURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("encoding","UTF-8");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.d("SyncTask","Response Code is" + urlConnection.getResponseCode());
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream())
            );
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            Log.d("SyncTask", "Query Result is: " + stringBuilder.toString());
            if (stringBuilder.charAt(stringBuilder.length() - 1) != '}') {
                stringBuilder.append('}');
            }
            JSONObject queryResultJsonObject = new JSONObject(stringBuilder.toString());
            JSONArray resultJsonArray = queryResultJsonObject.getJSONArray("results");
            Log.d("SyncTask","Result array length is " + resultJsonArray.length());
            for (int i = 0; i < resultJsonArray.length(); i++) {
                Log.d("SyncTask","Inside add loop");
                JSONObject childResultJsonObject = resultJsonArray.getJSONObject(i);
                String remoteID = String.valueOf(childResultJsonObject.getInt("id"));
                String tags = childResultJsonObject.getString("keys");
                ArrayList<Integer> tagList = new ArrayList<Integer>();
                for (int j = 0; j < tags.length(); j++) {
                    if (tags.charAt(j) == '1') {
                        tagList.add(j);
                    }
                }
                String contentEncrypted = childResultJsonObject.getString("content");
                String contentJsonString = Encrypter.decrypt(contentEncrypted);
                JSONObject contentJsonObject = new JSONObject(contentJsonString);
                String localID = contentJsonObject.getString("localId");
                String title = contentJsonObject.getString("title");
                String content = contentJsonObject.getString("content");
                NoteContent.NoteItem resultNoteItem = new NoteContent.NoteItem(localID, title, content);
                resultNoteItem.tags = tagList;
                resultNoteItem.remoteId = remoteID;
                receivedNoteList.add(resultNoteItem);
                Log.d("SyncTask","Added note item, title: " + resultNoteItem.title);
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
        Log.d("SyncTask","received Note List Length is " + receivedNoteList.size());
        syncCompleteListener.handleSyncResult(receivedNoteList);
    }
}