package com.nullpointerexception.cicerone.components;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *      ImageFetcher
 *
 *      Get link of first image found on Google Images for a given key-word
 *
 *      @author Luca
 */
public class ImageFetcher
{
    /**   Url to search  */
    private String url;
    /**   Html retrieved from Google Images  */
    private String html;
    /**   Implementation of callback method  */
    private OnImageFoundListener onImageFoundListener;

    public ImageFetcher() { }

    /**
     *      Search on Google Images the giver key-word.
     *
     *      @param subject  Key-word to search
     *      @param onImageFoundListener Implementation of callback method
     */
    public void findSubject(String subject, OnImageFoundListener onImageFoundListener)
    {
        this.onImageFoundListener = onImageFoundListener;
        this.url = "https://www.google.com/search?q=" + subject.toLowerCase() + "&source=lnms&tbm=isch";
        new Executor().execute();
    }

    /**   Interface that gives a callback method to call when search ended  */
    public interface OnImageFoundListener { void onImageFound(String url); void onError(); }

    /**
     *      Class that make operations on another thread
     */
    private class Executor extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... strings)
        {
            try
            {
                html = getHtml();

                if(onImageFoundListener != null)
                    onImageFoundListener.onImageFound( getImageLink() );
            }
            catch (IOException e)
            {
                Log.e("ImageFetcher", "Error: " + e.toString());

                if(onImageFoundListener != null)
                    onImageFoundListener.onError();
            }

            return null;
        }

        /**
         *      Get HTML content of the search result
         *
         *      @return Html content of page searched
         *      @throws IOException
         */
        private String getHtml() throws IOException
        {
            // Build and set timeout values for the request.
            URLConnection connection = (new URL(url)).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            // Read and store the result line by line then return the entire string.
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder html = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; )
            {
                html.append(line);
            }
            in.close();

            return html.toString();
        }

        /**
         *      Parse HTML page to find the first image url
         *
         *      @return url of first image
         */
        private String getImageLink()
        {
            String result = "";

            result = html.substring( html.indexOf("data-iurl=") + "data-iurl=".length()+1,
                    html.indexOf("\"", html.indexOf("data-iurl=") + "data-iurl=".length()+1))
                    .replace("\"", "");

            return result;
        }
    }
}
