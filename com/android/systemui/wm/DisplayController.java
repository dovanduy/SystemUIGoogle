// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wm;

import android.hardware.display.DisplayManager;
import android.os.RemoteException;
import android.util.Slog;
import android.content.res.Configuration;
import android.view.Display;
import android.view.IDisplayWindowListener$Stub;
import android.view.IWindowManager;
import android.os.Handler;
import android.util.SparseArray;
import android.view.IDisplayWindowListener;
import java.util.ArrayList;
import android.content.Context;

public class DisplayController
{
    private final DisplayChangeController mChangeController;
    private final Context mContext;
    private final ArrayList<OnDisplaysChangedListener> mDisplayChangedListeners;
    private final IDisplayWindowListener mDisplayContainerListener;
    private final SparseArray<DisplayRecord> mDisplays;
    private final Handler mHandler;
    private final IWindowManager mWmService;
    
    public DisplayController(final Context mContext, final Handler mHandler, final IWindowManager mWmService) {
        this.mDisplays = (SparseArray<DisplayRecord>)new SparseArray();
        this.mDisplayChangedListeners = new ArrayList<OnDisplaysChangedListener>();
        this.mDisplayContainerListener = (IDisplayWindowListener)new IDisplayWindowListener$Stub() {
            public void onDisplayAdded(final int n) {
                DisplayController.this.mHandler.post((Runnable)new _$$Lambda$DisplayController$1$zJ2mVywyLG45RsLGtw9ST7xxypY(this, n));
            }
            
            public void onDisplayConfigurationChanged(final int n, final Configuration configuration) {
                DisplayController.this.mHandler.post((Runnable)new _$$Lambda$DisplayController$1$mO2SyO_pDmJKrsjv09X_0fk_FOg(this, n, configuration));
            }
            
            public void onDisplayRemoved(final int n) {
                DisplayController.this.mHandler.post((Runnable)new _$$Lambda$DisplayController$1$sHTeIz3WbujoajhpVNRgzuLoi74(this, n));
            }
        };
        this.mHandler = mHandler;
        this.mContext = mContext;
        this.mWmService = mWmService;
        this.mChangeController = new DisplayChangeController(mHandler, mWmService);
        try {
            this.mWmService.registerDisplayWindowListener(this.mDisplayContainerListener);
        }
        catch (RemoteException ex) {
            throw new RuntimeException("Unable to register hierarchy listener");
        }
    }
    
    public void addDisplayChangingController(final DisplayChangeController.OnDisplayChangingListener onDisplayChangingListener) {
        this.mChangeController.addRotationListener(onDisplayChangingListener);
    }
    
    public void addDisplayWindowListener(final OnDisplaysChangedListener onDisplaysChangedListener) {
        synchronized (this.mDisplays) {
            if (this.mDisplayChangedListeners.contains(onDisplaysChangedListener)) {
                return;
            }
            this.mDisplayChangedListeners.add(onDisplaysChangedListener);
            for (int i = 0; i < this.mDisplays.size(); ++i) {
                onDisplaysChangedListener.onDisplayAdded(this.mDisplays.keyAt(i));
            }
        }
    }
    
    public Display getDisplay(final int n) {
        return ((DisplayManager)this.mContext.getSystemService((Class)DisplayManager.class)).getDisplay(n);
    }
    
    public Context getDisplayContext(final int n) {
        final DisplayRecord displayRecord = (DisplayRecord)this.mDisplays.get(n);
        Context mContext;
        if (displayRecord != null) {
            mContext = displayRecord.mContext;
        }
        else {
            mContext = null;
        }
        return mContext;
    }
    
    public DisplayLayout getDisplayLayout(final int n) {
        final DisplayRecord displayRecord = (DisplayRecord)this.mDisplays.get(n);
        DisplayLayout mDisplayLayout;
        if (displayRecord != null) {
            mDisplayLayout = displayRecord.mDisplayLayout;
        }
        else {
            mDisplayLayout = null;
        }
        return mDisplayLayout;
    }
    
    private static class DisplayRecord
    {
        Context mContext;
        DisplayLayout mDisplayLayout;
    }
    
    public interface OnDisplaysChangedListener
    {
        default void onDisplayAdded(final int n) {
        }
        
        default void onDisplayConfigurationChanged(final int n, final Configuration configuration) {
        }
        
        default void onDisplayRemoved(final int n) {
        }
    }
}
