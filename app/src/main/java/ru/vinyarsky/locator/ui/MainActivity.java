package ru.vinyarsky.locator.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.vinyarsky.ui.R;

public class MainActivity extends AppCompatActivity
        implements
            AddressListFragment.AddressListFragmentListener,
            AddAddressFragment.AddAddressFragmentListener,
            NavigationView.OnNavigationItemSelectedListener {

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
                break;
            case R.id.menuitem_drawer_map:
                break;
            default:
                return false;
        }
        mainActivity.closeDrawer(GravityCompat.START);
        return true;
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
    public void showAddressListFragment() {
        String fragmentTag = AddressListFragment.class.getName();

        int fragmentId = getBackstackEntryIdForFragmentTag(fragmentTag);
        if (fragmentId == -1) {
            Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.framelayout_layout_main_appbar_fragment);
            Fragment newFragment = AddressListFragment.newInstance();

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

    @Override
    public void showAddAddressFragment() {
        Fragment newFragment = AddAddressFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.framelayout_layout_main_appbar_fragment, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
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
