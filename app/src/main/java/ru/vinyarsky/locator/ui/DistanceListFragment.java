package ru.vinyarsky.locator.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ru.vinyarsky.locator.presenter.DistanceListFragmentPresenter;
import ru.vinyarsky.ui.R;

public final class DistanceListFragment extends Fragment implements DistanceListFragmentPresenter.DistanceListFragmentView {

    public static DistanceListFragment newInstance() { // For possible arguments in future
        return new DistanceListFragment();
    }

    @BindView(R.id.distance_list_fragment_list)
    RecyclerView recyclerView;

    @BindView(R.id.distance_list_fragment_no_data_label)
    TextView noDataLabel;

    private final DistanceListFragmentPresenter presenter;

    private Listener listener;

    public DistanceListFragment() {
        presenter = LocatorApplication
                .getLocatorComponent()
                .createUiComponent(new UiModule(this))
                .getDistanceListFragmentPresenter();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (!(activity instanceof Listener))
            throw new IllegalArgumentException("Activity must implement DistanceListFragment.Listener");
        listener = (Listener) activity;
        presenter.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_distance_list, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new DistanceListAdapter());

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
    public void displayList(List<DistanceListFragmentPresenter.DistanceItem> data) {
        ((DistanceListAdapter) recyclerView.getAdapter()).swapDistanceList(data);
        if (data.size() == 0)
            noDataLabel.setVisibility(View.VISIBLE);
        else
            noDataLabel.setVisibility(View.GONE);
    }

    private static class DistanceListAdapter extends RecyclerView.Adapter {

        List<DistanceListFragmentPresenter.DistanceItem> distanceList;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_distance_list_item, parent, false);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DistanceListFragmentPresenter.DistanceItem item = distanceList.get(position);

            AddressViewHolder addressViewHolder = (AddressViewHolder) holder;
            addressViewHolder.representationView.setText(item.getRepresentation());
            addressViewHolder.distanceView.setText(Integer.toString(item.getDistance()));
        }

        @Override
        public int getItemCount() {
            return distanceList != null ? distanceList.size() : 0;
        }

        public void swapDistanceList(List<DistanceListFragmentPresenter.DistanceItem> addressList) {
            this.distanceList = addressList;
            notifyDataSetChanged();
        }
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.distance_list_fragment_item_representation)
        public TextView representationView;

        @BindView(R.id.distance_list_fragment_item_distance)
        public TextView distanceView;

        AddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface Listener {
        void showProgress();
        void hideProgress();
    }
}
