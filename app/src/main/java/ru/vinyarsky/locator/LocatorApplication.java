package ru.vinyarsky.locator;

import android.app.Application;

import ru.vinyarsky.locator.ui.UiComponent;

public final class LocatorApplication extends Application {

    private static LocatorComponent locatorComponent;
    private static UiComponent uiComponent;

    public static LocatorComponent getLocatorComponent() {
        assert locatorComponent != null;
        return locatorComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locatorComponent = DaggerLocatorComponent.builder()
                .locatorModule(new LocatorModule(this.getApplicationContext()))
                .build();
    }
}
