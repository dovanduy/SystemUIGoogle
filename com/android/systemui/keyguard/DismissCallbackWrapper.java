// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import android.os.RemoteException;
import android.util.Log;
import com.android.internal.policy.IKeyguardDismissCallback;

public class DismissCallbackWrapper
{
    private IKeyguardDismissCallback mCallback;
    
    public DismissCallbackWrapper(final IKeyguardDismissCallback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void notifyDismissCancelled() {
        try {
            this.mCallback.onDismissCancelled();
        }
        catch (RemoteException ex) {
            Log.i("DismissCallbackWrapper", "Failed to call callback", (Throwable)ex);
        }
    }
    
    public void notifyDismissError() {
        try {
            this.mCallback.onDismissError();
        }
        catch (RemoteException ex) {
            Log.i("DismissCallbackWrapper", "Failed to call callback", (Throwable)ex);
        }
    }
    
    public void notifyDismissSucceeded() {
        try {
            this.mCallback.onDismissSucceeded();
        }
        catch (RemoteException ex) {
            Log.i("DismissCallbackWrapper", "Failed to call callback", (Throwable)ex);
        }
    }
}
