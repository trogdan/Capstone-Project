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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Defines table and column names for the marker database.
 */
public class MarkerContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.xanadu.marker.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PLACES = "places";
    public static final String PATH_BLOGS = "blogs";
    public static final String PATH_POSTS = "posts";

    public static final String PATH_PLACE = "place";
    public static final String PATH_BLOG = "blog";

    public static final String MARKER_DB_PLACE_PLACE_ID = "place_id";
    public static final String MARKER_DB_PLACE_GOO_ID = "goo_id";
    public static final String MARKER_DB_PLACE_NAME = "name";
    public static final String MARKER_DB_PLACE_ABOUT = "about";
    public static final String MARKER_DB_POST_POST_ID = "post_id";
    public static final String MARKER_DB_BLOG_BLOG_ID = "blog_id";

    /* Inner class that defines the table contents of the place table */
    public static final class PlacesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;

        // Table name
        public static final String TABLE_NAME = "places";

        // The place id is what is sent to marker to get more info about this location
        public static final String COLUMN_PLACE_ID = "place_id";

        // The goo-gle id is what is sent to google to get more info about this location
        public static final String COLUMN_GOO_ID = "goo_id";

        // Human readable location string
        public static final String COLUMN_NAME = "name";

        // Human readable description
        public static final String COLUMN_ABOUT = "about";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by marker.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildPlacesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the blogs table */
    public static final class BlogsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BLOGS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BLOGS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BLOGS;

        public static final String TABLE_NAME = "blogs";

        // The blog id is what is sent to marker to get more info about this blog
        public static final String COLUMN_BLOG_ID = "blog_id";

        // An ID used with the blogging service to get this blog
        public static final String COLUMN_SERVICE_BLOG_ID = "service_blog_id";

        // URI to an image for the blog
        public static final String COLUMN_IMAGE_URI = "image_uri";

        // Human readable description
        public static final String COLUMN_NAME = "name";

        // Human readable description
        public static final String COLUMN_DESC = "desc";

        // Date, stored as long in milliseconds since the epoch. previous is used to
        // determine if the posts for a blog needs to be updated
        public static final String COLUMN_LAST_UPDATED = "last_updated";
        public static final String COLUMN_PREV_LAST_UPDATED = "prev_last_updated";

        public static final String COLUMN_URL = "url";

        public static final String COLUMN_POST_COUNT = "post_count";

        // Used for blogger to get the next paginated set of posts
        public static final String COLUMN_NEXT_PAGE_TOKEN = "next_page_token";

        public static Uri buildBlogsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the posts table */
    public static final class PostsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POSTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POSTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POSTS;

        public static final String TABLE_NAME = "posts";

        // Column with the foreign key into the places table.
        public static final String COLUMN_PLACE_KEY = "place_id";
        // Column with the foreign key into the blogs table.
        public static final String COLUMN_BLOG_KEY = "blog_id";

        // The blog id is what is sent to marker to get more info about this post
        public static final String COLUMN_POST_ID = "post_id";

        // URI to an image for the post
        public static final String COLUMN_IMAGE_URI = "image_uri";

        // An ID used with the blogging service to get this post
        public static final String COLUMN_SERVICE_POST_ID = "service_post_id";

        // Human readable description
        public static final String COLUMN_TITLE = "title";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_PUBLISHED = "published";

        public static final String COLUMN_URL = "url";

        public static Uri buildPostsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPostsPlace(String place) {
            return CONTENT_URI.buildUpon().appendPath(place).build();
        }

        public static Uri buildPostsBlog(String blog) {
            return CONTENT_URI.buildUpon().appendPath(blog).build();
        }

        @Nullable
        public static String getPlaceFromUri(Uri uri) {
            List<String> segments = uri.getPathSegments();
            if (segments != null && segments.get(1).equals(PATH_PLACE))
                return segments.get(2);
            else
                return null;
        }

        @Nullable
        public static String getBlogFromUri(Uri uri) {
            List<String> segments = uri.getPathSegments();
            if (segments != null && segments.get(1).equals(PATH_BLOG))
                return segments.get(2);
            else
                return null;
        }
    }
}
