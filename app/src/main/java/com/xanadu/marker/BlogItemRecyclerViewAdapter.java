package com.xanadu.marker;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xanadu.marker.BlogsFragment.OnListFragmentInteractionListener;
import com.xanadu.marker.data.BlogItem;
import com.xanadu.marker.data.BlogLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * {@link RecyclerView.Adapter} that can display a BlogItem and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class BlogItemRecyclerViewAdapter extends RecyclerView.Adapter<BlogItemRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "BlogItemRecyViewAdapter";

    private final OnListFragmentInteractionListener mListener;
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy",
            java.util.Locale.getDefault());

    private String mUpdatedLabel;
    private Cursor mCursor;
    private View mEmptyView;

    public BlogItemRecyclerViewAdapter(OnListFragmentInteractionListener listener, View emptyView) {
        mListener = listener;
        mEmptyView = emptyView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_blog, parent, false);
        mUpdatedLabel = parent.getResources().getString(R.string.blogs_list_item_date_prefix);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mCursor == null ) return;
        mCursor.moveToPosition(position);

        holder.mNameView.setText(mCursor.getString(BlogLoader.COLUMN_NAME));
        holder.mDescView.setText(mCursor.getString(BlogLoader.COLUMN_DESC)); // TODO it's possible to have HTML here
        //Log.d(TAG, "Latest Updated: " + mCursor.getLong(BlogLoader.COLUMN_LAST_UPDATED));
        holder.mDateView.setText(mUpdatedLabel + "  " +
                mSimpleDateFormat.format(new Date(mCursor.getLong(BlogLoader.COLUMN_LAST_UPDATED))));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener && mCursor != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mCursor.moveToPosition(holder.getAdapterPosition());
                    mListener.onListFragmentInteraction(new BlogItem(mCursor));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mDescView;
        public final TextView mDateView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.blog_list_item_name);
            mDescView = (TextView) view.findViewById(R.id.blog_list_item_desc);
            mDateView = (TextView) view.findViewById(R.id.blog_list_item_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDateView.getText() + "'";
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
