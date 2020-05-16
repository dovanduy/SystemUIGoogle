// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import java.lang.ref.WeakReference;
import android.view.ViewGroup$LayoutParams;
import android.util.Log;
import android.view.View;
import android.content.Context;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.core.view.ActionProvider;

public class MediaRouteActionProvider extends ActionProvider
{
    private boolean mAlwaysVisible;
    private MediaRouteButton mButton;
    private MediaRouteDialogFactory mDialogFactory;
    private final MediaRouter mRouter;
    private MediaRouteSelector mSelector;
    private boolean mUseDynamicGroup;
    
    public MediaRouteActionProvider(final Context context) {
        super(context);
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mDialogFactory = MediaRouteDialogFactory.getDefault();
        this.mRouter = MediaRouter.getInstance(context);
        new MediaRouterCallback(this);
    }
    
    @Override
    public boolean isVisible() {
        final boolean mAlwaysVisible = this.mAlwaysVisible;
        boolean b = true;
        if (!mAlwaysVisible) {
            b = (this.mRouter.isRouteAvailable(this.mSelector, 1) && b);
        }
        return b;
    }
    
    @Override
    public View onCreateActionView() {
        if (this.mButton != null) {
            Log.e("MRActionProvider", "onCreateActionView: this ActionProvider is already associated with a menu item. Don't reuse MediaRouteActionProvider instances! Abandoning the old menu item...");
        }
        (this.mButton = this.onCreateMediaRouteButton()).setCheatSheetEnabled(true);
        this.mButton.setRouteSelector(this.mSelector);
        if (this.mUseDynamicGroup) {
            this.mButton.enableDynamicGroup();
        }
        this.mButton.setAlwaysVisible(this.mAlwaysVisible);
        this.mButton.setDialogFactory(this.mDialogFactory);
        this.mButton.setLayoutParams(new ViewGroup$LayoutParams(-2, -1));
        return this.mButton;
    }
    
    public MediaRouteButton onCreateMediaRouteButton() {
        return new MediaRouteButton(this.getContext());
    }
    
    @Override
    public boolean onPerformDefaultAction() {
        final MediaRouteButton mButton = this.mButton;
        return mButton != null && mButton.showDialog();
    }
    
    @Override
    public boolean overridesItemVisibility() {
        return true;
    }
    
    void refreshRoute() {
        this.refreshVisibility();
    }
    
    private static final class MediaRouterCallback extends Callback
    {
        private final WeakReference<MediaRouteActionProvider> mProviderWeak;
        
        public MediaRouterCallback(final MediaRouteActionProvider referent) {
            this.mProviderWeak = new WeakReference<MediaRouteActionProvider>(referent);
        }
        
        private void refreshRoute(final MediaRouter mediaRouter) {
            final MediaRouteActionProvider mediaRouteActionProvider = this.mProviderWeak.get();
            if (mediaRouteActionProvider != null) {
                mediaRouteActionProvider.refreshRoute();
            }
            else {
                mediaRouter.removeCallback((MediaRouter.Callback)this);
            }
        }
        
        @Override
        public void onProviderAdded(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
            this.refreshRoute(mediaRouter);
        }
        
        @Override
        public void onProviderChanged(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
            this.refreshRoute(mediaRouter);
        }
        
        @Override
        public void onProviderRemoved(final MediaRouter mediaRouter, final ProviderInfo providerInfo) {
            this.refreshRoute(mediaRouter);
        }
        
        @Override
        public void onRouteAdded(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            this.refreshRoute(mediaRouter);
        }
        
        @Override
        public void onRouteChanged(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            this.refreshRoute(mediaRouter);
        }
        
        @Override
        public void onRouteRemoved(final MediaRouter mediaRouter, final RouteInfo routeInfo) {
            this.refreshRoute(mediaRouter);
        }
    }
}
