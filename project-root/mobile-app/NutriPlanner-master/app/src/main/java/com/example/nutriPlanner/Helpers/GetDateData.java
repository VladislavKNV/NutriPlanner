package com.example.nutriPlanner.Helpers;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetDateData extends AsyncTask<Void, Void, String> {
    private DownloadDataListener listener;

    // интерфейс для обратного вызова результата
    public interface DownloadDataListener {
        void onDataDownloaded(String result);
    }

    // Метод для установки слушателя
    public void setDownloadDataListener(DownloadDataListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            // получение текущей даты с сервера
            URL url = new URL("https://your-server-url.com/currentDateTime");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                connection.disconnect();

                // парсинг JSON поля "datetime"
                String jsonResponse = response.toString();
                String datetime = jsonResponse.substring(jsonResponse.indexOf("\"datetime\":\"") + 12, jsonResponse.indexOf("\"", jsonResponse.indexOf("\"datetime\":\"") + 12));

                return datetime;
            } else {
                connection.disconnect();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onDataDownloaded(result);
        }
    }
}
