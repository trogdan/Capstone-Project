package com.xanadu.marker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xanadu.marker.data.BlogPostLoader;
import com.xanadu.marker.data.BlogPostQuery;
import com.xanadu.marker.data.MarkerContract;
import com.xanadu.marker.data.PlaceLoader;
import com.xanadu.marker.data.UpdaterService;
import com.xanadu.marker.ui.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapsActivity
        extends AppCompatActivity
        implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener
{

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id)
        {
            case PLACE_LOADER_ID:
                return PlaceLoader.newAllPlacesInstance(this);
                //return PlaceLoader.newAllPlacesBlogPostInstance(this);
            case BLOG_POST_LOADER_ID:
                int placeId = args.getInt(BlogPostLoader.LOADER_ARG_PLACE_ID);
                return BlogPostLoader.newBlogPostsByPlace(this, placeId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case PLACE_LOADER_ID:
                loadPlaces(cursor);
                break;
            case BLOG_POST_LOADER_ID:
                mRecyclerView.performClick();
                loadBlogPosts(cursor);
                break;
            default:
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private ArrayList<BlogPostQuery> getQueryFromMap(String placeId)
    {
        Iterator it = mMarkerMap.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<Marker, ArrayList<BlogPostQuery>> pair = (Map.Entry)it.next();
            Marker marker = pair.getKey();
            if(marker.getSnippet().equals(placeId))
            {
                return pair.getValue();
            }
        }
        return null;
    }
    private void loadPlaces(Cursor cursor)
    {
        while(cursor != null && cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(MarkerContract.MARKER_DB_PLACE_NAME));

            LatLng position = new LatLng(
                    cursor.getDouble(PlaceLoader.Query.COLUMN_COORD_LAT),
                    cursor.getDouble(PlaceLoader.Query.COLUMN_COORD_LONG));

            mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(name));
            // TODO, for map popup
//            String placeId = Integer.toString(PlaceLoader.Query._ID);
//            ArrayList<BlogPostQuery> list = getQueryFromMap(placeId);
//            if (list == null) {
//                Marker marker = mMap.addMarker(new MarkerOptions()
//                        .position(position)
//                        .title(name)
//                        .snippet(Integer.toString(PlaceLoader.Query._ID))); // hack, store place_id with marker
//                list = new ArrayList<>();
//                list.add(new BlogPostQuery(
//                        cursor.getString(PlaceLoader.Query.COLUMN_BLOG_ID),
//                        cursor.getString(PlaceLoader.Query.COLUMN_POST_ID),
//                        placeId));
//                mMarkerMap.put(marker, list);
//            }
//            else {
//                list.add(new BlogPostQuery(
//                        cursor.getString(PlaceLoader.Query.COLUMN_BLOG_ID),
//                        cursor.getString(PlaceLoader.Query.COLUMN_POST_ID),
//                        placeId));
//            }
        }
    }

    private void loadBlogPosts(Cursor cursor)
    {
        while(cursor != null && cursor.moveToNext()) {

        }
    }
    private static final String TAG = "MapsActivity";
    private static final int PLACE_LOADER_ID = 1;
    private static final int BLOG_POST_LOADER_ID = 4;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private LatLngBounds mLatestBounds;
    private Location mLatestLocation;
    private BlogPostItemRecyclerViewAdapter mBlogPostAdapter;
    private RecyclerView mRecyclerView;
    private ImageView mLogoView;

    // Create the hash map on the beginning
    private HashMap<Marker, ArrayList<BlogPostQuery>> mMarkerMap = new HashMap<>();

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mLogoView = (ImageView)findViewById(R.id.logo_imageview);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_maps);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBlogPostAdapter = new BlogPostItemRecyclerViewAdapter(null,
                findViewById(R.id.recyclerview_maps));
        mRecyclerView.setAdapter(mBlogPostAdapter);

        Toolbar toolbar = (Toolbar)findViewById(R.id.blogs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportLoaderManager().initLoader(PLACE_LOADER_ID, null, this);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Transition reenterTrans = new Slide();
            getWindow().setReenterTransition(reenterTrans);
            getWindow().setExitTransition(reenterTrans);
            getWindow().setBackgroundDrawable(new ColorDrawable(0xFFFFFF)); // Clear the splash screen
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    @TargetApi(21)
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
        switch(item.getItemId()) {
            //noinspection SimplifiableIfStatement
            case R.id.action_search:
                mMap.clear();
                mMarkerMap.clear();

                Intent searchIntent = new Intent(this, UpdaterService.class);
                searchIntent.putExtra(UpdaterService.EXTRA_WFS_QUERY_BOX, mLatestBounds);
                startService(searchIntent);
                return true;
            case R.id.action_blogs:
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
//                    ActivityOptions options = ActivityOptions
//                            .makeSceneTransitionAnimation(this, mLogoView, "reveal");
//                    startActivity(new Intent(this, BlogsActivity.class), options.toBundle());
//                } else{
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this);
                Intent intent = new Intent(MapsActivity.this, BlogsActivity.class);
                startActivity(intent, options.toBundle());
//                    startActivity(new Intent(this, BlogsActivity.class));
//                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                mLatestBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //TODO popup
//        int placeId = Integer.parseInt(marker.getSnippet());
//
//        ArrayList<BlogPostQuery> query = mMarkerMap.get(marker);
//        getSupportLoaderManager().destroyLoader(BLOG_POST_LOADER_ID);
//
//        Bundle args = new Bundle();
//        args.putInt(BlogPostLoader.LOADER_ARG_PLACE_ID, placeId);
//
//        getSupportLoaderManager().initLoader(BLOG_POST_LOADER_ID, args, this);
//
//        Intent searchIntent = new Intent(this, UpdaterService.class);
//        searchIntent.putParcelableArrayListExtra(UpdaterService.EXTRA_BLOGGER_BLOG_POST_UPDATE, query);
//        startService(searchIntent);

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if ( ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLatestLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLatestLocation.getLatitude(), mLatestLocation.getLongitude()), 10));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
