// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.MotionEvent;
import android.os.RemoteException;
import android.util.Log;
import android.content.Context;
import android.view.IWindowManager;
import com.android.systemui.statusbar.AutoHideUiElement;
import android.os.Handler;

public class AutoHideController
{
    private final Runnable mAutoHide;
    private boolean mAutoHideSuspended;
    private int mDisplayId;
    private final Handler mHandler;
    private AutoHideUiElement mNavigationBar;
    private AutoHideUiElement mStatusBar;
    private final IWindowManager mWindowManagerService;
    
    public AutoHideController(final Context context, final Handler mHandler, final IWindowManager mWindowManagerService) {
        this.mAutoHide = new _$$Lambda$AutoHideController$sJYAhc6qJ_sO_ZdtlpSd2BPK504(this);
        this.mHandler = mHandler;
        this.mWindowManagerService = mWindowManagerService;
        this.mDisplayId = context.getDisplayId();
    }
    
    private void cancelAutoHide() {
        this.mAutoHideSuspended = false;
        this.mHandler.removeCallbacks(this.mAutoHide);
    }
    
    private Runnable getCheckBarModesRunnable() {
        if (this.mStatusBar != null) {
            return new _$$Lambda$AutoHideController$Dw54NegELGCFcbvVgChoOa9gkLA(this);
        }
        if (this.mNavigationBar != null) {
            return new _$$Lambda$AutoHideController$FON87SM6b4__2jIBTAjBTcUbKIM(this);
        }
        return null;
    }
    
    private void hideTransientBars() {
        try {
            this.mWindowManagerService.hideTransientBars(this.mDisplayId);
        }
        catch (RemoteException ex) {
            Log.w("AutoHideController", "Cannot get WindowManager");
        }
        final AutoHideUiElement mStatusBar = this.mStatusBar;
        if (mStatusBar != null) {
            mStatusBar.hide();
        }
        final AutoHideUiElement mNavigationBar = this.mNavigationBar;
        if (mNavigationBar != null) {
            mNavigationBar.hide();
        }
    }
    
    private boolean isAnyTransientBarShown() {
        final AutoHideUiElement mStatusBar = this.mStatusBar;
        if (mStatusBar != null && mStatusBar.isVisible()) {
            return true;
        }
        final AutoHideUiElement mNavigationBar = this.mNavigationBar;
        return mNavigationBar != null && mNavigationBar.isVisible();
    }
    
    private void scheduleAutoHide() {
        this.cancelAutoHide();
        this.mHandler.postDelayed(this.mAutoHide, 2250L);
    }
    
    private void userAutoHide() {
        this.cancelAutoHide();
        this.mHandler.postDelayed(this.mAutoHide, 350L);
    }
    
    void checkUserAutoHide(final MotionEvent motionEvent) {
        final boolean b = this.isAnyTransientBarShown() && motionEvent.getAction() == 4 && motionEvent.getX() == 0.0f && motionEvent.getY() == 0.0f;
        final AutoHideUiElement mStatusBar = this.mStatusBar;
        boolean b2 = b;
        if (mStatusBar != null) {
            b2 = (b & mStatusBar.shouldHideOnTouch());
        }
        final AutoHideUiElement mNavigationBar = this.mNavigationBar;
        int n = b2 ? 1 : 0;
        if (mNavigationBar != null) {
            n = ((b2 & mNavigationBar.shouldHideOnTouch()) ? 1 : 0);
        }
        if (n != 0) {
            this.userAutoHide();
        }
    }
    
    void resumeSuspendedAutoHide() {
        if (this.mAutoHideSuspended) {
            this.scheduleAutoHide();
            final Runnable checkBarModesRunnable = this.getCheckBarModesRunnable();
            if (checkBarModesRunnable != null) {
                this.mHandler.postDelayed(checkBarModesRunnable, 500L);
            }
        }
    }
    
    public void setNavigationBar(final AutoHideUiElement mNavigationBar) {
        this.mNavigationBar = mNavigationBar;
    }
    
    public void setStatusBar(final AutoHideUiElement mStatusBar) {
        this.mStatusBar = mStatusBar;
    }
    
    void suspendAutoHide() {
        this.mHandler.removeCallbacks(this.mAutoHide);
        final Runnable checkBarModesRunnable = this.getCheckBarModesRunnable();
        if (checkBarModesRunnable != null) {
            this.mHandler.removeCallbacks(checkBarModesRunnable);
        }
        this.mAutoHideSuspended = this.isAnyTransientBarShown();
    }
    
    public void touchAutoHide() {
        if (this.isAnyTransientBarShown()) {
            this.scheduleAutoHide();
        }
        else {
            this.cancelAutoHide();
        }
    }
}
