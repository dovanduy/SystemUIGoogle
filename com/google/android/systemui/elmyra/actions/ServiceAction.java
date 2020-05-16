// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.actions;

import java.util.NoSuchElementException;
import com.google.android.systemui.elmyra.IElmyraServiceListener;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import android.os.DeadObjectException;
import android.os.RemoteException;
import java.util.Arrays;
import android.util.Log;
import android.content.ServiceConnection;
import android.content.ComponentName;
import com.google.android.systemui.elmyra.ElmyraServiceProxy;
import android.content.Intent;
import android.os.Binder;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import android.os.IBinder;
import com.google.android.systemui.elmyra.IElmyraServiceGestureListener;
import com.google.android.systemui.elmyra.IElmyraService;
import android.os.IBinder$DeathRecipient;

public abstract class ServiceAction extends Action implements IBinder$DeathRecipient
{
    private IElmyraService mElmyraService;
    private final ElmyraServiceConnection mElmyraServiceConnection;
    private IElmyraServiceGestureListener mElmyraServiceGestureListener;
    private final ElmyraServiceListener mElmyraServiceListener;
    private final IBinder mToken;
    
    public ServiceAction(final Context context, final List<FeedbackEffect> list) {
        super(context, list);
        this.mToken = (IBinder)new Binder();
        this.mElmyraServiceConnection = new ElmyraServiceConnection();
        this.mElmyraServiceListener = new ElmyraServiceListener();
        try {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName(this.getContext(), (Class)ElmyraServiceProxy.class));
            this.getContext().bindService(intent, (ServiceConnection)this.mElmyraServiceConnection, 1);
        }
        catch (SecurityException ex) {
            Log.e("Elmyra/ServiceAction", "Unable to bind to ElmyraServiceProxy", (Throwable)ex);
        }
    }
    
    public void binderDied() {
        Log.w("Elmyra/ServiceAction", "Binder died");
        this.mElmyraServiceGestureListener = null;
        this.notifyListener();
    }
    
    protected abstract boolean checkSupportedCaller();
    
    protected boolean checkSupportedCaller(final String s) {
        final String[] packagesForUid = this.getContext().getPackageManager().getPackagesForUid(Binder.getCallingUid());
        return packagesForUid != null && Arrays.asList(packagesForUid).contains(s);
    }
    
    @Override
    public boolean isAvailable() {
        return this.mElmyraServiceGestureListener != null;
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        if (this.mElmyraServiceGestureListener != null) {
            this.updateFeedbackEffects(n, n2);
            try {
                this.mElmyraServiceGestureListener.onGestureProgress(n, n2);
            }
            catch (RemoteException ex) {
                Log.e("Elmyra/ServiceAction", "Unable to send progress, setting listener to null", (Throwable)ex);
                this.mElmyraServiceGestureListener = null;
                this.notifyListener();
            }
            catch (DeadObjectException ex2) {
                Log.e("Elmyra/ServiceAction", "Listener crashed or closed without unregistering", (Throwable)ex2);
                this.mElmyraServiceGestureListener = null;
                this.notifyListener();
            }
        }
    }
    
    protected void onServiceConnected() {
    }
    
    protected void onServiceDisconnected() {
    }
    
    @Override
    public void onTrigger(final GestureSensor.DetectionProperties detectionProperties) {
        if (this.mElmyraServiceGestureListener != null) {
            this.triggerFeedbackEffects(detectionProperties);
            try {
                this.mElmyraServiceGestureListener.onGestureDetected();
            }
            catch (RemoteException ex) {
                Log.e("Elmyra/ServiceAction", "Unable to send onGestureDetected; removing listener", (Throwable)ex);
                this.mElmyraServiceGestureListener = null;
                this.notifyListener();
            }
            catch (DeadObjectException ex2) {
                Log.e("Elmyra/ServiceAction", "Listener crashed or closed without unregistering", (Throwable)ex2);
                this.mElmyraServiceGestureListener = null;
                this.notifyListener();
            }
        }
    }
    
    protected void triggerAction() {
    }
    
    private class ElmyraServiceConnection implements ServiceConnection
    {
        public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
            ServiceAction.this.mElmyraService = IElmyraService.Stub.asInterface(binder);
            try {
                ServiceAction.this.mElmyraService.registerServiceListener(ServiceAction.this.mToken, (IBinder)ServiceAction.this.mElmyraServiceListener);
            }
            catch (RemoteException ex) {
                Log.e("Elmyra/ServiceAction", "Error registering listener", (Throwable)ex);
            }
            ServiceAction.this.onServiceConnected();
        }
        
        public void onServiceDisconnected(final ComponentName componentName) {
            ServiceAction.this.mElmyraService = null;
            ServiceAction.this.onServiceDisconnected();
        }
    }
    
    private class ElmyraServiceListener extends Stub
    {
        public void setListener(final IBinder binder, final IBinder binder2) {
            if (!ServiceAction.this.checkSupportedCaller()) {
                return;
            }
            if (binder2 == null && ServiceAction.this.mElmyraServiceGestureListener == null) {
                return;
            }
            final IElmyraServiceGestureListener interface1 = IElmyraServiceGestureListener.Stub.asInterface(binder2);
            if (interface1 != ServiceAction.this.mElmyraServiceGestureListener) {
                ServiceAction.this.mElmyraServiceGestureListener = interface1;
                ServiceAction.this.notifyListener();
            }
            if (binder == null) {
                goto Label_0105;
            }
            Label_0080: {
                if (binder2 == null) {
                    break Label_0080;
                }
                try {
                    binder.linkToDeath((IBinder$DeathRecipient)ServiceAction.this, 0);
                    goto Label_0105;
                    binder.unlinkToDeath((IBinder$DeathRecipient)ServiceAction.this, 0);
                    goto Label_0105;
                }
                catch (RemoteException ex) {
                    Log.e("Elmyra/ServiceAction", "RemoteException during linkToDeath", (Throwable)ex);
                }
                catch (NoSuchElementException ex2) {
                    goto Label_0105;
                }
            }
        }
        
        public void triggerAction() {
            if (!ServiceAction.this.checkSupportedCaller()) {
                return;
            }
            ServiceAction.this.triggerAction();
        }
    }
}
