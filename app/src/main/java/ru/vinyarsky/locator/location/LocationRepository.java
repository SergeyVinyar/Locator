package ru.vinyarsky.locator.location;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;

import io.reactivex.Observable;
import io.reactivex.Observer;

// For the sake of simplicity I ommited subscribing to update location events.
// Let's hope we have some "last location" on a device.

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
