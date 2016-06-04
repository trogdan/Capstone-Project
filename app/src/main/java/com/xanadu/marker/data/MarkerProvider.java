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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MarkerProvider extends ContentProvider {

    private static final String TAG = "MarkerProvider";

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MarkerDbHelper mOpenHelper;

    static final int PLACES = 100;
    static final int BLOGS = 200;
    static final int POSTS = 300;
    static final int POSTS_WITH_PLACE= 301;
    static final int POSTS_WITH_BLOG= 302;
    static final int POSTS_WITH_POST= 303;

    private static final SQLiteQueryBuilder sPostsByPlaceQueryBuilder;
    private static final SQLiteQueryBuilder sPostsByBlogQueryBuilder;

    static{
        sPostsByPlaceQueryBuilder = new SQLiteQueryBuilder();
        
        //This is an inner join which looks like
        //posts INNER JOIN places ON posts.place_key = places._id
        sPostsByPlaceQueryBuilder.setTables(
                MarkerContract.PostsEntry.TABLE_NAME + " INNER JOIN " +
                        MarkerContract.PlacesEntry.TABLE_NAME +
                        " ON " + MarkerContract.PostsEntry.TABLE_NAME +
                        "." + MarkerContract.PostsEntry.COLUMN_PLACE_KEY +
                        " = " + MarkerContract.PlacesEntry.TABLE_NAME +
                        "." + MarkerContract.PlacesEntry._ID);

        sPostsByBlogQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //posts INNER JOIN blogs ON posts.blog_key = blogs._id
        sPostsByBlogQueryBuilder.setTables(
                MarkerContract.PostsEntry.TABLE_NAME + " INNER JOIN " +
                        MarkerContract.BlogsEntry.TABLE_NAME +
                        " ON " + MarkerContract.PostsEntry.TABLE_NAME +
                        "." + MarkerContract.PostsEntry.COLUMN_BLOG_KEY +
                        " = " + MarkerContract.BlogsEntry.TABLE_NAME +
                        "." + MarkerContract.BlogsEntry._ID);

    }

    //places.place_id = ?
    private static final String sPlaceSelection =
            MarkerContract.PlacesEntry.TABLE_NAME+
                    "." + MarkerContract.PlacesEntry.COLUMN_PLACE_ID + " = ? ";

    //blogs.blog_id = ?
    private static final String sBlogSelection =
            MarkerContract.BlogsEntry.TABLE_NAME+
                    "." + MarkerContract.BlogsEntry.COLUMN_BLOG_ID + " = ? ";

    //posts._id = ?
    private static final String sPostsIdSelection =
            MarkerContract.PostsEntry.TABLE_NAME+
                    "." + MarkerContract.PostsEntry._ID + " = ? ";

    private Cursor getPostsByPlace(Uri uri, String[] projection, String sortOrder) {
        String placeFromUri = MarkerContract.PostsEntry.getPlaceFromUri(uri);

        String[] selectionArgs = new String[]{placeFromUri};
        String selection = sPlaceSelection;

        return sPostsByPlaceQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPostsByBlog(Uri uri, String[] projection, String sortOrder) {
        String blogFromUri = MarkerContract.PostsEntry.getBlogFromUri(uri);

        String[] selectionArgs = new String[]{blogFromUri};
        String selection = sBlogSelection;

        return sPostsByBlogQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getPostsByPost(Uri uri, String[] projection, String sortOrder) {
        String postFromUri = MarkerContract.PostsEntry.getPostFromUri(uri);

        String[] selectionArgs = new String[]{postFromUri};
        String selection = sPostsIdSelection;

        return mOpenHelper.getReadableDatabase().query(
                MarkerContract.PostsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MarkerContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MarkerContract.PATH_PLACES, PLACES);
        matcher.addURI(authority, MarkerContract.PATH_BLOGS, BLOGS);
        matcher.addURI(authority, MarkerContract.PATH_POSTS, POSTS);
        matcher.addURI(authority, MarkerContract.PATH_POSTS + "/*",
                POSTS_WITH_POST);
        matcher.addURI(authority, MarkerContract.PATH_POSTS + "/" +
                MarkerContract.PATH_PLACE + "/*", POSTS_WITH_PLACE);
        matcher.addURI(authority, MarkerContract.PATH_POSTS + "/" +
                MarkerContract.PATH_BLOG + "/*", POSTS_WITH_BLOG);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MarkerDbHelper(getContext());
        mOpenHelper.getWritableDatabase().delete(MarkerContract.PlacesEntry.TABLE_NAME, null, null);
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POSTS_WITH_PLACE:
                return MarkerContract.PostsEntry.CONTENT_TYPE;
            case POSTS_WITH_BLOG:
                return MarkerContract.PostsEntry.CONTENT_TYPE;
            case POSTS_WITH_POST:
                return MarkerContract.PostsEntry.CONTENT_TYPE;
            case PLACES:
                return MarkerContract.PlacesEntry.CONTENT_TYPE;
            case BLOGS:
                return MarkerContract.BlogsEntry.CONTENT_TYPE;
            case POSTS:
                return MarkerContract.PostsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "posts/place/*"
            case POSTS_WITH_PLACE: {
                retCursor = getPostsByPlace(uri, projection, sortOrder);
                break;
            }
            // "posts/blog/*"
            case POSTS_WITH_BLOG: {
                retCursor = getPostsByBlog(uri, projection, sortOrder);
                break;
            }
            // "posts/*"
            case POSTS_WITH_POST: {
                retCursor = getPostsByPost(uri, projection, sortOrder);
                break;
            }
            // "places"
            case PLACES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MarkerContract.PlacesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "blogs"
            case BLOGS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MarkerContract.BlogsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "posts"
            case POSTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MarkerContract.PostsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLACES: {
                long _id = db.insert(MarkerContract.PlacesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MarkerContract.PlacesEntry.buildPlacesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BLOGS: {
                long _id = db.insert(MarkerContract.BlogsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MarkerContract.BlogsEntry.buildBlogsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case POSTS: {
                long _id = db.insert(MarkerContract.PostsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MarkerContract.PostsEntry.buildPostsUri(_id);
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case PLACES:
                rowsDeleted = db.delete(
                        MarkerContract.PlacesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BLOGS:
                rowsDeleted = db.delete(
                        MarkerContract.BlogsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POSTS:
                rowsDeleted = db.delete(
                        MarkerContract.PostsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PLACES:
                rowsUpdated = db.update(MarkerContract.PlacesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BLOGS:
                rowsUpdated = db.update(MarkerContract.BlogsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case POSTS:
                rowsUpdated = db.update(MarkerContract.PostsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String tableName;
        switch (match) {
            case PLACES:
                tableName = MarkerContract.PlacesEntry.TABLE_NAME;
                break;
            case BLOGS:
                tableName = MarkerContract.BlogsEntry.TABLE_NAME;
                break;
            case POSTS:
                tableName = MarkerContract.PostsEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        if (tableName != null)
        {
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(tableName, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);

            return returnCount;
        }

        return 0;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}