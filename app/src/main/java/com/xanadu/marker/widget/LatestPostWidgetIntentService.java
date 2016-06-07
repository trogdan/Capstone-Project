/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xanadu.marker.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.google.api.services.blogger.Blogger;
import com.xanadu.marker.PostActivity;
import com.xanadu.marker.R;
import com.xanadu.marker.data.MarkerContract;
import com.xanadu.marker.data.MarkerContract.PostsEntry;
import com.xanadu.marker.data.MarkerContract.BlogsEntry;
import com.xanadu.marker.data.PostLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * IntentService which handles updating all Today widgets with the latest data
 */
public class LatestPostWidgetIntentService extends IntentService {
    private static final String TAG = "MarkerWidgetService";

    private static final String[] LAST_COLUMNS = {
            PostsEntry.TABLE_NAME + "." + PostsEntry._ID,
            PostsEntry.COLUMN_PUBLISHED,
            PostsEntry.COLUMN_TITLE,
            PostsEntry.TABLE_NAME + "." + PostsEntry.COLUMN_IMAGE_URI,
            BlogsEntry.COLUMN_NAME
    };

    private static final int INDEX_POST_ID = 0;
    private static final int INDEX_POST_PUBLISHED = 1;
    private static final int INDEX_POST_TITLE = 2;
    private static final int INDEX_POST_IMAGE_URI = 3;
    private static final int INDEX_BLOG_NAME = 4;

    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy",
            java.util.Locale.getDefault());

    public LatestPostWidgetIntentService() {
        super("LatestPostWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                LatestPostWidgetProvider.class));

        // Get today's data from the ContentProvider
        Uri latestPostUri = MarkerContract.PostsEntry.buildPostBlogLast();
        Cursor data = getContentResolver().query(latestPostUri, LAST_COLUMNS, null,
                null, null);
        if (data == null) {
            Log.d(TAG, "No cursor returned from provider");
            return;
        }
        if (!data.moveToFirst()) {
            Log.d(TAG, "Cursor is empty");
            data.close();
            return;
        }

        // Extract the weather data from the Cursor
        int post_id = data.getInt(INDEX_POST_ID);
        long timestamp = data.getLong(INDEX_POST_PUBLISHED);
        String title = data.getString(INDEX_POST_TITLE);
        String blogName = data.getString(INDEX_BLOG_NAME);
        String imageUri = data.getString(INDEX_POST_IMAGE_URI);

        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);

//            // Add the data to the RemoteViews
//            views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
//            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, title);
            }
            views.setTextViewText(R.id.widget_last_name, title);
//            views.setTextViewText(R.id.blog_list_item_desc, blogName);
            views.setTextViewText(R.id.widget_last_date,
                    mSimpleDateFormat.format(new Date(timestamp)));
//
//            // Create an Intent to launch PostActivity
            Intent launchIntent = new Intent(this, PostActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_last, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_last_thumbnail, description);
    }
}
