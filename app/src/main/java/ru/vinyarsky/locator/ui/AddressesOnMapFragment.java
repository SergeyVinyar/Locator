package ru.vinyarsky.locator.ui;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ru.vinyarsky.locator.LocatorApplication;
import ru.vinyarsky.locator.presenter.AddressesOnMapFragmentPresenter;

public final class AddressesOnMapFragment extends SupportMapFragment
        implements
            OnMapReadyCallback,
            AddressesOnMapFragmentPresenter.AddressesOnMapFragmentView {

    public static SupportMapFragment newInstance() {
        return new AddressesOnMapFragment();
    }

    private final AddressesOnMapFragmentPresenter presenter;

    private Listener listener;
    private GoogleMap googleMap;

    public AddressesOnMapFragment() {
        presenter = LocatorApplication
                .getLocatorComponent()
                .createUiComponent(new UiModule(this))
                .getAddressesOnMapFragmentPresenter();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Activity activity = getActivity();
        if (!(activity instanceof Listener))
            throw new IllegalArgumentException("Activity must implement AddressesOnMapFragment.Listener");
        listener = (Listener) activity;
        presenter.onCreate(bundle);
        this.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        presenter.onMapReady();
    }

    @Override
    public void displayMarkers(List<AddressesOnMapFragmentPresenter.AddressMarker> data) {
        data.forEach(marker -> {
            MarkerOptions options = new MarkerOptions();
            options.title(marker.getRepresentation());
            options.position(new LatLng(marker.getLatitude(), marker.getLongitude()));
            googleMap.addMarker(options);
        });
    }

    @Override
    public void showProgress() {
        listener.showProgress();
    }

    @Override
    public void hideProgress() {
        listener.hideProgress();
    }

    public interface Listener {
        void showProgress();
        void hideProgress();
    }
}
