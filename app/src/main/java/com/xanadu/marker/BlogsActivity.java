package com.xanadu.marker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;

import com.xanadu.marker.data.BlogItem;

public class BlogsActivity extends AppCompatActivity implements BlogsFragment.OnListFragmentInteractionListener{

    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogs);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Transition reenterTrans = new Slide();
            getWindow().setReenterTransition(reenterTrans);
            getWindow().setExitTransition(reenterTrans);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.blogs_toolbar);
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
            startIntent.putExtra(BlogItem.EXTRA_BLOG_ITEM, item);
            startActivity(startIntent);
        }
    }
}
