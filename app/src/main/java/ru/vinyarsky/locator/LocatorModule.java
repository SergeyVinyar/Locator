package ru.vinyarsky.locator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.location.LocationRepository;
import ru.vinyarsky.locator.net.NetRepository;

@Module
public final class LocatorModule {

    private Context context;

    public LocatorModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context getContext() {
        return this.context;
    }

    @Provides
    @Singleton
    public DbRepository getDbRepository(Context context) {
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(new SqlHelper(context), Schedulers.io());
        return new DbRepository(db);
    }

    @Provides
    @Singleton
    public NetRepository getNetRepository() {
        return new NetRepository();
    }

    @Provides
    @Singleton
    public FusedLocationProviderClient getLocationClient(Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }

    @Provides
    @Singleton
    public LocationRepository getLocationRepository(FusedLocationProviderClient locationClient) {
        return new LocationRepository(locationClient);
    }

    private static class SqlHelper extends SQLiteOpenHelper {

        private static int DB_VERSION = 1;

        SqlHelper(Context context) {
            super(context, "data", null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            DbRepository.createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Some upgrade procedure
        }
    }
}
