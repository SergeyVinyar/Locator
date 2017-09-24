package ru.vinyarsky.locator.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.vinyarsky.ui.R;

public class MainActivity extends AppCompatActivity
        implements
            AddressListFragment.Listener,
            AddAddressFragment.Listener,
            AddressesOnMapFragment.Listener,
            DistanceListFragment.Listener,
            NavigationView.OnNavigationItemSelectedListener,
            ActivityCompat.OnRequestPermissionsResultCallback {

    @BindView(R.id.activity_main)
    DrawerLayout mainActivity;

    @BindView(R.id.toolbar_layout_main_appbar)
    Toolbar toolbar;

    @BindView(R.id.navview_main)
    NavigationView navigationView;

    @BindView(R.id.appbarlayout_layout_main_appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.progressbar_layout_main_appbar)
    ProgressBar progressBar;

    private final static int ACCESS_COARSE_LOCATION_REQUEST_PERMISSION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainActivity, toolbar, R.string.main_navigation_drawer_open, R.string.main_navigation_drawer_close);
        mainActivity.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Fragment existedFragment = getSupportFragmentManager().findFragmentById(R.id.framelayout_layout_main_appbar_fragment);
        if (existedFragment == null)
            showAddressListFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mainActivity.isDrawerOpen(GravityCompat.START)) {
            mainActivity.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_drawer_search:
                showAddressListFragment();
                break;
            case R.id.menuitem_drawer_distance:
                showDistanceListFragment();
                break;
            case R.id.menuitem_drawer_map:
                showAddressesOnMapFragment();
                break;
            default:
                return false;
        }
        mainActivity.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showAddressListFragment() {
        showSingleTopFragment(AddressListFragment.class, AddressListFragment::newInstance);
    }

    @Override
    public void showAddAddressFragment() {
        Fragment newFragment = AddAddressFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.framelayout_layout_main_appbar_fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void showAddressesOnMapFragment() {
        showSingleTopFragment(AddressesOnMapFragment.class, AddressesOnMapFragment::newInstance);
    }

    public void showDistanceListFragment() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION }, ACCESS_COARSE_LOCATION_REQUEST_PERMISSION_ID);
        else
            showSingleTopFragment(DistanceListFragment.class, DistanceListFragment::newInstance);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_COARSE_LOCATION_REQUEST_PERMISSION_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Ok, do it again
                    showDistanceListFragment();
                } else {
                    // Oops
                    Toast.makeText(this, "No permission - no distance. Sorry, bro.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Shows new fragment. If it's already existed show the existed instance.
     */
    private void showSingleTopFragment(Class fragmentClass, com.annimon.stream.function.Supplier<Fragment> supplyFragment) {
        String fragmentTag = fragmentClass.getName();

        int fragmentId = getBackstackEntryIdForFragmentTag(fragmentTag);
        if (fragmentId == -1) {
            Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.framelayout_layout_main_appbar_fragment);
            Fragment newFragment = supplyFragment.get();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (oldFragment != null)
                transaction.remove(oldFragment);

            transaction.replace(R.id.framelayout_layout_main_appbar_fragment, newFragment);

            if (oldFragment != null)
                transaction.addToBackStack(fragmentTag);

            transaction.commit();
        }
        else {
            getSupportFragmentManager().popBackStackImmediate(fragmentId, 0);
        }
    }

    /**
     * Returns id of a backstack entry for a fragment with particular tag.
     * -1 if not found.
     */
    private int getBackstackEntryIdForFragmentTag(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int id = 0; id < fragmentManager.getBackStackEntryCount(); id++)
            if (tag.equals(fragmentManager.getBackStackEntryAt(id).getName()))
                return id;
        return -1;
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
