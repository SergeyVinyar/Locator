package ru.vinyarsky.locator.ui;

import dagger.Subcomponent;
import ru.vinyarsky.locator.presenter.AddAddressFragmentPresenter;
import ru.vinyarsky.locator.presenter.AddressListFragmentPresenter;
import ru.vinyarsky.locator.presenter.AddressesOnMapFragmentPresenter;

@Subcomponent(modules = {UiModule.class})
@UiScope
public interface UiComponent {

    AddressListFragmentPresenter getAddressListFragmentPresenter();
    AddAddressFragmentPresenter getAddAddressFragmentPresenter();
    AddressesOnMapFragmentPresenter getAddressesOnMapFragmentPresenter();
}
