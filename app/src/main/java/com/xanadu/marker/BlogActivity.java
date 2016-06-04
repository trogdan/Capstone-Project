package com.xanadu.marker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.xanadu.marker.data.BlogItem;

public class BlogActivity extends AppCompatActivity implements BlogFragment.OnFragmentInteractionListener{

    public static final String EXTRA_BLOG_ITEM = "extra_blog_item";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        if (savedInstanceState == null) {
            // Create the blog fragment and add it to the activity
            // using a fragment transaction.

            BlogFragment fragment = BlogFragment.newInstance((BlogItem)getIntent().getParcelableExtra(EXTRA_BLOG_ITEM));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.blog_container, fragment)
                    .commit();

            // Being here means we are in animation mode
            // TODO supportPostponeEnterTransition();
        }

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
