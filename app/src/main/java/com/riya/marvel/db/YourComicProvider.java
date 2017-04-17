package com.riya.marvel.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by RiyaSharma on 16-04-2017.
 */
public class YourComicProvider extends ContentProvider {

    private final String LOG_TAG = YourComicProvider.class.getSimpleName();

    private static final UriMatcher fUriMatcher = buildUriMatcher();
    private YourHeroesDbHelper fDbHelper;

    private static final int COMIC = 100;
    private static final int COMIC_MARVEL_ID = 101;
    private static final int COMIC_STARTNAME = 102;

    @Override
    public boolean onCreate() {
        fDbHelper = new YourHeroesDbHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = YourComicContract.CONTENT_AUTHORITY;

        // Each type of uri with you corresponding code!
        matcher.addURI(authority, YourComicContract.PATH_COMIC,COMIC);
        matcher.addURI(authority, YourComicContract.PATH_COMIC+"/"+YourComicContract.PATH_COMIC_MARVEL_ID+"/#", COMIC_MARVEL_ID);
        matcher.addURI(authority, YourComicContract.PATH_COMIC+"/"+YourComicContract.PATH_COMIC_STARTNAME+"/*", COMIC_STARTNAME);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        // Verifying whats kind o uri we have...
        final int match = fUriMatcher.match(uri);
        switch (match) {
            case COMIC:
                return YourComicContract.ComicEntry.CONTENT_TYPE;
            case COMIC_MARVEL_ID:
                return YourComicContract.ComicEntry.CONTENT_ITEM_TYPE;
            case COMIC_STARTNAME:
                return YourComicContract.ComicEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor rCursor;

        Log.d(LOG_TAG, uri.toString());

        switch (fUriMatcher.match(uri)) {
            case COMIC:
                Log.d(LOG_TAG, "COMIC");
                rCursor = fDbHelper.getReadableDatabase().query(
                        YourComicContract.ComicEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;

            case COMIC_MARVEL_ID:
                Log.d(LOG_TAG, "COMIC_MARVEL_ID");
                String marvelID = YourComicContract.ComicEntry.getMarvelIDFromUri(uri);

                selection = YourComicContract.ComicEntry.TABLE_NAME+"."+
                        YourComicContract.ComicEntry.COLUMN_MARVEL_ID + " = ?";
                selectionArgs = new String[]{marvelID};

                rCursor = fDbHelper.getReadableDatabase().query(
                        YourComicContract.ComicEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;

            case COMIC_STARTNAME:
                Log.d(LOG_TAG, "COMIC_STARTNAME");
                Log.d(LOG_TAG, uri+" - Query by getStartName: " + YourComicContract.ComicEntry.getStartNameFromUri(uri) );
                String startName = YourComicContract.ComicEntry.getStartNameFromUri(uri);

                selection = YourComicContract.ComicEntry.TABLE_NAME+"."+
                        YourComicContract.ComicEntry.COLUMN_NAME + " like ?";
                selectionArgs = new String[]{startName+"%"};
                sortOrder = YourComicContract.ComicEntry.COLUMN_NAME;

                rCursor = fDbHelper.getReadableDatabase().query(
                        YourComicContract.ComicEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        rCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return rCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = fDbHelper.getWritableDatabase();
        final int match = fUriMatcher.match(uri);
        Uri returnUri;

        Log.d(LOG_TAG, uri.toString()+" - id: "+fUriMatcher.match(uri));

        switch (match) {
            case COMIC: {
                long _id = db.insert(YourComicContract.ComicEntry.TABLE_NAME, null, values);
                if (_id  > 0)
                    returnUri = YourComicContract.ComicEntry.buildComicUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = fDbHelper.getWritableDatabase();
        final int match = fUriMatcher.match(uri);
        switch (match) {
            case COMIC:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(YourComicContract.ComicEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Log.d(LOG_TAG, "Insert "+returnCount+" notifying.");
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = fDbHelper.getWritableDatabase();
        final int match = fUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case COMIC:
                rowsDeleted = db.delete(YourComicContract.ComicEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            Log.d(LOG_TAG, "delete ...");
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = fDbHelper.getWritableDatabase();
        final int match = fUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case COMIC:
                rowsUpdated = db.update(YourComicContract.ComicEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
