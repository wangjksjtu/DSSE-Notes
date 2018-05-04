package cn.edu.is.dsse_notes.Async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import cn.edu.is.dsse_notes.note.NoteContent;

public class PutTask extends AsyncTask<NoteContent.NoteItem, Void, Boolean> {
    private String apiURL = "http://115.159.88.104:2118/ciphertext/";
    @Override
    protected Boolean doInBackground(NoteContent.NoteItem... args) {
        HttpURLConnection urlConnection = null;
        try{
            for (NoteContent.NoteItem item : args) {
                String putUrl = apiURL + item.remoteId + "/";
                URL url = new URL(putUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(2000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("contentType","application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("encoding", "UTF-8");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os,"UTF-8"));
                writer.write(constructForm(item));
                writer.flush();
                writer.close();
                os.close();
                // urlConnection.connect();

                StringBuilder sb = new StringBuilder();
                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    Log.d("PutTask", sb.toString());
                } else {
                    Log.d("PutTask", urlConnection.getResponseMessage());
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
            itemJsonObject.put("title", item.title);
            itemJsonObject.put("title", item.details);
            String content = URLEncoder.encode(itemJsonObject.toString(),"UTF-8");
            return "keys=" + keys + "&title=" + content;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
