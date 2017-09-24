package ru.vinyarsky.locator.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import ru.vinyarsky.locator.db.DbAddress;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.net.NetAddress;
import ru.vinyarsky.locator.net.NetRepository;

final public class AddAddressFragmentPresenter extends Presenter {

    private final AddAddressFragmentView view;

    private String searchString;
    private ArrayList<NetAddress> addressList = new ArrayList<>();

    public AddAddressFragmentPresenter(DbRepository dbRepository, NetRepository netRepository, AddAddressFragmentView view) {
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
        // It would be great to save and restore data here like it is done in other presenters,
        // but I'm a bit tired of it already...
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Yeah. No saving
    }

    public void searchStringChange(String searchString) {
        this.searchString = searchString;
    }

    public void searchButtonClick() {
        showProgress();
        if (searchString != null && !"".equals(searchString)) {
            autoDispose(
                    netRepository.getAddressList(searchString, 10)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError(error -> hideProgress())
                            .subscribe(netList -> {
                                try {
                                    addressList = netList;
                                    view.displayAddressList(convertNetAddressesToStrings(addressList));
                                }
                                finally {
                                    hideProgress();
                                }
                            }));
        }
        else {
            try {
                addressList.clear();
                view.displayAddressList(convertNetAddressesToStrings(addressList));
            }
            finally {
                hideProgress();
            }
        }
    }

    public void addressClick(String addressRepresentation) {
        NetAddress address = null;
        for (int i = 0; i < addressList.size(); i++) {
            if (addressRepresentation.equals(addressList.get(i).getRepresentation())) {
                address = addressList.get(i);
                break;
            }
        }
        if (address != null) {
            showProgress();
            autoDispose(
                    Observable.just(address)
                            .observeOn(Schedulers.io())
                            .flatMap(netAddress -> dbRepository.saveAddress(new DbAddress(netAddress.getRepresentation(), netAddress.getLatitude(), netAddress.getLongitude())))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError(error -> hideProgress())
                            .subscribe(id -> {
                                hideProgress();
                                view.goToAddressListFragment();
                            }));
        }
    }

    private List<String> convertNetAddressesToStrings(ArrayList<NetAddress> data) {
        List<String> result = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++)
            result.add(data.get(i).getRepresentation());
        return result;
    }

    public interface AddAddressFragmentView extends View {
        void displayAddressList(List<String> addressList);
        void goToAddressListFragment();
    }
}
