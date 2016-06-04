package com.xanadu.marker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import com.xanadu.marker.data.BlogItem;
import com.xanadu.marker.data.PostItem;

public class BlogActivity extends AppCompatActivity implements BlogFragment.OnListFragmentInteractionListener {

    private static String BLOG_BUNDLE_BLOG_ITEM = "BLOG_BUNDLE_BLOG_ITEM";
    private boolean mTwoPane = false;
    private BlogItem mBlogItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        if (savedInstanceState == null) {
            // Create the blog fragment and add it to the activity
            // using a fragment transaction.
            mBlogItem = getIntent().getParcelableExtra(BlogItem.EXTRA_BLOG_ITEM);
        }
        else
        {
            mBlogItem = savedInstanceState.getParcelable(BLOG_BUNDLE_BLOG_ITEM);
        }

        BlogFragment fragment = BlogFragment.newInstance(mBlogItem);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.blog_container, fragment)
                .commit();

        // Being here means we are in animation mode
        // TODO supportPostponeEnterTransition();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BLOG_BUNDLE_BLOG_ITEM, mBlogItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListFragmentInteraction(PostItem item) {
        if (!mTwoPane)
        {
            // Fire up the new activity
            Intent startIntent = new Intent(this, PostActivity.class);
            startIntent.putExtra(PostItem.EXTRA_POSTS_ITEM, item);
            startActivity(startIntent);
        }
    }
}
