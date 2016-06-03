/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xanadu.marker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xanadu.marker.data.MarkerContract.PlacesEntry;
import com.xanadu.marker.data.MarkerContract.BlogsEntry;
import com.xanadu.marker.data.MarkerContract.PostsEntry;

/**
 * Manages a local database for weather data.
 */
public class MarkerDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "marker.db";

    public MarkerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold places.
        final String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + PlacesEntry.TABLE_NAME + " (" +
                PlacesEntry._ID + " INTEGER PRIMARY KEY," +
                PlacesEntry.COLUMN_PLACE_ID + " TEXT UNIQUE ON CONFLICT REPLACE NOT NULL, " +
                PlacesEntry.COLUMN_GOO_ID + " TEXT UNIQUE ON CONFLICT REPLACE NOT NULL, " +
                PlacesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlacesEntry.COLUMN_ABOUT + " TEXT, " +
                PlacesEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                PlacesEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
                " );";
        final String SQL_CREATE_BLOGS_TABLE = "CREATE TABLE " + BlogsEntry.TABLE_NAME + " (" +
                BlogsEntry._ID + " INTEGER PRIMARY KEY," +
                BlogsEntry.COLUMN_BLOG_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                BlogsEntry.COLUMN_SERVICE_BLOG_ID + " TEXT NOT NULL, " +
                BlogsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BlogsEntry.COLUMN_DESC + " TEXT, " +
                BlogsEntry.COLUMN_IMAGE_URI + " TEXT, " +
                BlogsEntry.COLUMN_URL + " TEXT UNIQUE NOT NULL," +
                BlogsEntry.COLUMN_LAST_UPDATED + " INTEGER NOT NULL" +
                " );";
        final String SQL_CREATE_POSTS_TABLE = "CREATE TABLE " + PostsEntry.TABLE_NAME + " (" +
                PostsEntry._ID + " INTEGER PRIMARY KEY," +
                PostsEntry.COLUMN_POST_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                PostsEntry.COLUMN_BLOG_KEY + " INTEGER NOT NULL, " +
                PostsEntry.COLUMN_PLACE_KEY + " INTEGER NOT NULL, " +
                PostsEntry.COLUMN_SERVICE_POST_ID + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_IMAGE_URI + " TEXT, " +
                PostsEntry.COLUMN_URL + " TEXT UNIQUE NOT NULL, " +
                PostsEntry.COLUMN_PUBLISHED + " INTEGER NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_PLACES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BLOGS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlacesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BlogsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PostsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}
