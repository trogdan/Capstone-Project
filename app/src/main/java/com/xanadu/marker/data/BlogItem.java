package com.xanadu.marker.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.blogger.model.Blog;

/**
 * Created by dan on 6/3/16.
 */
public class BlogItem implements Parcelable {
    public static final String EXTRA_BLOG_ITEM = "extra_posts_item";

    public String name;
    public String uri;
    public String image_uri;
    public String blog_id;
    public String service_blog_id;
    public String next_page_token;
    public int _id;
    public int post_count;
    public long last_update_time;
    public long prev_last_update_time;

    // Cursor assumed to be on this item
    public BlogItem(Cursor cursor){
        this.name = cursor.getString(BlogLoader.COLUMN_NAME);
        this.uri = cursor.getString(BlogLoader.COLUMN_URL);
        this.image_uri = cursor.getString(BlogLoader.COLUMN_IMAGE_URI);
        this.blog_id = cursor.getString(BlogLoader.COLUMN_BLOG_ID);
        this.service_blog_id = cursor.getString(BlogLoader.COLUMN_SERVICE_BLOG_ID);
        this.next_page_token = cursor.getString(BlogLoader.COLUMN_NEXT_PAGE_TOKEN);
        this._id = cursor.getInt(BlogLoader._ID);
        this.post_count = cursor.getInt(BlogLoader.COLUMN_POST_COUNT);
        this.last_update_time = cursor.getLong(BlogLoader.COLUMN_LAST_UPDATED);
        this.prev_last_update_time = cursor.getLong(BlogLoader.COLUMN_PREV_LAST_UPDATED);
    }

    // Parcelling part
    public BlogItem(Parcel in){
        String[] data = new String[6];

        in.readStringArray(data);

        this.name = data[0];
        this.uri = data[1];
        this.image_uri = data[2];
        this.blog_id = data[3];
        this.service_blog_id = data[4];
        this.next_page_token = data[5];

        int[] intData = new int[2];
        in.readIntArray(intData);
        this._id = intData[0];
        this.post_count = intData[1];

        long[] longData = new long[2];
        in.readLongArray(longData);
        this.last_update_time = longData[0];
        this.prev_last_update_time = longData[1];
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.name,
                this.uri,
                this.image_uri,
                this.blog_id,
                this.service_blog_id,
                this.next_page_token
        });
        dest.writeIntArray(new int[] {
                this._id,
                this.post_count
        });
        dest.writeLongArray(new long[] {
                this.last_update_time,
                this.prev_last_update_time
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BlogItem createFromParcel(Parcel in) {
            return new BlogItem(in);
        }

        public BlogItem[] newArray(int size) {
            return new BlogItem[size];
        }
    };
}
