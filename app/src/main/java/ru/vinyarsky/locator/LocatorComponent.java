package ru.vinyarsky.locator;

import javax.inject.Singleton;

import dagger.Component;

import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.location.LocationRepository;
import ru.vinyarsky.locator.net.NetRepository;
import ru.vinyarsky.locator.ui.UiComponent;
import ru.vinyarsky.locator.ui.UiModule;

@Component(modules = {LocatorModule.class})
@Singleton
public interface LocatorComponent {

    DbRepository getDbRepository();
    NetRepository getNetRepository();
    LocationRepository getLocationRepository();

    UiComponent createUiComponent(UiModule uiModule);
}
