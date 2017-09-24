package ru.vinyarsky.locator.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import ru.vinyarsky.locator.LocatorApplication;
import ru.vinyarsky.locator.presenter.AddAddressFragmentPresenter;
import ru.vinyarsky.ui.R;

public final class AddAddressFragment extends Fragment implements AddAddressFragmentPresenter.AddAddressFragmentView {

    public static AddAddressFragment newInstance() { // For possible arguments in future
        return new AddAddressFragment();
    }

    private final AddAddressFragmentPresenter presenter;

    private Listener listener;

    @BindView(R.id.add_address_fragment_search_text)
    EditText searchText;

    @BindView(R.id.add_address_fragment_search_button)
    Button searchButton;

    @BindView(R.id.add_address_fragment_address_list)
    RecyclerView recyclerView;

    public AddAddressFragment() {
        presenter = LocatorApplication
                .getLocatorComponent()
                .createUiComponent(new UiModule(this))
                .getAddAddressFragmentPresenter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (!(activity instanceof Listener))
            throw new IllegalArgumentException("Activity must implement AddAddressFragment.Listener");
        listener = (Listener) activity;
        presenter.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_address, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new AddressAdapter());

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.searchStringChange(s.toString());
            }
        });

        searchButton.setOnClickListener(v -> presenter.searchButtonClick());

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
    public void goToAddressListFragment() {
        listener.showAddressListFragment();
    }

    private class AddressAdapter extends RecyclerView.Adapter {

        List<String> addressList;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_add_address_list_item, parent, false);
            AddressViewHolder holder = new AddressViewHolder(view);
            holder.representationView
                    .setOnClickListener(v -> presenter.addressClick(holder.representationView.getText().toString()));
            return holder;
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
            this.notifyDataSetChanged();
        }
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.add_address_fragment_list_item_representation)
        public TextView representationView;

        AddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface Listener {
        void showAddressListFragment();

        void showProgress();
        void hideProgress();
    }
}
