// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.assist.AssistHandleViewController;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import com.android.systemui.fragments.FragmentHostManager;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.LightBarController;
import android.app.Fragment;
import android.view.Display;
import com.android.internal.statusbar.RegisterStatusBarResult;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import android.util.SparseArray;
import android.os.Handler;
import android.hardware.display.DisplayManager;
import android.content.Context;

public class NavigationBarController implements Callbacks
{
    private static final String TAG = "NavigationBarController";
    private final Context mContext;
    private final DisplayManager mDisplayManager;
    private final Handler mHandler;
    @VisibleForTesting
    SparseArray<NavigationBarFragment> mNavigationBars;
    
    public NavigationBarController(final Context mContext, final Handler mHandler, final CommandQueue commandQueue) {
        this.mNavigationBars = (SparseArray<NavigationBarFragment>)new SparseArray();
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mDisplayManager = (DisplayManager)mContext.getSystemService("display");
        commandQueue.addCallback((CommandQueue.Callbacks)this);
    }
    
    private void removeNavigationBar(final int n) {
        final NavigationBarFragment navigationBarFragment = (NavigationBarFragment)this.mNavigationBars.get(n);
        if (navigationBarFragment != null) {
            navigationBarFragment.setAutoHideController(null);
            final View rootView = navigationBarFragment.getView().getRootView();
            WindowManagerGlobal.getInstance().removeView(rootView, true);
            FragmentHostManager.removeAndDestroy(rootView);
            this.mNavigationBars.remove(n);
        }
    }
    
    public void checkNavBarModes(final int n) {
        final NavigationBarFragment navigationBarFragment = (NavigationBarFragment)this.mNavigationBars.get(n);
        if (navigationBarFragment != null) {
            navigationBarFragment.checkNavBarModes();
        }
    }
    
    @VisibleForTesting
    void createNavigationBar(final Display display, final RegisterStatusBarResult registerStatusBarResult) {
        if (display == null) {
            return;
        }
        final int displayId = display.getDisplayId();
        final boolean b = displayId == 0;
        final IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
        try {
            if (!windowManagerService.hasNavigationBar(displayId)) {
                return;
            }
            Context context;
            if (b) {
                context = this.mContext;
            }
            else {
                context = this.mContext.createDisplayContext(display);
            }
            NavigationBarFragment.create(context, new _$$Lambda$NavigationBarController$oyTONslWMHHQSXiga3Vs0njIek8(this, b, context, displayId, registerStatusBarResult, display));
        }
        catch (RemoteException ex) {
            Log.w(NavigationBarController.TAG, "Cannot get WindowManager.");
        }
    }
    
    public void createNavigationBars(final boolean b, final RegisterStatusBarResult registerStatusBarResult) {
        for (final Display display : this.mDisplayManager.getDisplays()) {
            if (b || display.getDisplayId() != 0) {
                this.createNavigationBar(display, registerStatusBarResult);
            }
        }
    }
    
    public void disableAnimationsDuringHide(final int n, final long n2) {
        final NavigationBarFragment navigationBarFragment = (NavigationBarFragment)this.mNavigationBars.get(n);
        if (navigationBarFragment != null) {
            navigationBarFragment.disableAnimationsDuringHide(n2);
        }
    }
    
    public void finishBarAnimations(final int n) {
        final NavigationBarFragment navigationBarFragment = (NavigationBarFragment)this.mNavigationBars.get(n);
        if (navigationBarFragment != null) {
            navigationBarFragment.finishBarAnimations();
        }
    }
    
    public AssistHandleViewController getAssistHandlerViewController() {
        final NavigationBarFragment defaultNavigationBarFragment = this.getDefaultNavigationBarFragment();
        AssistHandleViewController assistHandlerViewController;
        if (defaultNavigationBarFragment == null) {
            assistHandlerViewController = null;
        }
        else {
            assistHandlerViewController = defaultNavigationBarFragment.getAssistHandlerViewController();
        }
        return assistHandlerViewController;
    }
    
    public NavigationBarFragment getDefaultNavigationBarFragment() {
        return (NavigationBarFragment)this.mNavigationBars.get(0);
    }
    
    public NavigationBarView getDefaultNavigationBarView() {
        return this.getNavigationBarView(0);
    }
    
    public NavigationBarView getNavigationBarView(final int n) {
        final NavigationBarFragment navigationBarFragment = (NavigationBarFragment)this.mNavigationBars.get(n);
        NavigationBarView navigationBarView;
        if (navigationBarFragment == null) {
            navigationBarView = null;
        }
        else {
            navigationBarView = (NavigationBarView)navigationBarFragment.getView();
        }
        return navigationBarView;
    }
    
    @Override
    public void onDisplayReady(final int n) {
        this.createNavigationBar(this.mDisplayManager.getDisplay(n), null);
    }
    
    @Override
    public void onDisplayRemoved(final int n) {
        this.removeNavigationBar(n);
    }
    
    public void touchAutoDim(final int n) {
        final NavigationBarFragment navigationBarFragment = (NavigationBarFragment)this.mNavigationBars.get(n);
        if (navigationBarFragment != null) {
            navigationBarFragment.touchAutoDim();
        }
    }
    
    public void transitionTo(final int n, final int n2, final boolean b) {
        final NavigationBarFragment navigationBarFragment = (NavigationBarFragment)this.mNavigationBars.get(n);
        if (navigationBarFragment != null) {
            navigationBarFragment.transitionTo(n2, b);
        }
    }
}
