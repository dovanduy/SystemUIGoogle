// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.os.IBinder;
import android.os.Binder;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import android.view.ViewGroup;
import android.content.res.Resources;
import android.view.WindowManager$LayoutParams;
import android.content.Context;

public class StatusBarWindowController
{
    private int mBarHeight;
    private final Context mContext;
    private final State mCurrentState;
    private WindowManager$LayoutParams mLp;
    private final WindowManager$LayoutParams mLpChanged;
    private final Resources mResources;
    private ViewGroup mStatusBarView;
    private final SuperStatusBarViewFactory mSuperStatusBarViewFactory;
    private final WindowManager mWindowManager;
    
    public StatusBarWindowController(final Context mContext, final WindowManager mWindowManager, final SuperStatusBarViewFactory mSuperStatusBarViewFactory, final Resources mResources) {
        this.mBarHeight = -1;
        this.mCurrentState = new State();
        this.mContext = mContext;
        this.mWindowManager = mWindowManager;
        this.mSuperStatusBarViewFactory = mSuperStatusBarViewFactory;
        this.mStatusBarView = (ViewGroup)mSuperStatusBarViewFactory.getStatusBarWindowView();
        this.mLpChanged = new WindowManager$LayoutParams();
        this.mResources = mResources;
        if (this.mBarHeight < 0) {
            this.mBarHeight = mResources.getDimensionPixelSize(17105471);
        }
    }
    
    private void apply(final State state) {
        this.applyForceStatusBarVisibleFlag(state);
        this.applyHeight();
        final WindowManager$LayoutParams mLp = this.mLp;
        if (mLp != null && mLp.copyFrom(this.mLpChanged) != 0) {
            this.mWindowManager.updateViewLayout((View)this.mStatusBarView, (ViewGroup$LayoutParams)this.mLp);
        }
    }
    
    private void applyForceStatusBarVisibleFlag(final State state) {
        if (state.mForceStatusBarVisible) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            mLpChanged.privateFlags |= 0x1000;
        }
        else {
            final WindowManager$LayoutParams mLpChanged2 = this.mLpChanged;
            mLpChanged2.privateFlags &= 0xFFFFEFFF;
        }
    }
    
    private void applyHeight() {
        this.mLpChanged.height = this.mBarHeight;
    }
    
    public void attach() {
        final WindowManager$LayoutParams mLp = new WindowManager$LayoutParams(-1, this.mBarHeight, 2000, -2139095032, -3);
        this.mLp = mLp;
        mLp.token = (IBinder)new Binder();
        final WindowManager$LayoutParams mLp2 = this.mLp;
        mLp2.gravity = 48;
        mLp2.setFitInsetsTypes(0);
        this.mLp.setTitle((CharSequence)"StatusBar");
        this.mLp.packageName = this.mContext.getPackageName();
        final WindowManager$LayoutParams mLp3 = this.mLp;
        mLp3.layoutInDisplayCutoutMode = 3;
        this.mWindowManager.addView((View)this.mStatusBarView, (ViewGroup$LayoutParams)mLp3);
        this.mLpChanged.copyFrom(this.mLp);
    }
    
    public int getStatusBarHeight() {
        return this.mBarHeight;
    }
    
    public void refreshStatusBarHeight() {
        final int dimensionPixelSize = this.mResources.getDimensionPixelSize(17105471);
        if (this.mBarHeight != dimensionPixelSize) {
            this.mBarHeight = dimensionPixelSize;
            this.apply(this.mCurrentState);
        }
    }
    
    public void setForceStatusBarVisible(final boolean mForceStatusBarVisible) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mForceStatusBarVisible = mForceStatusBarVisible;
        this.apply(mCurrentState);
    }
    
    private static class State
    {
        boolean mForceStatusBarVisible;
    }
}
