package cn.edu.is.dsse_notes.Async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import cn.edu.is.dsse_notes.Encryption.Encrypter;
import cn.edu.is.dsse_notes.note.NoteContent;

public class PostTask extends AsyncTask<NoteContent.NoteItem, Void, Boolean> {

    // any class that uses this post task needs to implement a listener interface
    // to perform action after posting notes, especially updating the remote ID

    public static interface PostListener {
        public void onPostComplete(NoteContent.NoteItem noteItem, Integer remoteID);
    }

    public void setListener (PostListener postListener) {
        this.postListener = postListener;
    }

    private String apiURL = "http://115.159.88.104:2118/ciphertext/";
    private PostListener postListener = null;
    private List<NoteContent.NoteItem> noteItemList = null;

    @Override
    protected Boolean doInBackground(NoteContent.NoteItem... args) {
        for (NoteContent.NoteItem item : args) {
            Log.d("PostTask","Prepare to upload: " + item.title);
        }
        HttpURLConnection urlConnection = null;
        try{
            for (NoteContent.NoteItem item : args) {
                URL url = new URL(apiURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(2000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("contentType","application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("encoding", "UTF-8");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os,"UTF-8"));
                writer.write(constructForm(item));
                Log.d("PostTask","Write Form: " + constructForm(item));
                writer.flush();
                writer.close();
                os.close();
                // urlConnection.connect();

                //display what returns the POST request

                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult >= 200 && HttpResult <= 299) {
                    InputStream stream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    // TODO: Strange problem, some times '}' is missing from the json response
                    if (buffer.charAt(buffer.length() - 1) != '}') {
                        buffer.append('}');
                    }
                    Log.d("PostTask","response is" + buffer.toString());
                    // Parse the result
                    try {
                        JSONObject jsonObject = new JSONObject(buffer.toString());
                        Log.d("PostTask","RemoteID: " + jsonObject.getInt("id"));
                        postListener.onPostComplete(item, jsonObject.getInt("id"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("PostTask", urlConnection.getResponseMessage());
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return true;
    }

    private String constructForm(NoteContent.NoteItem item) {
        try {
            String keys = URLEncoder.encode(item.getKeyString(), "UTF-8");
            JSONObject itemJsonObject = new JSONObject();
            itemJsonObject.put("localId", item.id);
            itemJsonObject.put("title", item.title);
            itemJsonObject.put("content", item.details);
            String content = URLEncoder.encode(Encrypter.encrypt(itemJsonObject.toString()),"UTF-8");
            return "keys=" + keys + "&content=" + content;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


}
