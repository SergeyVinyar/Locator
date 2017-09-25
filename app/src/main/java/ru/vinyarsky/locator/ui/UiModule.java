package ru.vinyarsky.locator.ui;

import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.location.LocationRepository;
import ru.vinyarsky.locator.net.NetRepository;
import ru.vinyarsky.locator.presenter.AddAddressFragmentPresenter;
import ru.vinyarsky.locator.presenter.AddressListFragmentPresenter;
import ru.vinyarsky.locator.presenter.AddressesOnMapFragmentPresenter;
import ru.vinyarsky.locator.presenter.DistanceListFragmentPresenter;

@Module
public final class UiModule {

    private final Fragment fragment;

    public UiModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @UiScope
    public AddressListFragmentPresenter getAddressListFragmentPresenter(DbRepository dbRepository, NetRepository netRepository) {
        if (!(fragment instanceof AddressListFragmentPresenter.AddressListFragmentView))
            throw new IllegalArgumentException(String.format("Fragment '%s' must implement AddressListFragmentPresenter.AddressListFragmentView", fragment.getClass().getSimpleName()));
        return new AddressListFragmentPresenter(dbRepository, (AddressListFragmentPresenter.AddressListFragmentView) fragment);
    }

    @Provides
    @UiScope
    public AddAddressFragmentPresenter getAddAddressFragmentPresenter(DbRepository dbRepository, NetRepository netRepository) {
        if (!(fragment instanceof AddAddressFragmentPresenter.AddAddressFragmentView))
            throw new IllegalArgumentException(String.format("Fragment '%s' must implement AddAddressFragmentPresenter.AddAddressFragmentView", fragment.getClass().getSimpleName()));
        return new AddAddressFragmentPresenter(dbRepository, netRepository, (AddAddressFragmentPresenter.AddAddressFragmentView) fragment);
    }

    @Provides
    @UiScope
    public AddressesOnMapFragmentPresenter getAddressesOnMapFragmentPresenter(DbRepository dbRepository, NetRepository netRepository) {
        if (!(fragment instanceof AddressesOnMapFragmentPresenter.AddressesOnMapFragmentView))
            throw new IllegalArgumentException(String.format("Fragment '%s' must implement AddressesOnMapFragmentPresenter.AddressesOnMapFragmentView", fragment.getClass().getSimpleName()));
        return new AddressesOnMapFragmentPresenter(dbRepository, (AddressesOnMapFragmentPresenter.AddressesOnMapFragmentView) fragment);
    }

    @Provides
    @UiScope
    public DistanceListFragmentPresenter getDistanceListFragmentPresenter(DbRepository dbRepository, NetRepository netRepository, LocationRepository locationRepository) {
        if (!(fragment instanceof DistanceListFragmentPresenter.DistanceListFragmentView))
            throw new IllegalArgumentException(String.format("Fragment '%s' must implement DistanceListFragmentPresenter.DistanceListFragmentView", fragment.getClass().getSimpleName()));
        return new DistanceListFragmentPresenter(dbRepository, locationRepository, (DistanceListFragmentPresenter.DistanceListFragmentView) fragment);
    }
}
