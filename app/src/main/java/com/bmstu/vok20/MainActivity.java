package com.bmstu.vok20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.bmstu.vok20.Fragments.AboutFragment;
import com.bmstu.vok20.Fragments.SettingsFragment;
import com.bmstu.vok20.Helpers.PreferenceHelper;
import com.bmstu.vok20.VK.VKDialogsFragment;
import com.bmstu.vok20.VK.VKLongPollService;
import com.bmstu.vok20.VK.VKMessagesFragment;
import com.flurry.android.FlurryAgent;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import android.Manifest;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_WRITE_STORAGE = 112;

    public static final String VK_DIALOGS_FRAGMENT_TAG = "VK_DIALOGS_FRAGMENT_TAG";
    public static final String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG";
    public static final String VK_MESSAGES_FRAGMENT_TAG = "VK_MESSAGES_FRAGMENT_TAG";
    public static final String VK_USERS_FRAGMENT_TAG = "VK_USERS_FRAGMENT_TAG";
    public static final String ABOUT_FRAGMENT_TAG = "ABOUT_FRAGMENT_TAG";

    private VKActionReceiver vkActionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*** Flurry Analitics ***/
        final String FLURRY_KEY = "P5J926ZM3YPF4WTPRPVY";
        FlurryAgent.init(MainActivity.this, FLURRY_KEY);
        /*** ***/

        /*** Get external storage permissions ***/
        boolean hasPermission = (
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE
            );
        }
        /*** ***/
        setBackgroundColor();
        vkActionReceiver = new VKActionReceiver();
        VKLongPollService.startActionUpdateMessages(MainActivity.this);
    }

    private void setBackgroundColor() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(PreferenceHelper.getInstance().getInt(PreferenceHelper.BACKGROUND_COLOR));
    }

    @Override
    protected void onStart() {
        super.onStart();
        vkActionReceiver.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vkActionReceiver.unregister();
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Log.d(TAG, "VK receive access token");
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager
                        .findFragmentById(R.id.content_main)
                        .onActivityResult(requestCode, resultCode, data);
            }
            @Override
            public void onError(VKError error) {
                Log.d(TAG, error.toString());
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

        @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.nav_vk: {
                showVKDialogsFragment();
                break;
            }
            case R.id.nav_settings: {
                showSettingsFragment();
                break;
            }
            case R.id.nav_about: {
                showAboutFragment();
                break;
            }
            default: break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAboutFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_main, new AboutFragment(), ABOUT_FRAGMENT_TAG);
        transaction.commit();
    }

    private void showSettingsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_main, new SettingsFragment(), SETTINGS_FRAGMENT_TAG);
        transaction.commit();
    }

    private void showVKDialogsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_main, new VKDialogsFragment(), VK_DIALOGS_FRAGMENT_TAG);
        transaction.commit();
    }

    private class VKActionReceiver extends BroadcastReceiver {
        private LocalBroadcastManager broadcastManager;

        public VKActionReceiver() {
            super();
            broadcastManager = LocalBroadcastManager.getInstance(MainActivity.this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case VKLongPollService.ACTION_VK_NEW_MESSAGE: {
                    Toast.makeText(context, "New message", Toast.LENGTH_SHORT).show();

                    // TODO: Насколько это законно?
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_main);
                    switch (currentFragment.getTag()) {
                        case VK_DIALOGS_FRAGMENT_TAG: {
                            ((VKDialogsFragment) currentFragment).getVKDialogs();
                        } break;
                        case VK_MESSAGES_FRAGMENT_TAG: {
                            ((VKMessagesFragment) currentFragment).getVKMessageHistory();
                        } break;
                    }
                } break;
            }
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(VKLongPollService.ACTION_VK_NEW_MESSAGE);

            broadcastManager.registerReceiver(this, intentFilter);
        }

        public void unregister() {
            broadcastManager.unregisterReceiver(this);
        }
    };
}

