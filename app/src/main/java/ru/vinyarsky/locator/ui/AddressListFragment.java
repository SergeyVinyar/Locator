package ru.vinyarsky.locator.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.vinyarsky.locator.LocatorApplication;
import ru.vinyarsky.locator.presenter.AddressListFragmentPresenter;
import ru.vinyarsky.ui.R;

public final class AddressListFragment extends Fragment implements AddressListFragmentPresenter.AddressListFragmentView {

    public static AddressListFragment newInstance() { // For possible arguments in future
        return new AddressListFragment();
    }

    @BindView(R.id.address_list_fragment_address_list)
    RecyclerView recyclerView;

    @BindView(R.id.address_list_fragment_fab)
    FloatingActionButton fabView;

    private final AddressListFragmentPresenter presenter;
// TODO Write Empty if empty
    private Listener listener;

    public AddressListFragment() {
        presenter = LocatorApplication
                .getLocatorComponent()
                .createUiComponent(new UiModule(this))
                .getAddressListFragmentPresenter();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (!(activity instanceof Listener))
            throw new IllegalArgumentException("Activity must implement AddressListFragment.Listener");
        listener = (Listener) activity;
        presenter.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_list, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new AddressAdapter());

        fabView.setOnClickListener((v) -> presenter.addAddressClick());

        return view;
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
    public void showProgress() {
        listener.showProgress();
    }

    @Override
    public void hideProgress() {
        listener.hideProgress();
    }

    @Override
    public void displayAddressList(List<String> data) {
        ((AddressAdapter) recyclerView.getAdapter()).swapAddressList(data);
    }

    @Override
    public void goToAddAddressFragment() {
        listener.showAddAddressFragment();
    }

    private static class AddressAdapter extends RecyclerView.Adapter {

        List<String> addressList;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_address_list_item, parent, false);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String address = addressList.get(position);

            AddressViewHolder addressViewHolder = (AddressViewHolder) holder;
            addressViewHolder.representationView.setText(address);
        }

        @Override
        public int getItemCount() {
            return addressList != null ? addressList.size() : 0;
        }

        public void swapAddressList(List<String> addressList) {
            this.addressList = addressList;
            notifyDataSetChanged();
        }
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.address_list_fragment_item_representation)
        public TextView representationView;

        AddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface Listener {
        void showAddAddressFragment();

        void showProgress();
        void hideProgress();
    }
}
