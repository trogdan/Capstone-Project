package com.xanadu.marker.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Geometry;
import com.cocoahero.android.geojson.MultiPoint;
import com.cocoahero.android.geojson.Position;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import com.xanadu.marker.remote.BloggerApiUtil;
import com.xanadu.marker.remote.MarkerWfsUtil;
import com.xanadu.marker.data.MarkerContract.PlacesEntry;
import com.xanadu.marker.data.MarkerContract.BlogsEntry;
import com.xanadu.marker.data.MarkerContract.PostsEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.xanadu.marker.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.xanadu.marker.intent.extra.REFRESHING";

    public static final String EXTRA_WFS_QUERY_BOX
            = "com.xanadu.marker.intent.extra.WFS_QUERY_BOX";

    public static final String EXTRA_BLOGGER_BLOGS_UPDATE
            = "com.xanadu.marker.intent.extra.BLOGGER_BLOGS_UPDATE";

    public static final String EXTRA_BLOGGER_BLOG_UPDATE
            = "com.xanadu.marker.intent.extra.BLOGGER_BLOG_UPDATE";

    public static final String EXTRA_BLOGGER_POST_UPDATE
            = "com.xanadu.marker.intent.extra.BLOGGER_POST_UPDATE";


    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }

        if (intent.hasExtra(EXTRA_WFS_QUERY_BOX)) {
            // Delete all items
            getContentResolver().delete(PlacesEntry.CONTENT_URI, null, null);

            LatLngBounds box = intent.getParcelableExtra(EXTRA_WFS_QUERY_BOX);

            try {
                JSONObject jsonObj = MarkerWfsUtil.fetchMarkerPlacesInBox(box);
                if (jsonObj == null) {
                    throw new JSONException("Invalid parsed json" );
                }

                GeoJSONObject geoJSON = GeoJSON.parse(jsonObj);

                if (geoJSON.getType() != GeoJSON.TYPE_FEATURE_COLLECTION) {
                    throw new JSONException("Unexpected geojson content");
                }

                List<Feature> features = ((FeatureCollection) geoJSON).getFeatures();

                ContentValues[] values = new ContentValues[features.size()];

                //Parse and Pass the results to the content provider
                for (int i = 0; i < features.size(); i++) {
                    Feature feature = features.get(i);

                    Geometry geom = feature.getGeometry();

                    // Only handling multipoints, eventually maybe switch to points
                    if (geom.getType() != GeoJSON.TYPE_MULTI_POINT) {
                        continue;
                    }

                    MultiPoint point = (MultiPoint)geom;
                    List<Position> positions = point.getPositions();
                    JSONObject properties = feature.getProperties();

                    ContentValues value = values[i] = new ContentValues();

                    value.put(PlacesEntry.COLUMN_PLACE_ID, properties.getString(MarkerContract.MARKER_DB_PLACE_PLACE_ID));
                    value.put(PlacesEntry.COLUMN_GOO_ID, properties.getString(MarkerContract.MARKER_DB_PLACE_GOO_ID));
                    value.put(PlacesEntry.COLUMN_COORD_LAT, positions.get(0).getLatitude());
                    value.put(PlacesEntry.COLUMN_COORD_LONG, positions.get(0).getLongitude());
                    value.put(PlacesEntry.COLUMN_NAME, properties.getString(MarkerContract.MARKER_DB_PLACE_NAME));
                    value.put(PlacesEntry.COLUMN_ABOUT, properties.getString(MarkerContract.MARKER_DB_PLACE_ABOUT));
                }

                getContentResolver().bulkInsert(PlacesEntry.CONTENT_URI, values);

            } catch (JSONException e) {
                Log.e(TAG, "Error updating content.", e);
            }
        }
        else if (intent.hasExtra(EXTRA_BLOGGER_BLOGS_UPDATE)) {
            String url = intent.getStringExtra(EXTRA_BLOGGER_BLOGS_UPDATE);
            Blog blog = BloggerApiUtil.fetchBlog(url);

            if (blog != null) {

                // First, check if the this id exists, and get the last updated time,
                // and see if it's different than this time
                Cursor blogCursor = getContentResolver().query(
                        MarkerContract.BlogsEntry.CONTENT_URI,
                        new String[] { BlogsEntry.COLUMN_LAST_UPDATED },
                        MarkerContract.BlogsEntry.COLUMN_SERVICE_BLOG_ID + " = ?",
                        new String[]{blog.getId()},
                        null);

                // We should only have one
                if (blogCursor.getCount() == 1) {
                    // OK, this blog is already in the DB, check the last update time, to see if we need to
                    // update the record
                    blogCursor.moveToNext();
                    long lastUpdateTime = blogCursor.getInt(0);

                    if (lastUpdateTime != blog.getUpdated().getValue()) {
                        // is it different, if so, update columns that may have changed, and set the previous
                        // last update time
                        ContentValues value = new ContentValues();

                        value.put(BlogsEntry.COLUMN_SERVICE_BLOG_ID, blog.getId());
                        value.put(BlogsEntry.COLUMN_URL, blog.getUrl());
                        value.put(BlogsEntry.COLUMN_NAME, blog.getName());
                        value.put(BlogsEntry.COLUMN_NAME, blog.getDescription());
                        value.put(BlogsEntry.COLUMN_LAST_UPDATED, blog.getUpdated().getValue());
                        value.put(BlogsEntry.COLUMN_PREV_LAST_UPDATED, lastUpdateTime);
                        value.put(BlogsEntry.COLUMN_POST_COUNT, blog.getPosts().size());
                        //TODO other fields

                        getContentResolver().update(BlogsEntry.CONTENT_URI,
                                value,
                                MarkerContract.BlogsEntry.COLUMN_SERVICE_BLOG_ID + " = ?",
                                new String[]{blog.getId()});
                    }
                    // is it the same? if so, don't bother

                }
                else if (blogCursor.getCount() > 1) {
                    Log.e(TAG, "Found duplicate service_blog_id: {" + blog.getId() + "}");
                }
                else {
                    ContentValues value = new ContentValues();

                    value.put(BlogsEntry.COLUMN_SERVICE_BLOG_ID, blog.getId());
                    value.put(BlogsEntry.COLUMN_URL, blog.getUrl());
                    value.put(BlogsEntry.COLUMN_NAME, blog.getName());
                    value.put(BlogsEntry.COLUMN_DESC, blog.getDescription());
                    value.put(BlogsEntry.COLUMN_LAST_UPDATED, blog.getUpdated().getValue());
                    Log.d(TAG, "Received updated time " + blog.getUpdated().getValue());
                    value.put(BlogsEntry.COLUMN_POST_COUNT, blog.getPosts().getTotalItems().intValue());
                    //TODO other fields

                    getContentResolver().insert(BlogsEntry.CONTENT_URI, value);
                }

            }
            else
            {
                Log.i(TAG, "Failed to find blog for url: " + url);
            }
        }
        else if (intent.hasExtra(EXTRA_BLOGGER_BLOG_UPDATE))
        {
            BlogItem blogItem = intent.getParcelableExtra(EXTRA_BLOGGER_BLOG_UPDATE);

            if (blogItem != null) {
                if (blogItem.last_update_time != blogItem.prev_last_update_time ||
                        blogItem.next_page_token != null) {
                    PostList posts = BloggerApiUtil.fetchPosts(blogItem.service_blog_id, /*TODO*/ 12L, blogItem.next_page_token);

                    // Set the prev_last_update_time so we don't try to requery when going back to blog activity
                    if (blogItem.last_update_time != blogItem.prev_last_update_time) {
                        ContentValues value = new ContentValues();
                        value.put(BlogsEntry.COLUMN_PREV_LAST_UPDATED, blogItem.last_update_time);
                        //value.put(BlogsEntry.COLUMN_PREV_LAST_UPDATED, blogItem.prev_last_update_time);

                        // TODO replace this with _ID specific URI
                        getContentResolver().update(BlogsEntry.CONTENT_URI,
                                value,
                                BlogsEntry._ID + " = ?",
                                new String[]{Integer.toString(blogItem._id)});
                    }

                    List<Post> items = posts.getItems();
                    if (items != null && !items.isEmpty()) {

                        ContentValues[] values = new ContentValues[items.size()];
                        for (int i = 0; i < items.size(); i++) {
                            Post post = items.get(i);

                            ContentValues value = values[i] = new ContentValues();
                            value.put(PostsEntry.COLUMN_PUBLISHED, post.getPublished().getValue());
                            value.put(PostsEntry.COLUMN_IMAGE_URI,
                                    post.getImages() != null && post.getImages().size() > 0 ?
                                            post.getImages().get(0).getUrl() : null);
                            value.put(PostsEntry.COLUMN_TITLE, post.getTitle());
                            value.put(PostsEntry.COLUMN_SERVICE_POST_ID, post.getId());
                            value.put(PostsEntry.COLUMN_BLOG_KEY, blogItem._id);
                            value.put(PostsEntry.COLUMN_PLACE_KEY, "TODO"); //TODO
                            value.put(PostsEntry.COLUMN_URL, post.getUrl());
                        }
                        getContentResolver().bulkInsert(PostsEntry.CONTENT_URI, values);
                    }

                    // Update the next page token
                    ContentValues value = new ContentValues();
                    value.put(BlogsEntry.COLUMN_NEXT_PAGE_TOKEN, posts.getNextPageToken());
                    // TODO replace this with _ID specific URI
                    getContentResolver().update(BlogsEntry.CONTENT_URI,
                            value,
                            BlogsEntry._ID + " = ?",
                            new String[]{Integer.toString(blogItem._id)});
                }
            }
        }
        else if (intent.hasExtra(EXTRA_BLOGGER_POST_UPDATE))
        {
            PostItem postItem = intent.getParcelableExtra(EXTRA_BLOGGER_POST_UPDATE);

            if (postItem != null) {
                // See if we already have content for this _id;
                Cursor postCursor = getContentResolver().query(
                        MarkerContract.PostsEntry.CONTENT_URI,
                        new String[] { PostsEntry.COLUMN_CONTENT },
                        MarkerContract.BlogsEntry._ID + " = ?",
                        new String[]{Integer.toString(postItem._id)},
                        null);

                if (postCursor.getCount() == 1)
                {
                    postCursor.moveToNext();
                    if (postCursor.getString(0) == null) {
                        //Download and Update
                        Post post = BloggerApiUtil.fetchPost(postItem.blogItem.service_blog_id, postItem.service_post_id);

                        if (post != null) {
                            ContentValues value = new ContentValues();
                            value.put(PostsEntry.COLUMN_CONTENT, post.getContent());

                            // TODO replace this with _ID specific URI
                            getContentResolver().update(PostsEntry.CONTENT_URI,
                                    value,
                                    PostsEntry._ID + " = ?",
                                    new String[]{Integer.toString(postItem._id)});
                        }
                    }
                }
            }
        }
    }

}
