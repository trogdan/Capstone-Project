package com.xanadu.marker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paginate.Paginate;
import com.xanadu.marker.data.BlogItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlogFragment
        extends Fragment
        implements Paginate.Callbacks
{
    private static final String TAG = "BlogFragment";

    private static final String ARG_BLOG_ITEM = "blog_item";

    private BlogItem mBlogItem;

    private OnFragmentInteractionListener mListener;

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
        setupPagination();

        return inflater.inflate(R.layout.fragment_blog, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean loading = false;
    private int page = 0;
    private Paginate paginate;
    protected int threshold = 4;
    //protected int totalPages = 3;
    //protected int itemsPerPage = 10;
    //private static final int GRID_SPAN = 3;

    protected void setupPagination( ) {
        // If RecyclerView was recently bound, unbind
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;

//        paginate = Paginate.with(mRecyclerView, this)
//                .setLoadingTriggerThreshold(threshold)
//                .addLoadingListItem(true)
//                .setLoadingListItemCreator(null)
//                .build();
    }

    @Override
    public void onLoadMore() {
        Log.d("Paginate", "onLoadMore");
        loading = true;
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
        return true;
        //return page == totalPages; // If all pages are loaded return true
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
