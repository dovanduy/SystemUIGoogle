// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.wm;

import android.window.WindowContainerTransaction;
import android.os.RemoteException;
import android.view.IDisplayWindowRotationCallback;
import android.view.IDisplayWindowRotationController$Stub;
import android.view.IWindowManager;
import java.util.ArrayList;
import android.os.Handler;
import android.view.IDisplayWindowRotationController;

public class DisplayChangeController
{
    private final IDisplayWindowRotationController mDisplayRotationController;
    private final Handler mHandler;
    private final ArrayList<OnDisplayChangingListener> mRotationListener;
    private final ArrayList<OnDisplayChangingListener> mTmpListeners;
    private final IWindowManager mWmService;
    
    public DisplayChangeController(final Handler mHandler, final IWindowManager mWmService) {
        this.mRotationListener = new ArrayList<OnDisplayChangingListener>();
        this.mTmpListeners = new ArrayList<OnDisplayChangingListener>();
        final IDisplayWindowRotationController$Stub displayWindowRotationController$Stub = new IDisplayWindowRotationController$Stub() {
            public void onRotateDisplay(final int n, final int n2, final int n3, final IDisplayWindowRotationCallback displayWindowRotationCallback) {
                DisplayChangeController.this.mHandler.post((Runnable)new _$$Lambda$DisplayChangeController$1$cr2NyoFjnt2r0DMHwy9cOe5oGO4(this, n, n2, n3, displayWindowRotationCallback));
            }
        };
        this.mDisplayRotationController = (IDisplayWindowRotationController)displayWindowRotationController$Stub;
        this.mHandler = mHandler;
        this.mWmService = mWmService;
        try {
            mWmService.setDisplayWindowRotationController((IDisplayWindowRotationController)displayWindowRotationController$Stub);
        }
        catch (RemoteException ex) {
            throw new RuntimeException("Unable to register rotation controller");
        }
    }
    
    public void addRotationListener(final OnDisplayChangingListener e) {
        synchronized (this.mRotationListener) {
            this.mRotationListener.add(e);
        }
    }
    
    public interface OnDisplayChangingListener
    {
        void onRotateDisplay(final int p0, final int p1, final int p2, final WindowContainerTransaction p3);
    }
}
