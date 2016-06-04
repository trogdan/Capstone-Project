package com.xanadu.marker.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

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

    // Constructor
    public BlogItem(
            String name,
            String uri,
            String image_uri,
            String blog_id,
            String service_blog_id){
        this.name = name;
        this.uri = uri;
        this.image_uri = image_uri;
        this.blog_id = blog_id;
        this.service_blog_id = service_blog_id;
    }

    // Cursor assumed to be on this item
    public BlogItem(Cursor cursor){
        this.name = cursor.getString(BlogLoader.Query.COLUMN_NAME);
        this.uri = cursor.getString(BlogLoader.Query.COLUMN_URL);
        this.image_uri = cursor.getString(BlogLoader.Query.COLUMN_IMAGE_URI);
        this.blog_id = cursor.getString(BlogLoader.Query.COLUMN_BLOG_ID);
        this.service_blog_id = cursor.getString(BlogLoader.Query.COLUMN_SERVICE_BLOG_ID);
    }

    // Parcelling part
    public BlogItem(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);

        this.name = data[0];
        this.uri = data[1];
        this.image_uri = data[2];
        this.blog_id = data[3];
        this.service_blog_id = data[4];
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
                this.service_blog_id
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
