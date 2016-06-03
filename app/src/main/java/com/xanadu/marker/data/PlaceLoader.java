package com.xanadu.marker.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.xanadu.marker.data.MarkerContract.PlacesEntry;

/**
 * Helper for loading a list of places or a single place.
 */
public class PlaceLoader extends CursorLoader {
    public static PlaceLoader newAllPlacesInstance(Context context) {
        return new PlaceLoader(context, PlacesEntry.CONTENT_URI);
    }

    public static PlaceLoader newInstanceForItemId(Context context, long itemId) {
        return new PlaceLoader(context, PlacesEntry.buildPlacesUri(itemId));
    }

    private PlaceLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public interface Query {
        String[] PROJECTION = {
                PlacesEntry._ID,
                PlacesEntry.COLUMN_PLACE_ID,
                PlacesEntry.COLUMN_NAME,
                PlacesEntry.COLUMN_ABOUT,
                PlacesEntry.COLUMN_COORD_LAT,
                PlacesEntry.COLUMN_COORD_LONG,
                PlacesEntry.COLUMN_GOO_ID
        };

        int _ID = 0;
        int COLUMN_PLACE_ID = 1;
        int COLUMN_PLACE_NAME = 2;
        int COLUMN_PLACE_ABOUT = 3;
        int COLUMN_COORD_LAT = 4;
        int COLUMN_COORD_LONG = 5;
        int COLUMN_GOO_ID = 6;
    }


}
