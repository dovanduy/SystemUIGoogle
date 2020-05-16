// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import android.graphics.Rect;
import android.content.res.Configuration;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.provider.Settings$Global;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.SystemUI;

public class Recents extends SystemUI implements Callbacks
{
    private final CommandQueue mCommandQueue;
    private final RecentsImplementation mImpl;
    
    public Recents(final Context context, final RecentsImplementation mImpl, final CommandQueue mCommandQueue) {
        super(context);
        this.mImpl = mImpl;
        this.mCommandQueue = mCommandQueue;
    }
    
    private boolean isUserSetup() {
        final ContentResolver contentResolver = super.mContext.getContentResolver();
        boolean b = false;
        if (Settings$Global.getInt(contentResolver, "device_provisioned", 0) != 0) {
            b = b;
            if (Settings$Secure.getInt(contentResolver, "user_setup_complete", 0) != 0) {
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public void appTransitionFinished(final int n) {
        if (super.mContext.getDisplayId() == n) {
            this.mImpl.onAppTransitionFinished();
        }
    }
    
    @Override
    public void cancelPreloadRecentApps() {
        if (!this.isUserSetup()) {
            return;
        }
        this.mImpl.cancelPreloadRecentApps();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        this.mImpl.dump(printWriter);
    }
    
    public void growRecents() {
        this.mImpl.growRecents();
    }
    
    @Override
    public void hideRecentApps(final boolean b, final boolean b2) {
        if (!this.isUserSetup()) {
            return;
        }
        this.mImpl.hideRecentApps(b, b2);
    }
    
    public void onBootCompleted() {
        this.mImpl.onBootCompleted();
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        this.mImpl.onConfigurationChanged(configuration);
    }
    
    @Override
    public void preloadRecentApps() {
        if (!this.isUserSetup()) {
            return;
        }
        this.mImpl.preloadRecentApps();
    }
    
    @Override
    public void showRecentApps(final boolean b) {
        if (!this.isUserSetup()) {
            return;
        }
        this.mImpl.showRecentApps(b);
    }
    
    public boolean splitPrimaryTask(final int n, final Rect rect, final int n2) {
        return this.isUserSetup() && this.mImpl.splitPrimaryTask(n, rect, n2);
    }
    
    @Override
    public void start() {
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
        this.mImpl.onStart(super.mContext);
    }
    
    @Override
    public void toggleRecentApps() {
        if (!this.isUserSetup()) {
            return;
        }
        this.mImpl.toggleRecentApps();
    }
}
