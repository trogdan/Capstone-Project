package com.xanadu.marker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xanadu.marker.data.PostItem;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if (savedInstanceState == null) {
            // Create the blog fragment and add it to the activity
            // using a fragment transaction.

            PostFragment fragment = PostFragment.newInstance((PostItem)getIntent().getParcelableExtra(PostItem.EXTRA_POSTS_ITEM));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.post_container, fragment)
                    .commit();

            // Being here means we are in animation mode
            // TODO supportPostponeEnterTransition();
        }
    }

}
