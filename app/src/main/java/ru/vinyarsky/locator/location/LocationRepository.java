package ru.vinyarsky.locator.location;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.Task;

import io.reactivex.Observable;
import io.reactivex.Observer;

public final class LocationRepository {

    public final Observable<Location> lastLocation;
    
    public LocationRepository(FusedLocationProviderClient locationClient) {
        this.lastLocation = new Observable<Location>() {
            @Override
            protected void subscribeActual(Observer<? super Location> observer) {
                try {
                    Task<Location> task = locationClient.getLastLocation();
                    task.addOnSuccessListener(location -> {
                        observer.onNext(location);
                        observer.onComplete();
                    });
                    task.addOnFailureListener((e) -> {
                        observer.onError(e);
                    });
                }
                catch (SecurityException e) {
                    observer.onError(e); // MainActivity is responsible for requesting permissions
                }
            }
        };
    }
}
