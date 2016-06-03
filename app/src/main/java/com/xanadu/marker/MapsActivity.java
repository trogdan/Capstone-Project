package com.xanadu.marker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xanadu.marker.data.MarkerContract;
import com.xanadu.marker.data.PlaceLoader;
import com.xanadu.marker.data.UpdaterService;

public class MapsActivity
        extends AppCompatActivity
        implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor>
{

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return PlaceLoader.newAllPlacesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                loadPlaces(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
                    //.snippet("Population: 4,137,400")));
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)));
        }
    }

    private static final String TAG = "MapsActivity";
    private static final int LOADER_ID = 1;

    private GoogleMap mMap;
    private LatLngBounds mLatestBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
        switch(item.getItemId()) {
//            case R.id.action_websearch:
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }
//                return true;
            //noinspection SimplifiableIfStatement
            case R.id.action_search:
                mMap.clear();

                Intent searchIntent = new Intent(this, UpdaterService.class);
                searchIntent.putExtra(UpdaterService.EXTRA_WFS_QUERY_BOX, mLatestBounds);
                startService(searchIntent);
                return true;
            case R.id.action_blogs:
                startActivity(new Intent(this, BlogsActivity.class));
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

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                mLatestBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            }
        });

    }

}
