// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shortcut;

import android.os.Message;
import android.os.RemoteException;
import android.os.Handler;
import com.android.internal.policy.IShortcutService$Stub;

public class ShortcutKeyServiceProxy extends IShortcutService$Stub
{
    private Callbacks mCallbacks;
    private final Handler mHandler;
    private final Object mLock;
    
    public ShortcutKeyServiceProxy(final Callbacks mCallbacks) {
        this.mLock = new Object();
        this.mHandler = new H();
        this.mCallbacks = mCallbacks;
    }
    
    public void notifyShortcutKeyPressed(final long l) throws RemoteException {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1, (Object)l).sendToTarget();
        }
    }
    
    public interface Callbacks
    {
        void onShortcutKeyPressed(final long p0);
    }
    
    private final class H extends Handler
    {
        public void handleMessage(final Message message) {
            if (message.what == 1) {
                ShortcutKeyServiceProxy.this.mCallbacks.onShortcutKeyPressed((long)message.obj);
            }
        }
    }
}
