package com.xanadu.marker.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.xanadu.marker.data.MarkerContract.PostsEntry;

/**
 * Helper for loading a list of places or a single place.
 */
public class PostLoader extends CursorLoader {

    public static final int _ID = 0;
    public static final int COLUMN_POST_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_PUBLISHED = 3;
    public static final int COLUMN_IMAGE_URI = 4;
    public static final int COLUMN_SERVICE_POST_ID = 5;
    public static final int COLUMN_URL = 6;
    public static final int COLUMN_CONTENT = 7;
    public static final int COLUMN_PLACE_KEY = 7;

    public static PostLoader newAllPostsInstance(Context context) {
        return new PostLoader(context, PostsEntry.CONTENT_URI);
    }

    public static PostLoader newBlogPostsInstance(Context context, int blogId) {
        return new PostLoader(context, PostsEntry.buildPostsBlog(blogId));
    }
    public static PostLoader newPlacePostsInstance(Context context, int placeId) {
        return new PostLoader(context, PostsEntry.buildPostsPlace(placeId));
    }

    public static PostLoader newInstanceForItemId(Context context, long itemId) {
        return new PostLoader(context, PostsEntry.buildPostsUri(itemId));
    }

    private PostLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public interface Query {
        String[] PROJECTION = {
                MarkerContract.PATH_POSTS + "." + PostsEntry._ID,
                MarkerContract.PATH_POSTS + "." + PostsEntry.COLUMN_POST_ID,
                MarkerContract.PATH_POSTS + "." + PostsEntry.COLUMN_TITLE,
                MarkerContract.PATH_POSTS + "." + PostsEntry.COLUMN_PUBLISHED,
                MarkerContract.PATH_POSTS + "." + PostsEntry.COLUMN_IMAGE_URI,
                MarkerContract.PATH_POSTS + "." + PostsEntry.COLUMN_SERVICE_POST_ID,
                MarkerContract.PATH_POSTS + "." + PostsEntry.COLUMN_URL,
                MarkerContract.PATH_POSTS + "." + PostsEntry.COLUMN_CONTENT,
                MarkerContract.PATH_POSTS + "." + PostsEntry.COLUMN_PLACE_KEY
        };

    }


}
