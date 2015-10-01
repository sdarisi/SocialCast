package com.socialcast.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.MediaRouteChooserDialogFragment;
import android.support.v7.app.MediaRouteControllerDialogFragment;
import android.support.v7.media.MediaRouter;
import android.support.v7.app.MediaRouteDialogFactory;
import android.support.v7.media.MediaRouteSelector;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by sdarisi on 7/30/15.
 */
public class FloatingActionMediaRouteButton extends FloatingActionButton {

    private final MediaRouter mRouter;
    private final MediaRouterCallback mCallback;
    private MediaRouteSelector mSelector;
    private MediaRouteDialogFactory mDialogFactory;
    private boolean mAttachedToWindow;
    private boolean mRemoteActive;
    private boolean mIsConnecting;

    public FloatingActionMediaRouteButton(Context context) { this(context, (AttributeSet)null); }

    public FloatingActionMediaRouteButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionMediaRouteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mDialogFactory = MediaRouteDialogFactory.getDefault();
        this.mRouter = android.support.v7.media.MediaRouter.getInstance(context);
        this.mCallback = new MediaRouterCallback();
        this.setClickable(true);
     }

    @NonNull
    public MediaRouteSelector getRouteSelector() {
        return this.mSelector;
    }

    public void setRouteSelector(MediaRouteSelector selector) {
        if(selector == null) {
            throw new IllegalArgumentException("selector must not be null");
        } else {
            if(!this.mSelector.equals(selector)) {
                if(!this.mSelector.isEmpty()) {
                    this.mRouter.removeCallback(this.mCallback);
                }

                if(!selector.isEmpty()) {
                    this.mRouter.addCallback(selector, this.mCallback);
                }

                this.mSelector = selector;
            }

        }
    }

    @NonNull
    public MediaRouteDialogFactory getDialogFactory() {
        return this.mDialogFactory;
    }

    public void setDialogFactory(@NonNull MediaRouteDialogFactory factory) {
        if(factory == null) {
            throw new IllegalArgumentException("factory must not be null");
        } else {
            this.mDialogFactory = factory;
        }
    }

    public boolean showDialog() {
        if(!this.mAttachedToWindow) {
            return false;
        } else {
            FragmentManager fm = this.getFragmentManager();
            if(fm == null) {
                throw new IllegalStateException("The activity must be a subclass of FragmentActivity");
            } else {
                MediaRouter.RouteInfo route = this.mRouter.getSelectedRoute();
                if(!route.isDefault() && route.matchesSelector(this.mSelector)) {
                    if(fm.findFragmentByTag("android.support.v7.mediarouter:MediaRouteControllerDialogFragment") != null) {
                        Log.w("MediaRouteButton", "showDialog(): Route controller dialog already showing!");
                        return false;
                    }

                    MediaRouteControllerDialogFragment f1 = this.mDialogFactory.onCreateControllerDialogFragment();
                    f1.show(fm, "android.support.v7.mediarouter:MediaRouteControllerDialogFragment");
                } else {
                    if(fm.findFragmentByTag("android.support.v7.mediarouter:MediaRouteChooserDialogFragment") != null) {
                        Log.w("MediaRouteButton", "showDialog(): Route chooser dialog already showing!");
                        return false;
                    }

                    MediaRouteChooserDialogFragment f = this.mDialogFactory.onCreateChooserDialogFragment();
                    f.setRouteSelector(this.mSelector);
                    f.show(fm, "android.support.v7.mediarouter:MediaRouteChooserDialogFragment");
                }

                return true;
            }
        }
    }

    private FragmentManager getFragmentManager() {
        Activity activity = this.getActivity();
        return activity instanceof FragmentActivity ?((FragmentActivity)activity).getSupportFragmentManager():null;
    }

    private Activity getActivity() {
        for(Context context = this.getContext(); context instanceof ContextWrapper; context = ((ContextWrapper)context).getBaseContext()) {
            if(context instanceof Activity) {
                return (Activity)context;
            }
        }

        return null;
    }

    public boolean performClick() {
        boolean handled = super.performClick();
        if(!handled) {
            this.playSoundEffect(0);
        }

        return this.showDialog() || handled;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        if(!this.mSelector.isEmpty()) {
            this.mRouter.addCallback(this.mSelector, this.mCallback);
        }

        this.refreshRoute();
    }

    public void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        if(!this.mSelector.isEmpty()) {
            this.mRouter.removeCallback(this.mCallback);
        }

        super.onDetachedFromWindow();
    }

    private void refreshRoute() {
        if(this.mAttachedToWindow) {
            android.support.v7.media.MediaRouter.RouteInfo route = this.mRouter.getSelectedRoute();
            boolean isRemote = !route.isDefault() && route.matchesSelector(this.mSelector);
            boolean isConnecting = isRemote && route.isConnecting();
            boolean needsRefresh = false;
            if(this.mRemoteActive != isRemote) {
                this.mRemoteActive = isRemote;
                needsRefresh = true;
            }

            if(this.mIsConnecting != isConnecting) {
                this.mIsConnecting = isConnecting;
                needsRefresh = true;
            }

            if(needsRefresh) {
                this.refreshDrawableState();
            }

            this.setEnabled(this.mRouter.isRouteAvailable(this.mSelector, 1));
        }

    }

    private final class MediaRouterCallback extends android.support.v7.media.MediaRouter.Callback {
        private MediaRouterCallback() {
        }

        public void onRouteAdded(android.support.v7.media.MediaRouter router, android.support.v7.media.MediaRouter.RouteInfo info) {
            FloatingActionMediaRouteButton.this.refreshRoute();
        }

        public void onRouteRemoved(android.support.v7.media.MediaRouter router, android.support.v7.media.MediaRouter.RouteInfo info) {
            FloatingActionMediaRouteButton.this.refreshRoute();
        }

        public void onRouteChanged(android.support.v7.media.MediaRouter router, android.support.v7.media.MediaRouter.RouteInfo info) {
            FloatingActionMediaRouteButton.this.refreshRoute();
        }

        public void onRouteSelected(android.support.v7.media.MediaRouter router, android.support.v7.media.MediaRouter.RouteInfo info) {
            FloatingActionMediaRouteButton.this.refreshRoute();
        }

        public void onRouteUnselected(android.support.v7.media.MediaRouter router, android.support.v7.media.MediaRouter.RouteInfo info) {
            FloatingActionMediaRouteButton.this.refreshRoute();
        }

        public void onProviderAdded(android.support.v7.media.MediaRouter router, android.support.v7.media.MediaRouter.ProviderInfo provider) {
            FloatingActionMediaRouteButton.this.refreshRoute();
        }

        public void onProviderRemoved(android.support.v7.media.MediaRouter router, android.support.v7.media.MediaRouter.ProviderInfo provider) {
            FloatingActionMediaRouteButton.this.refreshRoute();
        }

        public void onProviderChanged(android.support.v7.media.MediaRouter router, android.support.v7.media.MediaRouter.ProviderInfo provider) {
            FloatingActionMediaRouteButton.this.refreshRoute();
        }
    }
}
