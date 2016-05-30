package com.xanadu.marker.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.RemoteException;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Geometry;
import com.cocoahero.android.geojson.MultiPoint;
import com.cocoahero.android.geojson.Position;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.xanadu.marker.remote.MarkerWfsUtil;
import com.xanadu.marker.data.MarkerContract.PlacesEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.xanadu.marker.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.xanadu.marker.intent.extra.REFRESHING";

    // RectF of coordinates
    public static final String EXTRA_WFS_QUERY_BOX
            = "com.xanadu.marker.intent.extra.WFS_QUERY_BOX";

    public UpdaterService() {
        super(TAG);
    }



    @Override
    protected void onHandleIntent(Intent intent)
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        LatLngBounds box = intent.getParcelableExtra(EXTRA_WFS_QUERY_BOX);

        try {
            JSONObject jsonObj = MarkerWfsUtil.fetchMarkerPlacesInBox(box);
            if (jsonObj == null) {
                throw new JSONException("Invalid parsed json" );
            }

            GeoJSONObject geoJSON = GeoJSON.parse(jsonObj);

            if (geoJSON.getType() != GeoJSON.TYPE_FEATURE_COLLECTION) {
                throw new JSONException("Unexpected geojson content");
            }

            List<Feature> features = ((FeatureCollection) geoJSON).getFeatures();

            //Parse and Pass the results to the content provider
            for (Feature feature : features) {
                Geometry geom = feature.getGeometry();

                // Only handling multipoints, eventually maybe switch to points
                if (geom.getType() != GeoJSON.TYPE_MULTI_POINT) {
                    continue;
                }

                MultiPoint point = (MultiPoint)geom;
                List<Position> positions = point.getPositions();

                JSONObject properties = feature.getProperties();
                ContentValues values = new ContentValues();
                values.put(PlacesEntry.COLUMN_PLACE_ID, properties.getString(MarkerContract.MARKER_DB_PLACE_PLACE_ID));
                values.put(PlacesEntry.COLUMN_GOO_ID, properties.getString(MarkerContract.MARKER_DB_PLACE_GOO_ID));
                values.put(PlacesEntry.COLUMN_COORD_LAT, positions.get(0).getLatitude());
                values.put(PlacesEntry.COLUMN_COORD_LONG, positions.get(0).getLongitude());
                values.put(PlacesEntry.COLUMN_PLACE_NAME, properties.getString(MarkerContract.MARKER_DB_PLACE_NAME));
                values.put(PlacesEntry.COLUMN_PLACE_ABOUT, properties.getString(MarkerContract.MARKER_DB_PLACE_ABOUT));

                cpo.add(ContentProviderOperation.newInsert(PlacesEntry.CONTENT_URI).withValues(values).build());
            }

            getContentResolver().applyBatch(MarkerContract.CONTENT_AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
        }

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }

}
