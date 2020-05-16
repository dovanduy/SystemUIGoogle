// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wm;

import android.os.IBinder;
import android.view.WindowlessWindowManager;
import android.view.IWindow;
import android.view.DisplayCutout$ParcelableWrapper;
import android.util.MergedConfiguration;
import android.graphics.Rect;
import com.android.internal.os.IResultReceiver;
import android.graphics.Point;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.os.ParcelFileDescriptor;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.IWindow$Stub;
import android.view.ViewGroup$LayoutParams;
import android.view.SurfaceControl;
import android.view.WindowManager$LayoutParams;
import android.os.RemoteException;
import android.util.Slog;
import android.view.IWindowSessionCallback;
import android.view.IWindowSessionCallback$Stub;
import android.content.res.Configuration;
import android.view.IWindowManager;
import android.view.SurfaceControlViewHost;
import android.view.View;
import java.util.HashMap;
import android.util.SparseArray;
import android.content.Context;

public class SystemWindows
{
    Context mContext;
    DisplayController mDisplayController;
    private final DisplayController.OnDisplaysChangedListener mDisplayListener;
    private final SparseArray<PerDisplay> mPerDisplay;
    final HashMap<View, SurfaceControlViewHost> mViewRoots;
    IWindowManager mWmService;
    
    public SystemWindows(final Context mContext, final DisplayController mDisplayController, final IWindowManager mWmService) {
        this.mPerDisplay = (SparseArray<PerDisplay>)new SparseArray();
        this.mViewRoots = new HashMap<View, SurfaceControlViewHost>();
        final DisplayController.OnDisplaysChangedListener mDisplayListener = new DisplayController.OnDisplaysChangedListener() {
            @Override
            public void onDisplayAdded(final int n) {
            }
            
            @Override
            public void onDisplayConfigurationChanged(final int n, final Configuration configuration) {
                final PerDisplay perDisplay = (PerDisplay)SystemWindows.this.mPerDisplay.get(n);
                if (perDisplay == null) {
                    return;
                }
                perDisplay.updateConfiguration(configuration);
            }
            
            @Override
            public void onDisplayRemoved(final int n) {
            }
        };
        this.mDisplayListener = mDisplayListener;
        this.mContext = mContext;
        this.mWmService = mWmService;
        (this.mDisplayController = mDisplayController).addDisplayWindowListener((DisplayController.OnDisplaysChangedListener)mDisplayListener);
        try {
            mWmService.openSession((IWindowSessionCallback)new IWindowSessionCallback$Stub(this) {
                public void onAnimatorScaleChanged(final float n) {
                }
            });
        }
        catch (RemoteException ex) {
            Slog.e("SystemWindows", "Unable to create layer", (Throwable)ex);
        }
    }
    
    public void addView(final View view, final WindowManager$LayoutParams windowManager$LayoutParams, final int n, final int n2) {
        PerDisplay perDisplay;
        if ((perDisplay = (PerDisplay)this.mPerDisplay.get(n)) == null) {
            perDisplay = new PerDisplay(n);
            this.mPerDisplay.put(n, (Object)perDisplay);
        }
        perDisplay.addView(view, windowManager$LayoutParams, n2);
    }
    
    public SurfaceControl getViewSurface(final View view) {
        for (int i = 0; i < this.mPerDisplay.size(); ++i) {
            for (int j = 0; j < ((PerDisplay)this.mPerDisplay.valueAt(i)).mWwms.size(); ++j) {
                final SurfaceControl surfaceControlForWindow = ((SysUiWindowManager)((PerDisplay)this.mPerDisplay.valueAt(i)).mWwms.get(j)).getSurfaceControlForWindow(view);
                if (surfaceControlForWindow != null) {
                    return surfaceControlForWindow;
                }
            }
        }
        return null;
    }
    
    public void removeView(final View key) {
        this.mViewRoots.remove(key).die();
    }
    
    public void updateViewLayout(final View key, final ViewGroup$LayoutParams layoutParams) {
        final SurfaceControlViewHost surfaceControlViewHost = this.mViewRoots.get(key);
        if (surfaceControlViewHost != null) {
            if (layoutParams instanceof WindowManager$LayoutParams) {
                key.setLayoutParams(layoutParams);
                surfaceControlViewHost.relayout((WindowManager$LayoutParams)layoutParams);
            }
        }
    }
    
    class ContainerWindow extends IWindow$Stub
    {
        ContainerWindow(final SystemWindows systemWindows) {
        }
        
        public void closeSystemDialogs(final String s) {
        }
        
        public void dispatchAppVisibility(final boolean b) {
        }
        
        public void dispatchDragEvent(final DragEvent dragEvent) {
        }
        
        public void dispatchGetNewSurface() {
        }
        
        public void dispatchPointerCaptureChanged(final boolean b) {
        }
        
        public void dispatchSystemUiVisibilityChanged(final int n, final int n2, final int n3, final int n4) {
        }
        
        public void dispatchWallpaperCommand(final String s, final int n, final int n2, final int n3, final Bundle bundle, final boolean b) {
        }
        
        public void dispatchWallpaperOffsets(final float n, final float n2, final float n3, final float n4, final float n5, final boolean b) {
        }
        
