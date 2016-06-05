package com.xanadu.marker.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dan on 6/3/16.
 */
public class PostItem implements Parcelable {
    public static final String EXTRA_POSTS_ITEM = "extra_posts_item";

    public String title;
    public String uri;
    public String image_uri;
    public String post_id;
    public String service_post_id;
    public int _id;
    public long published;
    public BlogItem blogItem;

    // Cursor assumed to be on this item
    public PostItem(Cursor cursor, BlogItem blogItem){
        this.title = cursor.getString(PostLoader.COLUMN_TITLE);
        this.uri = cursor.getString(PostLoader.COLUMN_URL);
        this.image_uri = cursor.getString(PostLoader.COLUMN_IMAGE_URI);
        this.post_id = cursor.getString(PostLoader.COLUMN_POST_ID);
        this.service_post_id = cursor.getString(PostLoader.COLUMN_SERVICE_POST_ID);
        this._id = cursor.getInt(PostLoader._ID);
        this.published = cursor.getLong(PostLoader.COLUMN_PUBLISHED);
        this.blogItem = blogItem;
    }

    // Parcelling part
    public PostItem(Parcel in){
        this.blogItem = in.readParcelable(BlogItem.class.getClassLoader());

        String[] data = new String[5];

        in.readStringArray(data);

        this.title = data[0];
        this.uri = data[1];
        this.image_uri = data[2];
        this.post_id = data[3];
        this.service_post_id = data[4];

        this._id = in.readInt();

        this.published = in.readLong();
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.blogItem, flags);
        dest.writeStringArray(new String[] {
                this.title,
                this.uri,
                this.image_uri,
                this.post_id,
                this.service_post_id
        });
        dest.writeInt(_id);
        dest.writeLong(published);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PostItem createFromParcel(Parcel in) {
            return new PostItem(in);
        }

        public PostItem[] newArray(int size) {
            return new PostItem[size];
        }
    };
}
