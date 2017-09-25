package ru.vinyarsky.locator.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.net.NetRepository;

abstract class Presenter {

    private final String BUNDLE_PROGRESS_VISIBLE = "presenter_progress_visible";

    private Boolean progressVisible;
    private CompositeDisposable compositeDisposable;

    private Boolean running = false;

    protected abstract View getView();

    public void onCreate(@Nullable Bundle savedInstanceState) {
        progressVisible = savedInstanceState != null && savedInstanceState.getBoolean(BUNDLE_PROGRESS_VISIBLE, false);
    }

    public void onStart() {
        running = true;
        if (progressVisible)
            getView().showProgress();
        else
            getView().hideProgress();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUNDLE_PROGRESS_VISIBLE, progressVisible);
    }

    public void onStop() {
        running = false;
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    protected void autoDispose(Disposable disposable) {
        if (compositeDisposable == null)
            compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
    }

    protected void showProgress() {
        if (!progressVisible && isRunning()) {
            getView().showProgress();
            progressVisible = true;
        }
    }

    protected void hideProgress() {
        if (progressVisible && isRunning()) {
            getView().hideProgress();
            progressVisible = false;
        }
    }

    protected Boolean isRunning() {
        return running;
    }

    protected interface View {
        void showProgress();
        void hideProgress();
    }
}
