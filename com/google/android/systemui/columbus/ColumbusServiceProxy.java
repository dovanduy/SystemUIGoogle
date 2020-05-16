// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.os.RemoteException;
import android.util.Log;
import kotlin.Unit;
import android.os.IBinder$DeathRecipient;
import kotlin.jvm.internal.Intrinsics;
import android.os.IBinder;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import android.app.Service;

public final class ColumbusServiceProxy extends Service
{
    private final ColumbusServiceProxy$binder.ColumbusServiceProxy$binder$1 binder;
    private final List<ColumbusServiceListener> columbusServiceListeners;
    
    public ColumbusServiceProxy() {
        this.columbusServiceListeners = new ArrayList<ColumbusServiceListener>();
        this.binder = new ColumbusServiceProxy$binder.ColumbusServiceProxy$binder$1(this);
    }
    
    private final void checkPermission() {
        this.enforceCallingOrSelfPermission("com.google.android.columbus.permission.CONFIGURE_COLUMBUS_GESTURE", "Must have com.google.android.columbus.permission.CONFIGURE_COLUMBUS_GESTURE permission");
    }
    
    public IBinder onBind(final Intent intent) {
        return (IBinder)this.binder;
    }
    
    public int onStartCommand(final Intent intent, final int n, final int n2) {
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        return 0;
    }
    
    private static final class ColumbusServiceListener implements IBinder$DeathRecipient
    {
        private IColumbusServiceListener listener;
        private IBinder token;
        
        public ColumbusServiceListener(final IBinder token, final IColumbusServiceListener listener) {
            this.token = token;
            this.listener = listener;
            this.linkToDeath();
        }
        
        private final void linkToDeath() {
            final IBinder token = this.token;
            if (token != null) {
                try {
                    token.linkToDeath((IBinder$DeathRecipient)this, 0);
                    final Unit instance = Unit.INSTANCE;
                }
                catch (RemoteException ex) {
                    Log.e("Columbus/ColumbusServiceProxy", "Unable to linkToDeath", (Throwable)ex);
                }
            }
        }
        
        public void binderDied() {
            Log.w("Columbus/ColumbusServiceProxy", "ColumbusServiceListener binder died");
            this.token = null;
            this.listener = null;
        }
        
        public final IColumbusServiceListener getListener() {
            return this.listener;
        }
        
        public final IBinder getToken() {
            return this.token;
        }
        
        public final Boolean unlinkToDeath() {
            final IBinder token = this.token;
            Boolean value;
            if (token != null) {
                value = token.unlinkToDeath((IBinder$DeathRecipient)this, 0);
            }
            else {
                value = null;
            }
            return value;
        }
    }
}
