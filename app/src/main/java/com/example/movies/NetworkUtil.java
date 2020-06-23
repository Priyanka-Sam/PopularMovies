package com.example.movies;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtil {

    final static String MOVIES_POPULAR_URL = "https://api.themoviedb.org";
    final static String API_KEY = "YOUR API KEY";

    final static String PARAM_API_KEY = "api_key";



    public static URL buildUrl(String queryType) {
        Uri builtUri = Uri.parse(MOVIES_POPULAR_URL).buildUpon()
                .appendPath("3")
                .appendPath("movie")
                .appendPath(queryType)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext())
            {

                return scanner.next();
            } else
                {
                return null;
            }
        }

        finally {
            urlConnection.disconnect();
        }
    }

}
