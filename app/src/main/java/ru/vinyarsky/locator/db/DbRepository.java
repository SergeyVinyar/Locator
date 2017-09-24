package ru.vinyarsky.locator.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;

import io.reactivex.Observable;

public final class DbRepository {

    private static final String ADDRESSES_TABLE = "addresses";

    private static final String ADDRESSES_ID = "id";
    private static final String ADDRESSES_REPRESENTATION = "representation";
    private static final String ADDRESSES_LATITUDE = "latitude";
    private static final String ADDRESSES_LONGITUDE = "longitude";

    private final BriteDatabase db;

    public final Observable<ArrayList<DbAddress>> addressList;

    public DbRepository(BriteDatabase db) {
        this.db = db;
        this.addressList = db.createQuery(ADDRESSES_TABLE, String.format("SELECT ROWID as %s, * FROM %s", ADDRESSES_ID, ADDRESSES_TABLE))
                    .flatMap(query -> {
                        ArrayList<DbAddress> list = new ArrayList<>();
                        Cursor cursor = query.run();
                        if (cursor != null) {
                            try {
                                while (cursor.moveToNext())
                                    list.add(new DbAddress(
                                            cursor.getLong(cursor.getColumnIndex(ADDRESSES_ID)),
                                            cursor.getString(cursor.getColumnIndex(ADDRESSES_REPRESENTATION)),
                                            cursor.getDouble(cursor.getColumnIndex(ADDRESSES_LATITUDE)),
                                            cursor.getDouble(cursor.getColumnIndex(ADDRESSES_LONGITUDE))));
                            }
                            finally {
                                cursor.close();
                            }

                        }
                        return Observable.just(list);
                    });
    }

    public Observable<Long> saveAddress(DbAddress address) {
        ContentValues values = new ContentValues(3);
        values.put(ADDRESSES_REPRESENTATION, address.getRepresentation());
        values.put(ADDRESSES_LATITUDE, address.getLatitude());
        values.put(ADDRESSES_LONGITUDE, address.getLongitude());
        return Observable.defer(() -> Observable.just(db.insert(ADDRESSES_TABLE, values)));
    }

    public static void createTables(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (%s TEXT, %s NUMBER, %s NUMBER)", ADDRESSES_TABLE, ADDRESSES_REPRESENTATION, ADDRESSES_LATITUDE, ADDRESSES_LONGITUDE));
    }
}
