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

    // Constructor
    public PostItem(
            String title,
            String uri,
            String image_uri,
            String post_id,
            String service_post_id){
        this.title = title;
        this.uri = uri;
        this.image_uri = image_uri;
        this.post_id = post_id;
        this.service_post_id = service_post_id;
    }

    // Cursor assumed to be on this item
    public PostItem(Cursor cursor){
        this.title = cursor.getString(PostLoader.Query.COLUMN_TITLE);
        this.uri = cursor.getString(PostLoader.Query.COLUMN_URL);
        this.image_uri = cursor.getString(PostLoader.Query.COLUMN_IMAGE_URI);
        this.post_id = cursor.getString(PostLoader.Query.COLUMN_POST_ID);
        this.service_post_id = cursor.getString(PostLoader.Query.COLUMN_SERVICE_POST_ID);
    }

    // Parcelling part
    public PostItem(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);

        this.title = data[0];
        this.uri = data[1];
        this.image_uri = data[2];
        this.post_id = data[3];
        this.service_post_id = data[4];
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.title,
                this.uri,
                this.image_uri,
                this.post_id,
                this.service_post_id
        });
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
