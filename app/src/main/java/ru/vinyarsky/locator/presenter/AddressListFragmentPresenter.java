package ru.vinyarsky.locator.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.vinyarsky.locator.db.DbRepository;
import ru.vinyarsky.locator.net.NetRepository;

final public class AddressListFragmentPresenter extends Presenter {

    private final String BUNDLE_DATA = "address_list_fragment_presenter_data";

    private final AddressListFragmentView view;

    private ArrayList<String> addressList = new ArrayList<>();

    public AddressListFragmentPresenter(DbRepository dbRepository, NetRepository netRepository, AddressListFragmentView view) {
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
            ArrayList<String> data = savedInstanceState.getStringArrayList(BUNDLE_DATA);
            if (data != null)
                addressList = data;
            else
                addressList.clear();
        }
        else {
            autoDispose(
                    dbRepository.addressList
                            // .subscribeOn(Schedulers.io()) has no effect (see BriteDatabase.createQuery docs)
                            .map(addressList -> {
                                ArrayList<String> result = new ArrayList<>(addressList.size());
                                for (int i = 0; i < addressList.size(); i++)
                                    result.add(addressList.get(i).getRepresentation());
                                return result;
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError(error -> hideProgress())
                            .subscribe(addressList -> {
                                this.addressList = addressList;
                                if (isRunning())
                                    view.displayAddressList(addressList);
                            }));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        view.displayAddressList(addressList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(BUNDLE_DATA, addressList);
    }

    public void addAddressClick() {
        view.goToAddAddressFragment();
    }

    public interface AddressListFragmentView extends View {
        void displayAddressList(List<String> data);
        void goToAddAddressFragment();
    }
}
