package com.xanadu.marker.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.xanadu.marker.data.MarkerContract.BlogsEntry;

/**
 * Helper for loading a list of places or a single place.
 */
public class BlogLoader extends CursorLoader {

    public static final int _ID = 0;
    public static final int COLUMN_BLOG_ID = 1;
    public static final int COLUMN_NAME = 2;
    public static final int COLUMN_DESC = 3;
    public static final int COLUMN_IMAGE_URI = 4;
    public static final int COLUMN_SERVICE_BLOG_ID = 5;
    public static final int COLUMN_URL = 6;
    public static final int COLUMN_LAST_UPDATED = 7;
    public static final int COLUMN_PREV_LAST_UPDATED = 8;
    public static final int COLUMN_POST_COUNT = 9;
    public static final int COLUMN_NEXT_PAGE_TOKEN = 10;

    public static BlogLoader newAllBlogsInstance(Context context) {
        return new BlogLoader(context, BlogsEntry.CONTENT_URI);
    }

    public static BlogLoader newInstanceForItemId(Context context, long itemId) {
        return new BlogLoader(context, MarkerContract.BlogsEntry.buildBlogsUri(itemId));
    }
    
    private BlogLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public interface Query {
        String[] PROJECTION = {
                BlogsEntry._ID,
                BlogsEntry.COLUMN_BLOG_ID,
                BlogsEntry.COLUMN_NAME,
                BlogsEntry.COLUMN_DESC,
                BlogsEntry.COLUMN_IMAGE_URI,
                BlogsEntry.COLUMN_SERVICE_BLOG_ID,
                BlogsEntry.COLUMN_URL,
                BlogsEntry.COLUMN_LAST_UPDATED,
                BlogsEntry.COLUMN_PREV_LAST_UPDATED,
                BlogsEntry.COLUMN_POST_COUNT,
                BlogsEntry.COLUMN_NEXT_PAGE_TOKEN
        };
    }


}
