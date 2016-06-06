package com.xanadu.marker.remote;


import android.util.Log;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.blogger.Blogger;

import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;

import java.io.IOException;

/**
 * Created by dan on 5/30/16.
 */
public class BloggerApiUtil {
    private static final String TAG = "BloggerApiUtil";

    // Configure the Java API Client for Installed Native App
    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new JacksonFactory();

    // Construct the Blogger API access facade object.

    private static final Blogger.Builder builder = new Blogger.Builder(transport, jsonFactory, null)
            .setApplicationName("marker-1319");
    private static final Blogger.Blogs blogs = builder.build().blogs();
    private static final Blogger.Posts posts = builder.build().posts();

    private BloggerApiUtil() {}

    public static Blog fetchBlog(String blogUrl) {
        try {
            Blogger.Blogs.GetByUrl getter = blogs.getByUrl(blogUrl);
            getter.setKey("AIzaSyAKhkqh332VCZRqOGxkXeCfQwDj5rZGCfY");
            Blog blog = getter.execute();
            return blog;
            //Log.d(TAG, "Blog title: " + blog.getName());
        } catch(IOException e)
        {
            Log.e(TAG, "Exception fetching blog", e);
        }

        return null;
    }

    public static Blog fetchBlogById(String blogId) {
        try {
            Blogger.Blogs.Get getter = blogs.get(blogId);
            getter.setKey("AIzaSyAKhkqh332VCZRqOGxkXeCfQwDj5rZGCfY");
            Blog blog = getter.execute();
            return blog;
            //Log.d(TAG, "Blog title: " + blog.getName());
        } catch(IOException e)
        {
            Log.e(TAG, "Exception fetching blog", e);
        }

        return null;
    }

    public static PostList fetchPosts(String blogId, Long maxResults, String nextPageToken)
    {
        try {
            Blogger.Posts.List lister = posts.list(blogId);
            lister.setKey("AIzaSyAKhkqh332VCZRqOGxkXeCfQwDj5rZGCfY");

            if (nextPageToken != null)
            {
                lister.setPageToken(nextPageToken);
            }

            // Restrict the result content to just the data we need.
            lister.setFetchImages(true);
            lister.setFields("items(id,author/displayName,published,title,url,images),nextPageToken");
            lister.setMaxResults(maxResults);

            return lister.execute();
        } catch(IOException e)
        {
            Log.e(TAG, "Exception fetching blog", e);
        }

        return null;
    }

    public static Post fetchPost(String blogId, String postId)
    {
        try {
            Blogger.Posts.Get getter = posts.get(blogId, postId);
            getter.setKey("AIzaSyAKhkqh332VCZRqOGxkXeCfQwDj5rZGCfY");
            getter.setFetchImages(true);
            getter.setFetchBody(true);

            // Restrict the result content to just the data we need.
            return getter.execute();
        } catch(IOException e)
        {
            Log.e(TAG, "Exception fetching blog", e);
        }

        return null;
    }
}
