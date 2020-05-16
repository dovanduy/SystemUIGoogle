// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.view.SurfaceControl;
import android.graphics.Rect;
import android.util.Log;
import android.os.RemoteException;
import android.view.IPinnedStackListener;
import android.view.WindowManagerGlobal;

public class WindowManagerWrapper
{
    private static final WindowManagerWrapper sInstance;
    private PinnedStackListenerForwarder mPinnedStackListenerForwarder;
    
    static {
        sInstance = new WindowManagerWrapper();
    }
    
    public WindowManagerWrapper() {
        this.mPinnedStackListenerForwarder = new PinnedStackListenerForwarder();
    }
    
    public static WindowManagerWrapper getInstance() {
        return WindowManagerWrapper.sInstance;
    }
    
    public void addPinnedStackListener(final PinnedStackListenerForwarder.PinnedStackListener pinnedStackListener) throws RemoteException {
        this.mPinnedStackListenerForwarder.addListener(pinnedStackListener);
        WindowManagerGlobal.getWindowManagerService().registerPinnedStackListener(0, (IPinnedStackListener)this.mPinnedStackListenerForwarder);
    }
    
    public int getNavBarPosition(int navBarPosition) {
        try {
            navBarPosition = WindowManagerGlobal.getWindowManagerService().getNavBarPosition(navBarPosition);
            return navBarPosition;
        }
        catch (RemoteException ex) {
            Log.w("WindowManagerWrapper", "Failed to get nav bar position");
            return -1;
        }
    }
    
    public void getStableInsets(final Rect rect) {
        try {
            WindowManagerGlobal.getWindowManagerService().getStableInsets(0, rect);
        }
        catch (RemoteException ex) {
            Log.e("WindowManagerWrapper", "Failed to get stable insets", (Throwable)ex);
        }
    }
    
    public boolean hasSoftNavigationBar(final int n) {
        try {
            return WindowManagerGlobal.getWindowManagerService().hasNavigationBar(n);
        }
        catch (RemoteException ex) {
            return false;
        }
    }
    
    public SurfaceControl mirrorDisplay(final int n) {
        try {
            final SurfaceControl surfaceControl = new SurfaceControl();
            WindowManagerGlobal.getWindowManagerService().mirrorDisplay(n, surfaceControl);
            return surfaceControl;
        }
        catch (RemoteException ex) {
            Log.e("WindowManagerWrapper", "Unable to reach window manager", (Throwable)ex);
            return null;
        }
    }
    
    public void setNavBarVirtualKeyHapticFeedbackEnabled(final boolean navBarVirtualKeyHapticFeedbackEnabled) {
        try {
            WindowManagerGlobal.getWindowManagerService().setNavBarVirtualKeyHapticFeedbackEnabled(navBarVirtualKeyHapticFeedbackEnabled);
        }
        catch (RemoteException ex) {
            Log.w("WindowManagerWrapper", "Failed to enable or disable navigation bar button haptics: ", (Throwable)ex);
        }
    }
    
    public void setPipVisibility(final boolean pipVisibility) {
        try {
            WindowManagerGlobal.getWindowManagerService().setPipVisibility(pipVisibility);
        }
        catch (RemoteException ex) {
            Log.e("WindowManagerWrapper", "Unable to reach window manager", (Throwable)ex);
        }
    }
}
