package com.xanadu.marker.remote;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MarkerWfsUtil {
    private static final String TAG = "MarkerWfsUtil";
    private static final String MARKER_WFS_BASE_URI = "http://marker.cloudapp.net:8080";
    private static OkHttpClient mHttpClient = new OkHttpClient();
    private static Request.Builder mBuilder = new Request.Builder();

    private MarkerWfsUtil() {}

    @Nullable
    public static JSONObject fetchMarkerPlacesInBox(LatLngBounds box)
    {
        JSONObject jsonObject;

        String queryUrl = buildWfsBoundingBoxQuery(box);
        if (queryUrl == null)
            return null;

        try {
            jsonObject = new JSONObject(fetchPlainText(queryUrl));
            return jsonObject;
        } catch (IOException e) {
            Log.e(TAG, "Error fetching items JSON", e);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        return null;
    }

    @NonNull
    static String fetchPlainText(String url) throws IOException
    {
        Request request = mBuilder.url(url).build();

        Response response = mHttpClient.newCall(request).execute();

        return response.body().string();
    }

    @Nullable
    static String buildWfsBoundingBoxQuery(LatLngBounds box)
    {
        try {
            Uri.Builder builder = Uri.parse(MARKER_WFS_BASE_URI).buildUpon();

            ///geoserver/wfs?srsname=EPSG:4326&typename=go:place_post_blog&version=1.0.0&request=GetFeature&service=WFS&outputFormat=json
            builder.path("/geoserver/wfs");
            builder.appendQueryParameter("srsname", "EPSG:4326");
            builder.appendQueryParameter("typename", "go:place_post_blog");
            builder.appendQueryParameter("version", "1.0.0");
            builder.appendQueryParameter("request", "GetFeature");
            builder.appendQueryParameter("service", "WFS");
            builder.appendQueryParameter("outputFormat", "json");
            builder.appendQueryParameter("BBOX",
                    Double.toString(box.southwest.longitude) + "," + Double.toString(box.southwest.latitude) + "," +
                    Double.toString(box.northeast.longitude) + "," + Double.toString(box.northeast.latitude));

            String result = builder.build().toString();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error converting coordinates to WFS QUERY URL", e);
        }

        return null;
    }
}
