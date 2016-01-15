package com.helpernet.nico.accelerometertest;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nico on 04/01/16.
 */
public class Uploader {

    private final static String TAG = "Uploader";

    private final static String BASE_URL = "http://87.106.89.243:4000/store/";

    static List<String> getStoredRides() {
        File[] files = new File(SensorLoggerService.FILE_PATH).listFiles();
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    static void sendAllStoredRides() {
        for (String fileName : getStoredRides()) {
            sendData(fileName);
        }
    }

    static boolean deleteFile(String fileName) {
        return new File(SensorLoggerService.FILE_PATH + fileName).delete();
    }

    static void sendData(String fileName) {
        Log.d(TAG, "sending File: " + fileName);
        new AsyncSendDataTask().execute(fileName);
    }

    private static class AsyncSendDataTask extends AsyncTask<String, Void, Void> {

        private String fileName;

        @Override
        protected Void doInBackground(String... params) {
            fileName = params[0];
            boolean success;
            try {
                success = postData(fileName);
                if(success) {
                    Log.d(TAG, "Delete file: " + fileName);
                    deleteFile(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        boolean postData(String fileName) throws IOException {
            String name = fileName.replace(".csv", "");
            BufferedReader reader = new BufferedReader(new FileReader(SensorLoggerService.FILE_PATH + fileName));
            URL url = new URL(BASE_URL + name);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            boolean success = false;
            String charset = "utf-8";
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Accept-Charset", charset);
                urlConnection.setRequestProperty("Content-Type", "text/plain;charset=" + charset);

                OutputStream writer = urlConnection.getOutputStream();

                String line;
                String data = "";
                while ((line = reader.readLine()) != null) {
                    data += line + "\n";
                }

                writer.write(data.getBytes());
                writer.flush();
                writer.close();

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    success = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                urlConnection.disconnect();
            }
            return success;
        }
    }
}
