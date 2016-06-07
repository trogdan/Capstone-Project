package com.xanadu.marker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;

import com.xanadu.marker.data.BlogItem;
import com.xanadu.marker.data.BlogLoader;
import com.xanadu.marker.data.UpdaterService;
import com.xanadu.marker.ui.BackEditText;
import com.xanadu.marker.ui.DividerItemDecoration;

/**
 * A fragment representing a list of blogs.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BlogsFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "BlogsFragment";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int BLOG_LOADER = 2;

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private BlogItemRecyclerViewAdapter mBlogItemAdapter;
    private BackEditText mUrlEdit;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BlogsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BlogsFragment newInstance(int columnCount) {
        BlogsFragment fragment = new BlogsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_blogs, container, false);
        final Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.blogs_toolbar);
        toolbar.setTitleTextAppearance(getContext(), R.style.ExpandedAppBar);

        rootView.findViewById(R.id.add_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUrlEdit.setVisibility(View.VISIBLE);
                mUrlEdit.setFocusableInTouchMode(true);
                mUrlEdit.requestFocus();
                //startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                //        .setType("text/plain")
                //        .setText("Some sample text")
                //        .getIntent(), getString(R.string.action_blog_add)));
            }
        });

        mUrlEdit = (BackEditText)rootView.findViewById(R.id.add_url_edit);
        mUrlEdit.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                // first check for valid URI
                                String uri = v.getText().toString();
                                if(URLUtil.isValidUrl(uri))
                                {
                                    //Send intent to updaterService
                                    Intent searchIntent = new Intent(getActivity(), UpdaterService.class);
                                    searchIntent.putExtra(UpdaterService.EXTRA_BLOGGER_BLOGS_UPDATE, uri);
                                    getActivity().startService(searchIntent);
                                }
                                else
                                {
                                    //Snackbar to tell user to please, try again
                                }
                                mUrlEdit.setVisibility(View.INVISIBLE);
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_blogs);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        // Set the mBlogItemAdapter
        Context context = rootView.getContext();
        //if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        //} else {
            //TODO
            //mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        //}
        mBlogItemAdapter = new BlogItemRecyclerViewAdapter(mListener,
                rootView.findViewById(R.id.recyclerview_blogs_empty));
        mRecyclerView.setAdapter(mBlogItemAdapter);

        return rootView;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BLOG_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return BlogLoader.newAllBlogsInstance(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBlogItemAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBlogItemAdapter.swapCursor(null);
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
        void onListFragmentInteraction(BlogItem item);
    }

}
