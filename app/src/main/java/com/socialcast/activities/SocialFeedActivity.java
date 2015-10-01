package com.socialcast.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastControllerActivity;
import com.socialcast.R;
import com.socialcast.ui.FloatingActionMediaRouteButton;
import com.socialcast.ui.TabPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SocialFeedActivity extends AppCompatActivity {

    public static final double VOLUME_INCREMENT = 0.05;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    @Bind(R.id.pager)
    ViewPager pager;

    TabPagerAdapter adapter;
    private MediaRouteSelector selector;
    private VideoCastManager castManager;
    private MenuItem mediaRouteMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);

        setContentView(R.layout.activity_social_feed);
        ButterKnife.bind(this);

        // initialize VideoCastManager
        VideoCastManager.
                initialize(this, getString(R.string.cast_app_id), VideoCastControllerActivity.class, null).
                setVolumeStep(VOLUME_INCREMENT).
                enableFeatures(VideoCastManager.FEATURE_NOTIFICATION |
                        VideoCastManager.FEATURE_LOCKSCREEN |
                        VideoCastManager.FEATURE_WIFI_RECONNECT |
                        VideoCastManager.FEATURE_CAPTIONS_PREFERENCE |
                        VideoCastManager.FEATURE_DEBUGGING);

        castManager = VideoCastManager.getInstance();

        setSupportActionBar(toolbar);

        castManager.reconnectSessionIfPossible();

        adapter = new TabPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pager.getLayoutParams();
//        layoutParams.topMargin = 2 * this.getResources().getDimensionPixelSize(R.dimen.toolbar_height);
//        tabLayout.setLayoutParams(layoutParams);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_social_feed, menu);

        // Attach the MediaRouteSelector to the menu item
        mediaRouteMenuItem = castManager.
                addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_show_queue).setVisible(castManager.isConnected());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
