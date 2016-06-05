package com.xanadu.marker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.paginate.Paginate;
import com.xanadu.marker.data.BlogItem;
import com.xanadu.marker.data.MarkerContract;
import com.xanadu.marker.data.PostItem;
import com.xanadu.marker.data.PostLoader;
import com.xanadu.marker.data.UpdaterService;
import com.xanadu.marker.ui.DividerItemDecoration;

import java.lang.reflect.Field;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlogFragment
        extends Fragment
        implements Paginate.Callbacks, LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG = "BlogFragment";

    private static final String ARG_BLOG_ITEM = "blog_item";
    private static final int POST_LOADER = 3;

    private BlogItem mBlogItem;

    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private PostItemRecyclerViewAdapter mPostItemAdapter;

    public BlogFragment() {
        // Required empty public constructor
    }

    public static BlogFragment newInstance(BlogItem blogItem) {
        BlogFragment fragment = new BlogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BLOG_ITEM, blogItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBlogItem = getArguments().getParcelable(ARG_BLOG_ITEM);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blog, container, false);

        final Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.blog_toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.blog_collapsing_toolbar);
        collapsingToolbar.setTitle(rootView.getResources().getString(R.string.title_fragment_blog));
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
//        ImageView imageView = (ImageView) rootView.findViewById(R.id.blog_photo);
//
//        Glide.with(getContext())
//                .load(mBlogItem.image_uri)
//                .error(R.drawable.photo_background_protection)
//                .crossFade()
//                .into(imageView);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_blog);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        // Set the mPostItemAdapter
        Context context = rootView.getContext();
        //if (mColumnCount <= 1) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        //} else {
        //TODO
        //mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        //}
        mPostItemAdapter = new PostItemRecyclerViewAdapter(mListener,
                rootView.findViewById(R.id.recyclerview_blog_empty), mBlogItem);
        mRecyclerView.setAdapter(mPostItemAdapter);

        setupPagination();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(PostItem item) {
        if (mListener != null) {
            mListener.onListFragmentInteraction(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean loading = false;
    private Paginate paginate;
    protected int threshold = 4;
    //private static final int GRID_SPAN = 3;

    protected void setupPagination( ) {
        // If RecyclerView was recently bound, unbind
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;

        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(threshold)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(null)
                .build();
    }

    @Override
    public void onLoadMore() {
        Log.d("Paginate", "onLoadMore");

        // We need the latest next_page_token
        // First, check if the this id exists, and get the last updated time,
        // and see if it's different than this time
        Cursor blogCursor = getContext().getContentResolver().query(
                MarkerContract.BlogsEntry.CONTENT_URI,
                new String[] { MarkerContract.BlogsEntry.COLUMN_NEXT_PAGE_TOKEN },
                MarkerContract.BlogsEntry._ID + " = ?",
                new String[]{Integer.toString(mBlogItem._id)},
                null);

        // We should only have one
        if (blogCursor.getCount() == 1) {
            // Set the pagination token for the blogger client api
            blogCursor.moveToNext();
            mBlogItem.next_page_token = blogCursor.getString(0);

            Intent requestIntent = new Intent(getActivity(), UpdaterService.class);
            requestIntent.putExtra(UpdaterService.EXTRA_BLOGGER_BLOG_UPDATE, mBlogItem);
            getActivity().startService(requestIntent);

            loading = true;
        }
        else {
            loading = false;
            Log.e(TAG, "Error querying blogs, did not find id");
        }
    }

    @Override
    public boolean isLoading() {
        return loading; // Return boolean whether data is already loading or not
    }

    @Override
    public boolean hasLoadedAllItems() {
        if (paginate != null) {
            paginate.unbind();
            paginate = null;
        }

        // If all posts are loaded return true
        if (mBlogItem.post_count != mPostItemAdapter.getItemCount()) return false;
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return PostLoader.newBlogPostsInstance(getContext(), mBlogItem._id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPostItemAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPostItemAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(PostItem item);
    }

}
