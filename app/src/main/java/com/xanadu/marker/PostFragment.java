package com.xanadu.marker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xanadu.marker.data.BlogItem;
import com.xanadu.marker.data.PostItem;
import com.xanadu.marker.data.PostLoader;
import com.xanadu.marker.data.UpdaterService;

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
        final AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.post_collapsing_toolbar);
        collapsingToolbar.setTitle("");
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

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
        //user to switch to a different blog entry, thus providing inconsistencies
//        Intent requestIntent = new Intent(getActivity(), UpdaterService.class);
//        requestIntent.putExtra(UpdaterService.EXTRA_BLOGGER_POST_UPDATE, mPostItem);
//        getActivity().startService(requestIntent);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(POST_VIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
        //return PostLoader.newInstanceForItemId(getContext(), mPostItem._id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst())
        {
            String content = data.getString(PostLoader.COLUMN_CONTENT);
            if(content != null)
            {
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
