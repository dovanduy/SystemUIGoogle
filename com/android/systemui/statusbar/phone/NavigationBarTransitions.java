// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Rect;
import com.android.systemui.util.Utils;
import java.util.Iterator;
import android.util.SparseArray;
import com.android.systemui.R$id;
import android.view.View$OnLayoutChangeListener;
import com.android.systemui.Dependency;
import android.view.IWindowManager;
import java.util.ArrayList;
import com.android.systemui.R$bool;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.view.IWallpaperVisibilityListener$Stub;
import com.android.systemui.R$drawable;
import com.android.systemui.statusbar.CommandQueue;
import android.view.IWallpaperVisibilityListener;
import android.view.View;
import android.os.Handler;
import java.util.List;

public final class NavigationBarTransitions extends BarTransitions implements DarkIntensityApplier
{
    private final boolean mAllowAutoDimWallpaperNotVisible;
    private boolean mAutoDim;
    private List<DarkIntensityListener> mDarkIntensityListeners;
    private final Handler mHandler;
    private final LightBarTransitionsController mLightTransitionsController;
    private boolean mLightsOut;
    private int mNavBarMode;
    private View mNavButtons;
    private final NavigationBarView mView;
    private final IWallpaperVisibilityListener mWallpaperVisibilityListener;
    private boolean mWallpaperVisible;
    
    public NavigationBarTransitions(final NavigationBarView mView, final CommandQueue commandQueue) {
        super((View)mView, R$drawable.nav_background);
        this.mNavBarMode = 0;
        this.mHandler = Handler.getMain();
        this.mWallpaperVisibilityListener = (IWallpaperVisibilityListener)new IWallpaperVisibilityListener$Stub() {
            public void onWallpaperVisibilityChanged(final boolean b, final int n) throws RemoteException {
                NavigationBarTransitions.this.mWallpaperVisible = b;
                NavigationBarTransitions.this.mHandler.post((Runnable)new _$$Lambda$NavigationBarTransitions$1$5foY_Yygo1gW25_mVBRpPSQRb_g(this));
            }
        };
        this.mView = mView;
        IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mLightTransitionsController = new LightBarTransitionsController(mView.getContext(), (LightBarTransitionsController.DarkIntensityApplier)this, commandQueue);
        this.mAllowAutoDimWallpaperNotVisible = mView.getContext().getResources().getBoolean(R$bool.config_navigation_bar_enable_auto_dim_no_visible_wallpaper);
        this.mDarkIntensityListeners = new ArrayList<DarkIntensityListener>();
        final IWindowManager windowManager = Dependency.get(IWindowManager.class);
        while (true) {
            try {
                this.mWallpaperVisible = windowManager.registerWallpaperVisibilityListener(this.mWallpaperVisibilityListener, 0);
                this.mView.addOnLayoutChangeListener((View$OnLayoutChangeListener)new _$$Lambda$NavigationBarTransitions$XJcD0ZRW4UO2juvu7uZcSTj_ILk(this));
                final View currentView = this.mView.getCurrentView();
                if (currentView != null) {
                    this.mNavButtons = currentView.findViewById(R$id.nav_buttons);
                }
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
    
    private void applyLightsOut(final boolean b, final boolean b2) {
        this.applyLightsOut(this.isLightsOut(this.getMode()), b, b2);
    }
    
    private void applyLightsOut(final boolean mLightsOut, final boolean b, final boolean b2) {
        if (!b2 && mLightsOut == this.mLightsOut) {
            return;
        }
        this.mLightsOut = mLightsOut;
        final View mNavButtons = this.mNavButtons;
        if (mNavButtons == null) {
            return;
        }
        mNavButtons.animate().cancel();
        final float n = this.mLightTransitionsController.getCurrentDarkIntensity() / 10.0f;
        float alpha;
        if (mLightsOut) {
            alpha = n + 0.6f;
        }
        else {
            alpha = 1.0f;
        }
        if (!b) {
            this.mNavButtons.setAlpha(alpha);
        }
        else {
            int n2;
            if (mLightsOut) {
                n2 = 1500;
            }
            else {
                n2 = 250;
            }
            this.mNavButtons.animate().alpha(alpha).setDuration((long)n2).start();
        }
    }
    
    public float addDarkIntensityListener(final DarkIntensityListener darkIntensityListener) {
        this.mDarkIntensityListeners.add(darkIntensityListener);
        return this.mLightTransitionsController.getCurrentDarkIntensity();
    }
    
    @Override
    public void applyDarkIntensity(final float n) {
        final SparseArray<ButtonDispatcher> buttonDispatchers = this.mView.getButtonDispatchers();
        for (int i = buttonDispatchers.size() - 1; i >= 0; --i) {
            ((ButtonDispatcher)buttonDispatchers.valueAt(i)).setDarkIntensity(n);
        }
        this.mView.getRotationButtonController().setDarkIntensity(n);
        final Iterator<DarkIntensityListener> iterator = this.mDarkIntensityListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDarkIntensity(n);
        }
        if (this.mAutoDim) {
            this.applyLightsOut(false, true);
        }
    }
    
    public void destroy() {
        final IWindowManager windowManager = Dependency.get(IWindowManager.class);
        try {
            windowManager.unregisterWallpaperVisibilityListener(this.mWallpaperVisibilityListener, 0);
        }
        catch (RemoteException ex) {}
    }
    
    public LightBarTransitionsController getLightTransitionsController() {
        return this.mLightTransitionsController;
    }
    
    @Override
    public int getTintAnimationDuration() {
        if (Utils.isGesturalModeOnDefaultDisplay(this.mView.getContext(), this.mNavBarMode)) {
            return Math.max(1700, 400);
        }
        return 120;
    }
    
    public void init() {
        this.applyModeBackground(-1, this.getMode(), false);
        this.applyLightsOut(false, true);
    }
    
    @Override
    protected boolean isLightsOut(final int n) {
        return super.isLightsOut(n) || (this.mAllowAutoDimWallpaperNotVisible && this.mAutoDim && !this.mWallpaperVisible && n != 5);
    }
    
    public void onNavigationModeChanged(final int mNavBarMode) {
        this.mNavBarMode = mNavBarMode;
    }
    
    @Override
    protected void onTransition(final int n, final int n2, final boolean b) {
        super.onTransition(n, n2, b);
        this.applyLightsOut(b, false);
        this.mView.onBarTransition(n2);
    }
    
    public void reapplyDarkIntensity() {
        this.applyDarkIntensity(this.mLightTransitionsController.getCurrentDarkIntensity());
    }
    
    public void removeDarkIntensityListener(final DarkIntensityListener darkIntensityListener) {
        this.mDarkIntensityListeners.remove(darkIntensityListener);
    }
    
    public void setAutoDim(final boolean mAutoDim) {
        if (mAutoDim && Utils.isGesturalModeOnDefaultDisplay(this.mView.getContext(), this.mNavBarMode)) {
            return;
        }
        if (this.mAutoDim == mAutoDim) {
            return;
        }
        this.mAutoDim = mAutoDim;
        this.applyLightsOut(true, false);
    }
    
    void setBackgroundFrame(final Rect frame) {
        super.mBarBackground.setFrame(frame);
    }
    
    public interface DarkIntensityListener
    {
        void onDarkIntensity(final float p0);
    }
}
