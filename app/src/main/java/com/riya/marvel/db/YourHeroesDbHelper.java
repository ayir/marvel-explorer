package com.riya.marvel.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.riya.marvel.db.YourHeroesContract.PersonEntry;
/**
 * Created by RiyaSharma on 16-04-2017.
 */

public class YourHeroesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "yourheroes.db";

    public YourHeroesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_PERSON = "CREATE TABLE " + PersonEntry.TABLE_NAME + "(" +
                PersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PersonEntry.COLUMN_MARVEL_ID + " INTEGER NOT NULL, " +
                PersonEntry.COLUMN_NAME + " TEXT, " +
                PersonEntry.COLUMN_DESCRIPTION + " TEXT, " +
                PersonEntry.COLUMN_URLDETAIL + " TEXT, " +
                PersonEntry.COLUMN_LANDSCAPESMALL + " TEXT, " +
                PersonEntry.COLUMN_STANDARDXLARGE + " TEXT );";


        final String SQL_CREATE_COMIC = "CREATE TABLE " + YourComicContract.ComicEntry.TABLE_NAME + "(" +
                YourComicContract.ComicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                YourComicContract.ComicEntry.COLUMN_MARVEL_ID + " INTEGER NOT NULL, " +
                YourComicContract.ComicEntry.COLUMN_NAME + " TEXT, " +
                YourComicContract.ComicEntry.COLUMN_DESCRIPTION + " TEXT, " +
                YourComicContract.ComicEntry.COLUMN_URLDETAIL + " TEXT, " +
                YourComicContract.ComicEntry.COLUMN_LANDSCAPESMALL + " TEXT, " +
                YourComicContract.ComicEntry.COLUMN_STANDARDXLARGE + " TEXT );";

        db.execSQL(SQL_CREATE_PERSON);
        db.execSQL(SQL_CREATE_COMIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PersonEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + YourComicContract.ComicEntry.TABLE_NAME);
        onCreate(db);
    }
}
