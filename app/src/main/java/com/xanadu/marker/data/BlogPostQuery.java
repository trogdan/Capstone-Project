package com.xanadu.marker.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dan on 6/6/16.
 */
public class BlogPostQuery implements Parcelable {
    public static final String EXTRA_BLOG_POSTS_QUERY_ITEM = "extra_blog_posts_query";

    public String service_blog_id;
    public String service_post_id;
    public String place_id;

    // Cursor assumed to be on this item
    public BlogPostQuery(String service_blog_id, String service_post_id, String place_id) {
        this.service_post_id = service_post_id;
        this.service_blog_id = service_blog_id;
        this.place_id = place_id;
    }

    // Parcelling part
    public BlogPostQuery(Parcel in) {
        this.service_blog_id = in.readString();
        this.service_post_id = in.readString();
        this.place_id = in.readString();

    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service_blog_id);
        dest.writeString(service_post_id);
        dest.writeString(place_id);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BlogPostQuery createFromParcel(Parcel in) {
            return new BlogPostQuery(in);
        }

        public BlogPostQuery[] newArray(int size) {
            return new BlogPostQuery[size];
        }

    };

}
