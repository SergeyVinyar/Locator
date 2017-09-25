package ru.vinyarsky.locator.presenter;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import ru.vinyarsky.locator.db.DbAddress;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.location.LocationRepository;
import ru.vinyarsky.locator.net.NetRepository;

public final class DistanceListFragmentPresenter extends Presenter {

    private final String BUNDLE_DATA = "distance_list_fragment_presenter_data";

    private final DbRepository dbRepository;
    private final LocationRepository locationRepository;

    private final DistanceListFragmentView view;

    private ArrayList<DistanceItem> distanceList = new ArrayList<>();

    public DistanceListFragmentPresenter(DbRepository dbRepository, LocationRepository locationRepository, DistanceListFragmentView view) {
        this.dbRepository = dbRepository;
        this.locationRepository = locationRepository;
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
            ArrayList<DistanceItem> data = savedInstanceState.getParcelableArrayList(BUNDLE_DATA);
            if (data != null)
                distanceList = data;
            else
                distanceList.clear();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        view.displayList(distanceList);
        autoDispose(
                Observable.combineLatest(dbRepository.addressList, locationRepository.lastLocation, (dbAddressList, location) -> {
                            ArrayList<DistanceItem> result = new ArrayList<>(dbAddressList.size());
                            for (int i = 0; i < dbAddressList.size(); i++) {
                                DbAddress dbAddress = dbAddressList.get(i);
                                Location addressLocation = new Location("");
                                addressLocation.setLatitude(dbAddress.getLatitude());
                                addressLocation.setLongitude(dbAddress.getLongitude());
                                int distance = 0;
                                if (location != null) // No Google play services or no permissions or no last location
                                    distance = (int) location.distanceTo(addressLocation) / 1000;
                                result.add(new DistanceItem(dbAddress.getRepresentation(), distance));
                            }
                            return result;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(distanceItems -> {
                    view.displayList(distanceItems);
                }));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_DATA, distanceList);
    }

    public interface DistanceListFragmentView extends View {
        void displayList(List<DistanceItem> data);
    }

    public static class DistanceItem implements Parcelable {

        private final String representation;
        private final int distance;

        public DistanceItem(String representation, int distance) {
            this.representation = representation;
            this.distance = distance;
        }

        public String getRepresentation() {
            return representation;
        }

        public int getDistance() {
            return distance;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(representation);
            out.writeInt(distance);
        }

        public static final Parcelable.Creator<DistanceItem> CREATOR = new Parcelable.Creator<DistanceItem>() {

            @Override
            public DistanceItem createFromParcel(Parcel in) {
                return new DistanceItem(in);
            }

            @Override
            public DistanceItem[] newArray(int size) {
                return new DistanceItem[size];
            }
        };

        private DistanceItem(Parcel in) {
            representation = in.readString();
            distance = in.readInt();
        }
    }
}
