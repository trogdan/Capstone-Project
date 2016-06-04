package com.xanadu.marker;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xanadu.marker.BlogsFragment.OnListFragmentInteractionListener;
import com.xanadu.marker.data.BlogItem;
import com.xanadu.marker.data.BlogLoader;

/**
 * {@link RecyclerView.Adapter} that can display a BlogItem and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class BlogItemRecyclerViewAdapter extends RecyclerView.Adapter<BlogItemRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mCursor == null ) return;
        mCursor.moveToPosition(position);

        holder.mIdView.setText(mCursor.getString(BlogLoader.COLUMN_NAME));
        holder.mContentView.setText(mCursor.getString(BlogLoader.COLUMN_URL));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener && mCursor != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
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
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.blog_list_item_id);
            mContentView = (TextView) view.findViewById(R.id.blog_list_item_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
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
