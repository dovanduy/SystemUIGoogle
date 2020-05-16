// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.stackdivider;

import android.os.RemoteException;
import android.view.SurfaceControl$Builder;
import android.view.SurfaceSession;
import android.view.SurfaceControl$Transaction;
import android.app.ActivityManager$RunningTaskInfo;
import android.graphics.Rect;
import android.view.SurfaceControl;
import java.util.ArrayList;
import android.window.TaskOrganizer;

class SplitScreenTaskOrganizer extends TaskOrganizer
{
    final Divider mDivider;
    ArrayList<SurfaceControl> mHomeAndRecentsSurfaces;
    Rect mHomeBounds;
    ActivityManager$RunningTaskInfo mPrimary;
    SurfaceControl mPrimaryDim;
    SurfaceControl mPrimarySurface;
    ActivityManager$RunningTaskInfo mSecondary;
    SurfaceControl mSecondaryDim;
    SurfaceControl mSecondarySurface;
    private boolean mSplitScreenSupported;
    
    SplitScreenTaskOrganizer(final Divider mDivider) {
        this.mHomeAndRecentsSurfaces = new ArrayList<SurfaceControl>();
        this.mHomeBounds = new Rect();
        this.mSplitScreenSupported = false;
        this.mDivider = mDivider;
    }
    
    private void handleTaskInfoChanged(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        final int topActivityType = this.mSecondary.topActivityType;
        final boolean b = topActivityType == 2 || topActivityType == 3;
        final boolean b2 = this.mPrimary.topActivityType == 0;
        final boolean b3 = this.mSecondary.topActivityType == 0;
        if (activityManager$RunningTaskInfo.token.asBinder() == this.mPrimary.token.asBinder()) {
            this.mPrimary = activityManager$RunningTaskInfo;
        }
        else if (activityManager$RunningTaskInfo.token.asBinder() == this.mSecondary.token.asBinder()) {
            this.mSecondary = activityManager$RunningTaskInfo;
        }
        final boolean b4 = this.mPrimary.topActivityType == 0;
        final boolean b5 = this.mSecondary.topActivityType == 0;
        final int topActivityType2 = this.mSecondary.topActivityType;
        final boolean b6 = topActivityType2 == 2 || topActivityType2 == 3;
        if (b4 == b2 && b3 == b5 && b == b6) {
            return;
        }
        if (!b4 && !b5) {
            if (b6) {
                this.mDivider.ensureMinimizedSplit();
            }
            else {
                this.mDivider.ensureNormalSplit();
            }
        }
        else if (this.mDivider.inSplitMode()) {
            WindowManagerProxy.applyDismissSplit(this, true);
            this.mDivider.updateVisibility(false);
        }
        else if (!b4 && b2 && b3) {
            this.mDivider.startEnterSplit();
        }
    }
    
    SurfaceControl$Transaction getTransaction() {
        return this.mDivider.mTransactionPool.acquire();
    }
    
    void init(final SurfaceSession surfaceSession) throws RemoteException {
        this.registerOrganizer(3);
        this.registerOrganizer(4);
        try {
            this.mPrimary = TaskOrganizer.createRootTask(0, 3);
            this.mSecondary = TaskOrganizer.createRootTask(0, 4);
            this.mPrimarySurface = this.mPrimary.token.getLeash();
            this.mSecondarySurface = this.mSecondary.token.getLeash();
            this.mSplitScreenSupported = true;
            this.mPrimaryDim = new SurfaceControl$Builder(surfaceSession).setParent(this.mPrimarySurface).setColorLayer().setName("Primary Divider Dim").build();
            this.mSecondaryDim = new SurfaceControl$Builder(surfaceSession).setParent(this.mSecondarySurface).setColorLayer().setName("Secondary Divider Dim").build();
            final SurfaceControl$Transaction transaction = this.getTransaction();
            transaction.setLayer(this.mPrimaryDim, Integer.MAX_VALUE);
            transaction.setColor(this.mPrimaryDim, new float[] { 0.0f, 0.0f, 0.0f });
            transaction.setLayer(this.mSecondaryDim, Integer.MAX_VALUE);
            transaction.setColor(this.mSecondaryDim, new float[] { 0.0f, 0.0f, 0.0f });
            transaction.apply();
            this.releaseTransaction(transaction);
        }
        catch (Exception ex) {
            this.unregisterOrganizer();
            throw ex;
        }
    }
    
    boolean isSplitScreenSupported() {
        return this.mSplitScreenSupported;
    }
    
    public void onBackPressedOnTaskRoot(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
    }
    
    public void onTaskInfoChanged(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        if (activityManager$RunningTaskInfo.displayId != 0) {
            return;
        }
        this.mDivider.getHandler().post((Runnable)new _$$Lambda$SplitScreenTaskOrganizer$VFKjLFziXUrC1SQQoEI4rRScXR8(this, activityManager$RunningTaskInfo));
    }
    
    void releaseTransaction(final SurfaceControl$Transaction surfaceControl$Transaction) {
        this.mDivider.mTransactionPool.release(surfaceControl$Transaction);
    }
}
