package com.xanadu.marker.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.xanadu.marker.data.MarkerContract.PostsEntry;
import com.xanadu.marker.data.MarkerContract.BlogsEntry;

/**
 * Helper for loading a list of posts with some blog data
 */
public class BlogPostLoader extends CursorLoader {

    public static final String LOADER_ARG_PLACE_ID = "place_id";

    public static final int _ID = 0;
    public static final int COLUMN_POST_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_PUBLISHED = 3;
    public static final int COLUMN_IMAGE_URI = 4;
    public static final int COLUMN_SERVICE_POST_ID = 5;
    public static final int COLUMN_URL = 6;
    public static final int COLUMN_CONTENT = 7;
    public static final int COLUMN_BLOG_ID = 8;
    public static final int COLUMN_BLOG_NAME = 9;

    public static BlogPostLoader newBlogPostsByPlace(Context context, int placeId) {
        return new BlogPostLoader(context, PostsEntry.buildPostsPlace(placeId));
    }

    private BlogPostLoader(Context context, Uri uri) {
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
                MarkerContract.PATH_BLOGS + "." + BlogsEntry._ID,
                MarkerContract.PATH_BLOGS + "." + BlogsEntry.COLUMN_NAME
        };
    }
}
