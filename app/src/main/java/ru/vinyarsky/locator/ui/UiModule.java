package ru.vinyarsky.locator.ui;

import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.net.NetRepository;
import ru.vinyarsky.locator.presenter.AddAddressFragmentPresenter;
import ru.vinyarsky.locator.presenter.AddressListFragmentPresenter;

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
        return new AddressListFragmentPresenter(dbRepository, netRepository, (AddressListFragmentPresenter.AddressListFragmentView) fragment);
    }

    @Provides
    @UiScope
    public AddAddressFragmentPresenter getAddAddressFragmentPresenter(DbRepository dbRepository, NetRepository netRepository) {
        if (!(fragment instanceof AddAddressFragmentPresenter.AddAddressFragmentView))
            throw new IllegalArgumentException(String.format("Fragment '%s' must implement AddAddressFragmentPresenter.AddAddressFragmentView", fragment.getClass().getSimpleName()));
        return new AddAddressFragmentPresenter(dbRepository, netRepository, (AddAddressFragmentPresenter.AddAddressFragmentView) fragment);
    }
}
