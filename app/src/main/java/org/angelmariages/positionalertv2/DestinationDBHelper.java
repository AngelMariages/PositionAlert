package org.angelmariages.positionalertv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class DestinationDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyDestinations.db";
    private static final String DESTINATIONS_TABLE = "destinations";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LAT = "latitude";
    private static final String COLUMN_LONG = "longitude";
    private static final String COLUMN_RADIUS = "radius";
    private static final String COLUMN_ACTIVE = "active";
    private static final String COLUMN_ID = BaseColumns._ID;

    public DestinationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO: replace with variable names
        db.execSQL("CREATE TABLE " + DESTINATIONS_TABLE +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY, name TEXT UNIQUE, latitude DOUBLE, longitude DOUBLE, radius INTEGER, active BOOLEAN)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DESTINATIONS_TABLE);
        onCreate(db);
    }

    public boolean insertDestination(Destination destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(COLUMN_NAME, destination.getRequestId());
        content.put(COLUMN_LAT, destination.getLatitude());
        content.put(COLUMN_LONG, destination.getLongitude());
        content.put(COLUMN_RADIUS, destination.getRadius());
        content.put(COLUMN_ACTIVE, true);

        db.insert(DESTINATIONS_TABLE, null, content);
        return true;
    }

    public boolean updateDestination(String name, Destination destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(COLUMN_NAME, destination.getRequestId());
        content.put(COLUMN_LAT, destination.getLatitude());
        content.put(COLUMN_LONG, destination.getLongitude());
        content.put(COLUMN_RADIUS, destination.getRadius());
        content.put(COLUMN_ACTIVE, true);

        db.update(DESTINATIONS_TABLE, content, "name = '" + name + "'", null);
        return true;
    }

    public int deleteDestination(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DESTINATIONS_TABLE, "name = '" + name + "'", null);
    }

    public Destination getDestination(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + DESTINATIONS_TABLE +" WHERE name = '" + name + "'", null);

        res.moveToFirst();
        LatLng destLatLng = new LatLng(res.getDouble(res.getColumnIndex(COLUMN_LAT)), res.getDouble(res.getColumnIndex(COLUMN_LONG)));
        int destRadius = res.getInt(res.getColumnIndex(COLUMN_RADIUS));
        String destName = res.getString(res.getColumnIndex(COLUMN_NAME));

        Destination tmp = new Destination(destLatLng,
                destRadius,
                destName,
                true);

        if(!res.isClosed()) {
            res.close();
        }
        return tmp;
    }
}
