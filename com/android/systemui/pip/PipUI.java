// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.os.UserManager;
import com.android.systemui.shared.recents.IPinnedStackAnimationListener;
import android.content.res.Configuration;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.SystemUI;

public class PipUI extends SystemUI implements Callbacks
{
    private final CommandQueue mCommandQueue;
    private BasePipManager mPipManager;
    
    public PipUI(final Context context, final CommandQueue mCommandQueue, final BasePipManager mPipManager) {
        super(context);
        this.mCommandQueue = mCommandQueue;
        this.mPipManager = mPipManager;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final BasePipManager mPipManager = this.mPipManager;
        if (mPipManager == null) {
            return;
        }
        mPipManager.dump(printWriter);
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final BasePipManager mPipManager = this.mPipManager;
        if (mPipManager == null) {
            return;
        }
        mPipManager.onConfigurationChanged(configuration);
    }
    
    public void setPinnedStackAnimationListener(final IPinnedStackAnimationListener pinnedStackAnimationListener) {
        final BasePipManager mPipManager = this.mPipManager;
        if (mPipManager != null) {
            mPipManager.setPinnedStackAnimationListener(pinnedStackAnimationListener);
        }
    }
    
    public void setPinnedStackAnimationType(final int pinnedStackAnimationType) {
        final BasePipManager mPipManager = this.mPipManager;
        if (mPipManager != null) {
            mPipManager.setPinnedStackAnimationType(pinnedStackAnimationType);
        }
    }
    
    public void setShelfHeight(final boolean b, final int n) {
        final BasePipManager mPipManager = this.mPipManager;
        if (mPipManager == null) {
            return;
        }
        mPipManager.setShelfHeight(b, n);
    }
    
    @Override
    public void showPictureInPictureMenu() {
        this.mPipManager.showPictureInPictureMenu();
    }
    
    @Override
    public void start() {
        if (!super.mContext.getPackageManager().hasSystemFeature("android.software.picture_in_picture")) {
            return;
        }
        if (UserManager.get(super.mContext).getUserHandle() == 0) {
            this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
            return;
        }
        throw new IllegalStateException("Non-primary Pip component not currently supported.");
    }
}
