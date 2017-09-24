package ru.vinyarsky.locator.presenter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.net.NetRepository;

public final class AddressesOnMapFragmentPresenter extends Presenter {

    private final String BUNDLE_DATA = "Addresses_on_map_fragment_presenter_data";

    private final AddressesOnMapFragmentView view;

    private ArrayList<AddressMarker> markersList = new ArrayList<>();

    private boolean isMapReady = false;

    public AddressesOnMapFragmentPresenter(DbRepository dbRepository, NetRepository netRepository, AddressesOnMapFragmentView view) {
        super(dbRepository, netRepository);
        this.view = view;
    }

    @Override
    protected View getView() {
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ArrayList<AddressMarker> data = savedInstanceState.getParcelableArrayList(BUNDLE_DATA);
            if (data != null)
                markersList = data;
            else
                markersList.clear();
        }
        else {
            autoDispose(
                    dbRepository.addressList
                            // .subscribeOn(Schedulers.io()) has no effect (see BriteDatabase.createQuery docs)
                            .map(addressList -> {
                                ArrayList<AddressMarker> result = new ArrayList<>(addressList.size());
                                addressList.forEach(address -> result.add(new AddressMarker(address.getRepresentation(), address.getLatitude(), address.getLongitude())));
                                return result;
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(addressList -> {
                                this.markersList = addressList;
                                if (isRunning() && isMapReady)
                                    view.displayMarkers(markersList);
                            }));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_DATA, markersList);
    }

    public void onMapReady() {
        view.displayMarkers(markersList);
        isMapReady = true;
    }

    public interface AddressesOnMapFragmentView extends View {
        void displayMarkers(List<AddressMarker> data);
    }

    public static class AddressMarker implements Parcelable {

        private final String representation;
        private final double latitude;
        private final double longitude;

        public AddressMarker(String representation, double latitude, double longitude) {
            this.representation = representation;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getRepresentation() {
            return representation;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(representation);
            out.writeDouble(latitude);
            out.writeDouble(longitude);
        }

        public static final Parcelable.Creator<AddressMarker> CREATOR = new Parcelable.Creator<AddressMarker>() {

            @Override
            public AddressMarker createFromParcel(Parcel in) {
                return new AddressMarker(in);
            }

            @Override
            public AddressMarker[] newArray(int size) {
                return new AddressMarker[size];
            }
        };

        private AddressMarker(Parcel in) {
            representation = in.readString();
            latitude = in.readDouble();
            longitude = in.readDouble();
        }
    }
}
