package com.xanadu.marker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.location.places.Place;
import com.xanadu.marker.data.BlogItem;
import com.xanadu.marker.data.PlaceLoader;
import com.xanadu.marker.data.PostItem;
import com.xanadu.marker.data.PostLoader;
import com.xanadu.marker.data.UpdaterService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class PostFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG = "PostFragment";
    private static final String ARG_POST_ITEM = "post_item";
    private static final int POST_VIEW_LOADER = 4;
    private static final int PLACE_VIEW_LOADER = 5;
//
//    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy",
//            java.util.Locale.getDefault());
    private ShareActionProvider mShareActionProvider;
    private PostItem mPostItem;
    private WebView mWebView;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(PostItem postItem) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POST_ITEM, postItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPostItem = getArguments().getParcelable(ARG_POST_ITEM);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_post, container, false);


        final Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.post_toolbar);
        toolbar.setTitle("");

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.post_collapsing_toolbar);
        collapsingToolbar.setTitle("");
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
//        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        final AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO, eventually, displaying map views as an option in the collapsing toolbar
//        final AppBarLayout appBarLayout =
//                (AppBarLayout) rootView.findViewById(R.id.post_appbar);
//        //appBarLayout.setExpanded(false);
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle("");
//                    isShow = true;
//                } else if(isShow) {
//                    collapsingToolbar.setTitle(mSimpleDateFormat.format(new Date(mPostItem.published)));
//                    isShow = false;
//                }
//            }
//        });

        mWebView = (WebView) rootView.findViewById(R.id.post_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl(mPostItem.uri);

        // TODO webview is a pain! downside of the above is that navigation of a site like Blogger allows the
        // user to switch to a different blog entry, thus providing inconsistencies
//        Intent requestIntent = new Intent(getActivity(), UpdaterService.class);
//        requestIntent.putExtra(UpdaterService.EXTRA_BLOGGER_POST_UPDATE, mPostItem);
//        getActivity().startService(requestIntent);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_post_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createSharePostUriIntent());
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
        switch(item.getItemId()) {
            //noinspection SimplifiableIfStatement
//            case R.id.action_post_map:
//                Log.d(TAG, "Received map menu action");
//                getLoaderManager().initLoader(PLACE_VIEW_LOADER, null, this);
//
//
//                return true;
            case R.id.action_post_share:
                Log.d(TAG, "Received share menu action");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(POST_VIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return PlaceLoader.newInstanceForItemId(getContext(), mPostItem.place_key);
        //return PostLoader.newInstanceForItemId(getContext(), mPostItem._id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case POST_VIEW_LOADER:
                if (data != null && data.moveToFirst()) {
                    String content = data.getString(PostLoader.COLUMN_CONTENT);
                    if (content != null) {
                        if (Build.VERSION.SDK_INT < 18) {
                            mWebView.clearView();
                        } else {
                            mWebView.loadUrl("about:blank");
                        }

                        mWebView.loadData(content, "text/html", "utf-8");
                        mWebView.getSettings().setLoadWithOverviewMode(true);
                        mWebView.getSettings().setUseWideViewPort(true);
                    }
                }
                break;
            case PLACE_VIEW_LOADER:
                if(data == null || !data.moveToFirst())
                    break;

                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:" +
                        data.getDouble(PlaceLoader.Query.COLUMN_COORD_LAT) + "," +
                        data.getDouble(PlaceLoader.Query.COLUMN_COORD_LONG) + ",?q=" +
                        Uri.encode(data.getString(PlaceLoader.Query.COLUMN_PLACE_NAME)));

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
                break;
            default:
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private static final String MARKER_SHARE_HASHTAG = " #Marker";
    private Intent createSharePostUriIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mPostItem.uri + " " + MARKER_SHARE_HASHTAG);
        return shareIntent;
    }
}