        public void dispatchWindowShown() {
        }
        
        public void executeCommand(final String s, final String s2, final ParcelFileDescriptor parcelFileDescriptor) {
        }
        
        public void hideInsets(final int n, final boolean b) {
        }
        
        public void insetsChanged(final InsetsState insetsState) {
        }
        
        public void insetsControlChanged(final InsetsState insetsState, final InsetsSourceControl[] array) {
        }
        
        public void locationInParentDisplayChanged(final Point point) {
        }
        
        public void moved(final int n, final int n2) {
        }
        
        public void requestAppKeyboardShortcuts(final IResultReceiver resultReceiver, final int n) {
        }
        
        public void resized(final Rect rect, final Rect rect2, final Rect rect3, final Rect rect4, final boolean b, final MergedConfiguration mergedConfiguration, final Rect rect5, final boolean b2, final boolean b3, final int n, final DisplayCutout$ParcelableWrapper displayCutout$ParcelableWrapper) {
        }
        
        public void showInsets(final int n, final boolean b) {
        }
        
        public void updatePointerIcon(final float n, final float n2) {
        }
        
        public void windowFocusChanged(final boolean b, final boolean b2) {
        }
    }
    
    private class PerDisplay
    {
        final int mDisplayId;
        private final SparseArray<SysUiWindowManager> mWwms;
        
        PerDisplay(final int mDisplayId) {
            this.mWwms = (SparseArray<SysUiWindowManager>)new SparseArray();
            this.mDisplayId = mDisplayId;
        }
        
        SysUiWindowManager addRoot(final int n) {
            final SysUiWindowManager sysUiWindowManager = (SysUiWindowManager)this.mWwms.get(n);
            if (sysUiWindowManager != null) {
                return sysUiWindowManager;
            }
            final ContainerWindow containerWindow = new ContainerWindow();
            SurfaceControl addShellRoot;
            try {
                addShellRoot = SystemWindows.this.mWmService.addShellRoot(this.mDisplayId, (IWindow)containerWindow, n);
            }
            catch (RemoteException ex) {
                addShellRoot = null;
            }
            if (addShellRoot == null) {
                Slog.e("SystemWindows", "Unable to get root surfacecontrol for systemui");
                return null;
            }
            final SysUiWindowManager sysUiWindowManager2 = new SysUiWindowManager(this.mDisplayId, SystemWindows.this.mDisplayController.getDisplayContext(this.mDisplayId), addShellRoot, containerWindow);
            this.mWwms.put(n, (Object)sysUiWindowManager2);
            return sysUiWindowManager2;
        }
        
        public void addView(final View key, final WindowManager$LayoutParams windowManager$LayoutParams, final int n) {
            final SysUiWindowManager addRoot = this.addRoot(n);
            if (addRoot == null) {
                Slog.e("SystemWindows", "Unable to create systemui root");
                return;
            }
            final SurfaceControlViewHost value = new SurfaceControlViewHost(SystemWindows.this.mContext, SystemWindows.this.mDisplayController.getDisplay(this.mDisplayId), (WindowlessWindowManager)addRoot, true);
            windowManager$LayoutParams.flags |= 0x1000000;
            value.setView(key, windowManager$LayoutParams);
            SystemWindows.this.mViewRoots.put(key, value);
        }
        
        void updateConfiguration(final Configuration configuration) {
            for (int i = 0; i < this.mWwms.size(); ++i) {
                ((SysUiWindowManager)this.mWwms.valueAt(i)).updateConfiguration(configuration);
            }
        }
    }
    
    public class SysUiWindowManager extends WindowlessWindowManager
    {
        final int mDisplayId;
        
        public SysUiWindowManager(final int mDisplayId, final Context context, final SurfaceControl surfaceControl, final ContainerWindow containerWindow) {
            super(context.getResources().getConfiguration(), surfaceControl, (IBinder)null);
            this.mDisplayId = mDisplayId;
        }
        
        SurfaceControl getSurfaceControlForWindow(final View view) {
            return this.getSurfaceControl(view);
        }
        
        public int relayout(final IWindow window, int relayout, final WindowManager$LayoutParams windowManager$LayoutParams, final int n, final int n2, final int n3, final int n4, final long n5, final Rect rect, final Rect rect2, final Rect rect3, final Rect rect4, final Rect rect5, final DisplayCutout$ParcelableWrapper displayCutout$ParcelableWrapper, final MergedConfiguration mergedConfiguration, final SurfaceControl surfaceControl, final InsetsState insetsState, final InsetsSourceControl[] array, final Point point, final SurfaceControl surfaceControl2) {
            relayout = super.relayout(window, relayout, windowManager$LayoutParams, n, n2, n3, n4, n5, rect, rect2, rect3, rect4, rect5, displayCutout$ParcelableWrapper, mergedConfiguration, surfaceControl, insetsState, array, point, surfaceControl2);
            if (relayout != 0) {
                return relayout;
            }
            rect5.set(SystemWindows.this.mDisplayController.getDisplayLayout(this.mDisplayId).stableInsets());
            return 0;
        }
        
        void updateConfiguration(final Configuration configuration) {
            this.setConfiguration(configuration);
        }
    }
}
