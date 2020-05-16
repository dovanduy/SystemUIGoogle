// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.res.Configuration;
import com.android.internal.annotations.VisibleForTesting;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;

public class RemoteInputQuickSettingsDisabler implements ConfigurationListener
{
    private final CommandQueue mCommandQueue;
    private Context mContext;
    private int mLastOrientation;
    @VisibleForTesting
    boolean mRemoteInputActive;
    @VisibleForTesting
    boolean misLandscape;
    
    public RemoteInputQuickSettingsDisabler(final Context mContext, final ConfigurationController configurationController, final CommandQueue mCommandQueue) {
        this.mContext = mContext;
        this.mCommandQueue = mCommandQueue;
        this.mLastOrientation = mContext.getResources().getConfiguration().orientation;
        configurationController.addCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    private void recomputeDisableFlags() {
        this.mCommandQueue.recomputeDisableFlags(this.mContext.getDisplayId(), true);
    }
    
    public int adjustDisableFlags(final int n) {
        int n2 = n;
        if (this.mRemoteInputActive) {
            n2 = n;
            if (this.misLandscape) {
                n2 = (n | 0x1);
            }
        }
        return n2;
    }
    
    @Override
    public void onConfigChanged(final Configuration configuration) {
        final int orientation = configuration.orientation;
        if (orientation != this.mLastOrientation) {
            this.misLandscape = (orientation == 2);
            this.mLastOrientation = configuration.orientation;
            this.recomputeDisableFlags();
        }
    }
    
    public void setRemoteInputActive(final boolean mRemoteInputActive) {
        if (this.mRemoteInputActive != mRemoteInputActive) {
            this.mRemoteInputActive = mRemoteInputActive;
            this.recomputeDisableFlags();
        }
    }
}
