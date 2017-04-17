package com.riya.marvel.db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by RiyaSharma on 16-04-2017.
 */
public class YourComicContract {

    public static final String CONTENT_AUTHORITY = "com.riya.marvel1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COMIC = "comic";

    public static final String PATH_COMIC_MARVEL_ID = "marvelid";
    public static final String PATH_COMIC_STARTNAME = "startname";

    public static final class ComicEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMIC).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_COMIC;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_COMIC;

        public static final String TABLE_NAME = "comic";

        public static final String COLUMN_MARVEL_ID = "mid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_URLDETAIL = "URLDetail";
        public static final String COLUMN_LANDSCAPESMALL = "landscapeSmallImageUrl";
        public static final String COLUMN_STANDARDXLARGE = "standardXLargeImageUrl";

        public static Uri buildComicUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildComicMarvelID(String marvelID) {
            return CONTENT_URI.buildUpon().appendPath(PATH_COMIC_MARVEL_ID).appendPath(marvelID).build();
        }

        public static Uri buildComicStartName(String startName) {
            return CONTENT_URI.buildUpon().appendPath(PATH_COMIC_STARTNAME).appendPath(startName).build();
        }

        public static String getMarvelIDFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getStartNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri getBaseUri(){

            return CONTENT_URI;
        }

    }
}
