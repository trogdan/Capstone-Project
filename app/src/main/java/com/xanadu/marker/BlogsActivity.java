package com.xanadu.marker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.xanadu.marker.data.BlogItem;

public class BlogsActivity extends AppCompatActivity implements BlogsFragment.OnListFragmentInteractionListener{

    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
        switch(item.getItemId()) {
            //noinspection SimplifiableIfStatement
//            case R.id.action_maps:
//                startActivity(new Intent(this, MapsActivity.class));
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListFragmentInteraction(BlogItem item) {
        if (!mTwoPane)
        {
            // Fire up the new activity
            Intent startIntent = new Intent(this, BlogActivity.class);
            startIntent.putExtra(BlogActivity.EXTRA_BLOG_ITEM, item);
            startActivity(startIntent);
        }
    }
}
