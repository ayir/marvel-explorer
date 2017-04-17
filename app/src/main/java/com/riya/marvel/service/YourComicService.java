package com.riya.marvel.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.riya.marvel.ComicListFragment;
import com.riya.marvel.db.Comic;
import com.riya.marvel.db.YourComicContract;
import com.riya.marvel.utilities.ImageFormat;
import com.riya.marvel.utilities.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;


public class YourComicService extends IntentService {

    private final String LOG_TAG = YourComicService.class.getSimpleName();


    private  Uri builtUri;
    public YourComicService() {
        super("YourHeroes");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String characterJsonStr = null;

        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String apikey = Tools.PUBLIC_KEY;
        String hash = Tools.md5(ts + Tools.PRIVATE_KEY + Tools.PUBLIC_KEY);
        String order = "name"; // name, modified, -name, -modified (- is descending order)

        try {

            final String COMIC_BASE_URL = "http://gateway.marvel.com/v1/public/comics";
            final String TIMESTAMP = "ts";
            final String API_KEY = "apikey";
            final String HASH = "hash";
            final String ORDER = "orderBy";




                builtUri = Uri.parse(COMIC_BASE_URL + "?").buildUpon()
                        // .appendQueryParameter(QUERY_PARAM, param)
                        .appendQueryParameter(TIMESTAMP, ts)
                        .appendQueryParameter(API_KEY, apikey)
                        .appendQueryParameter(HASH, hash)
                        .build();




            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.d(LOG_TAG, "Response: "+urlConnection.getResponseCode()+" - "+urlConnection.getResponseMessage());

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            characterJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Character string: " + characterJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        if (characterJsonStr != null) {
            ArrayList<Comic> comics = null;
            try {
                comics = getComicDataFromJson(characterJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            Vector<ContentValues> vcComics = new Vector<ContentValues>(comics.size());
            for (Comic c : comics) {
                ContentValues comicValues = new ContentValues();
                comicValues.put(YourComicContract.ComicEntry.COLUMN_MARVEL_ID, c.getId());
                comicValues.put(YourComicContract.ComicEntry.COLUMN_NAME, c.getName());
                comicValues.put(YourComicContract.ComicEntry.COLUMN_DESCRIPTION, c.getDescription());
                comicValues.put(YourComicContract.ComicEntry.COLUMN_URLDETAIL, c.getURLDetail());
                comicValues.put(YourComicContract.ComicEntry.COLUMN_LANDSCAPESMALL, c.getLandscapeSmallImageUrl());
                comicValues.put(YourComicContract.ComicEntry.COLUMN_STANDARDXLARGE, c.getStandardXLargeImageUrl());
                vcComics.add(comicValues);
            }
            if (vcComics.size() > 0) {
                ContentValues[] arComic = new ContentValues[vcComics.size()];
                vcComics.toArray(arComic);
                try {
                    // delete all records before super insert!
                    this.getContentResolver().delete(YourComicContract.ComicEntry.CONTENT_URI, null, null);

                    this.getContentResolver().bulkInsert(YourComicContract.ComicEntry.CONTENT_URI, arComic);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            // Sending message that the task was finished
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ComicListFragment.ServiceResponseReceive.ACTION_SERVICE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);
        }
    }


    public ArrayList<Comic> getComicDataFromJson(String comicJsonStr)
            throws JSONException {

        JSONObject wrapper = new JSONObject(comicJsonStr);
        JSONObject data = wrapper.getJSONObject("data");
        JSONArray comics = data.getJSONArray("results");

        ArrayList<Comic> chcs = new ArrayList<>();

        for(int i = 0; i < comics.length(); i++) {
            JSONObject jsonComic = comics.getJSONObject(i);
            int id = jsonComic.getInt("id");
            String name = jsonComic.getString("title");
            String description = jsonComic.getString("description");

            String URLDetail = "";
            String URLReader = "";

            JSONArray urlsArray = jsonComic.getJSONArray("urls"); //detail, wiki, comiclink
            for(int u = 0; u < urlsArray.length(); u++) {
                JSONObject jsonUrl = urlsArray.getJSONObject(u);
                String type = jsonUrl.getString("type");
                String url = jsonUrl.getString("url");
                if (type.equals("detail")) {
                    URLDetail = url;
                }
                if (type.equals("reader")) {
                    URLReader = url;
                }

            }
            // I decide URLDetail is priority. I like the info there :)
            if (URLDetail.equals("")) {
                if (!URLReader.equals("")) // if detail is null use the wiki url
                    URLDetail = URLReader;

            }

            String landscapeSmallImageUrl;
            String standardXLargeImageUrl;
            if (!jsonComic.isNull("thumbnail")) {
                JSONObject thumbnail = jsonComic.getJSONObject("thumbnail");
                landscapeSmallImageUrl = thumbnail.getString("path") + "/" + ImageFormat.LANDSCAPE_SMALL + "." + thumbnail.getString("extension");
                standardXLargeImageUrl = thumbnail.getString("path") + "/" + ImageFormat.STANDARD_FANTASTIC + "." + thumbnail.getString("extension");
            } else {
                landscapeSmallImageUrl = "";
                standardXLargeImageUrl = "";
            }

            Comic comic = new Comic();
            comic.setId(id);
            comic.setName(name);
            comic.setDescription(description);
            comic.setURLDetail(URLDetail);
            comic.setLandscapeSmallImageUrl(landscapeSmallImageUrl);
            comic.setStandardXLargeImageUrl(standardXLargeImageUrl);

            chcs.add(comic);
        }
        return chcs;

    }



}
