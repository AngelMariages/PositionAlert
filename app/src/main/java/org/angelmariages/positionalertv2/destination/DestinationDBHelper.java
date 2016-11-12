package org.angelmariages.positionalertv2.destination;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Objects;

import static android.R.attr.name;

public class DestinationDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyDestinations.db";
    private static final String DESTINATIONS_TABLE = "destinations";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LONG = "longitude";
    public static final String COLUMN_RADIUS = "radius";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_DELETEONREACH = "deleteonreach";
    public static final String COLUMN_REGISTERED = "registered";

    private static final String[] columnNames = new String[]{
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_LAT,
            COLUMN_LONG,
            COLUMN_RADIUS,
            COLUMN_ACTIVE,
            COLUMN_DELETEONREACH,
            COLUMN_REGISTERED
    };

    public DestinationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DESTINATIONS_TABLE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_LAT + " DOUBLE, " +
                COLUMN_LONG + " DOUBLE, " +
                COLUMN_RADIUS + " INTEGER, " +
                COLUMN_ACTIVE + " BOOLEAN, " +
                COLUMN_DELETEONREACH + " BOOLEAN, " +
                COLUMN_REGISTERED + " BOOLEAN)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DESTINATIONS_TABLE);
        onCreate(db);
    }

    public void deleteAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DESTINATIONS_TABLE);
        onCreate(db);
    }

    public long insertDestination(Destination destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(COLUMN_NAME, destination.getName());
        content.put(COLUMN_LAT, destination.getLatitude());
        content.put(COLUMN_LONG, destination.getLongitude());
        content.put(COLUMN_RADIUS, destination.getRadius());
        content.put(COLUMN_ACTIVE, destination.active());
        content.put(COLUMN_DELETEONREACH, destination.removeOnReach());
        content.put(COLUMN_REGISTERED, destination.registered());

        return db.insert(DESTINATIONS_TABLE, null, content);
    }

    public int updateDestination(int destinationID, Destination destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(COLUMN_NAME, destination.getName());
        content.put(COLUMN_LAT, destination.getLatitude());
        content.put(COLUMN_LONG, destination.getLongitude());
        content.put(COLUMN_RADIUS, destination.getRadius());
        content.put(COLUMN_ACTIVE, destination.active());
        content.put(COLUMN_DELETEONREACH, destination.removeOnReach());
        content.put(COLUMN_REGISTERED, destination.registered());

        return db.update(DESTINATIONS_TABLE, content, "id = ?", new String[]{String.valueOf(destinationID)});
    }

    public int updateValue(int destinationID, String column, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(column, value);

        return db.update(DESTINATIONS_TABLE, content, "id = ?", new String[]{String.valueOf(destinationID)});
    }

    public int updateValue(int destinationID, String column, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(column, value);

        return db.update(DESTINATIONS_TABLE, content, "id = ?", new String[]{String.valueOf(destinationID)});
    }

    public int updateValue(int destinationID, String column, boolean value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(column, value);

        return db.update(DESTINATIONS_TABLE, content, "id = ?", new String[]{String.valueOf(destinationID)});
    }

    public int deleteDestination(int destinationID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DESTINATIONS_TABLE, "id = ?", new String[]{String.valueOf(destinationID)});
    }

    public Destination getDestination(int destinationID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(DESTINATIONS_TABLE, columnNames, "id = ?", new String[]{String.valueOf(destinationID)}, null, null, null, null);

        res.moveToFirst();
        int destID = res.getInt(res.getColumnIndex(COLUMN_ID));
        String destName = res.getString(res.getColumnIndex(COLUMN_NAME));
        LatLng destLatLng = new LatLng(res.getDouble(res.getColumnIndex(COLUMN_LAT)), res.getDouble(res.getColumnIndex(COLUMN_LONG)));
        int destRadius = res.getInt(res.getColumnIndex(COLUMN_RADIUS));
        boolean destActive = res.getInt(res.getColumnIndex(COLUMN_ACTIVE)) != 0;
        boolean destDeleteOnReach = res.getInt(res.getColumnIndex(COLUMN_DELETEONREACH)) != 0;
        boolean destRegistered = res.getInt(res.getColumnIndex(COLUMN_REGISTERED)) != 0;

        Destination tmp = new Destination(destID,
                destName,
                destLatLng,
                destRadius,
                destActive,
                destDeleteOnReach,
                destRegistered
        );

        if(!res.isClosed()) {
            res.close();
            db.close();
        }
        return tmp;
    }

    public ArrayList<Destination> getAllDestinations() {
        ArrayList<Destination> destinations = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(DESTINATIONS_TABLE, null, null, null, null, null, "id", null);

        res.moveToFirst();
        while (!res.isAfterLast()) {
            int destID = res.getInt(res.getColumnIndex(COLUMN_ID));
            String destName = res.getString(res.getColumnIndex(COLUMN_NAME));
            LatLng destLatLng = new LatLng(res.getDouble(res.getColumnIndex(COLUMN_LAT)), res.getDouble(res.getColumnIndex(COLUMN_LONG)));
            int destRadius = res.getInt(res.getColumnIndex(COLUMN_RADIUS));
            boolean destActive = res.getInt(res.getColumnIndex(COLUMN_ACTIVE)) != 0;
            boolean destDeleteOnReach = res.getInt(res.getColumnIndex(COLUMN_DELETEONREACH)) != 0;
            boolean destRegistered = res.getInt(res.getColumnIndex(COLUMN_REGISTERED)) != 0;

            Destination tmp = new Destination(destID,
                    destName,
                    destLatLng,
                    destRadius,
                    destActive,
                    destDeleteOnReach,
                    destRegistered
            );

            destinations.add(tmp);

            res.moveToNext();
        }
        res.close();
        db.close();

        return destinations;
    }
}
