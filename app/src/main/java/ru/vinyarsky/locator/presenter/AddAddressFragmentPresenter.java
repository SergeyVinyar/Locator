package ru.vinyarsky.locator.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.vinyarsky.locator.db.DbAddress;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.net.NetAddress;
import ru.vinyarsky.locator.net.NetRepository;

final public class AddAddressFragmentPresenter extends Presenter {

    private final String BUNDLE_SEARCH_STRING = "add_address_fragment_presenter_search_string";
    private final String BUNDLE_ADDRESS_LIST = "add_address_fragment_presenter_address_list";

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
        if (savedInstanceState != null) {
//            ArrayList<String> data = savedInstanceState.getStringArrayList(BUNDLE_DATA);
//            if (data != null)
//                addressList = savedInstanceState.getStringArrayList(BUNDLE_DATA);
//            else
//                addressList.clear();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO No dup code
        List<String> uiList = new ArrayList<>(addressList.size());
        addressList.forEach(address -> uiList.add(address.getRepresentation()));
        view.displayAddressList(uiList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        savedInstanceState.putStringArrayList(BUNDLE_DATA, addressList);
    }

    public void searchStringChange(String searchString) {
        this.searchString = searchString;
    }

    public void searchButtonClick() {
        showProgress();
        if (!"".equals(searchString)) {
            autoDispose(
                    netRepository.getAddressList(searchString)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError(error -> hideProgress())
                            .subscribe(netList -> {
                                try {
                                    addressList = netList;
                                    List<String> uiList = new ArrayList<>(netList.size());
                                    netList.forEach(address -> uiList.add(address.getRepresentation()));
                                    view.displayAddressList(uiList);
                                }
                                finally {
                                    hideProgress();
                                }
                            }));
        }
        else {
            try {
                addressList.clear();
                view.displayAddressList(new ArrayList<>(0));
            }
            finally {
                hideProgress();
            }
        }
    }

    public void addressClick(String addressRepresentation) {
        Optional<NetAddress> netAddressOpt = addressList.stream().filter(address -> addressRepresentation.equals(address.getRepresentation())).findAny();
        if (netAddressOpt.isPresent()) {
            showProgress();
            autoDispose(
                    Observable.just(netAddressOpt.get())
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

    public interface AddAddressFragmentView extends View {
        void displayAddressList(List<String> addressList);
        void goToAddressListFragment();
    }
}
