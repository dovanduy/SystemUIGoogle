// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import java.util.NoSuchElementException;
import com.google.android.systemui.columbus.IColumbusServiceListener;
import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import kotlin.collections.ArraysKt;
import android.util.Log;
import android.content.ServiceConnection;
import android.content.ComponentName;
import com.google.android.systemui.columbus.ColumbusServiceProxy;
import android.content.Intent;
import android.os.Binder;
import kotlin.jvm.internal.Intrinsics;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import android.os.IBinder;
import com.google.android.systemui.columbus.IColumbusServiceGestureListener;
import com.google.android.systemui.columbus.IColumbusService;
import android.os.IBinder$DeathRecipient;

public abstract class ServiceAction extends Action implements IBinder$DeathRecipient
{
    private IColumbusService columbusService;
    private final ColumbusServiceConnection columbusServiceConnection;
    private IColumbusServiceGestureListener columbusServiceGestureListener;
    private final ColumbusServiceListener columbusServiceListener;
    private final IBinder token;
    
    public ServiceAction(final Context context, final List<? extends FeedbackEffect> list) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, list);
        this.token = (IBinder)new Binder();
        this.columbusServiceConnection = new ColumbusServiceConnection();
        this.columbusServiceListener = new ColumbusServiceListener();
        try {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName(context, (Class)ColumbusServiceProxy.class));
            context.bindService(intent, (ServiceConnection)this.columbusServiceConnection, 1);
        }
        catch (SecurityException ex) {
            Log.e("Columbus/ServiceAction", "Unable to bind to ColumbusServiceProxy", (Throwable)ex);
        }
    }
    
    public static final /* synthetic */ IColumbusService access$getColumbusService$p(final ServiceAction serviceAction) {
        return serviceAction.columbusService;
    }
    
    public static final /* synthetic */ IColumbusServiceGestureListener access$getColumbusServiceGestureListener$p(final ServiceAction serviceAction) {
        return serviceAction.columbusServiceGestureListener;
    }
    
    public static final /* synthetic */ ColumbusServiceListener access$getColumbusServiceListener$p(final ServiceAction serviceAction) {
        return serviceAction.columbusServiceListener;
    }
    
    public static final /* synthetic */ IBinder access$getToken$p(final ServiceAction serviceAction) {
        return serviceAction.token;
    }
    
    public static final /* synthetic */ void access$setColumbusService$p(final ServiceAction serviceAction, final IColumbusService columbusService) {
        serviceAction.columbusService = columbusService;
    }
    
    public static final /* synthetic */ void access$setColumbusServiceGestureListener$p(final ServiceAction serviceAction, final IColumbusServiceGestureListener columbusServiceGestureListener) {
        serviceAction.columbusServiceGestureListener = columbusServiceGestureListener;
    }
    
    public void binderDied() {
        Log.w("Columbus/ServiceAction", "Binder died");
        this.columbusServiceGestureListener = null;
        this.notifyListener();
    }
    
    protected abstract boolean checkSupportedCaller();
    
    protected final boolean checkSupportedCaller(final String s) {
        Intrinsics.checkParameterIsNotNull(s, "packageName");
        final String[] packagesForUid = this.getContext().getPackageManager().getPackagesForUid(Binder.getCallingUid());
        return packagesForUid != null && ArraysKt.contains(packagesForUid, s);
    }
    
    @Override
    public boolean isAvailable() {
        return this.columbusServiceGestureListener != null;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (this.columbusServiceGestureListener != null) {
            this.updateFeedbackEffects(n, detectionProperties);
            try {
                final IColumbusServiceGestureListener columbusServiceGestureListener = this.columbusServiceGestureListener;
                if (columbusServiceGestureListener != null) {
                    columbusServiceGestureListener.onGestureProgress(n);
                }
            }
            catch (RemoteException ex) {
                Log.e("Columbus/ServiceAction", "Unable to send progress, setting listener to null", (Throwable)ex);
                this.columbusServiceGestureListener = null;
                this.notifyListener();
            }
            catch (DeadObjectException ex2) {
                Log.e("Columbus/ServiceAction", "Listener crashed or closed without unregistering", (Throwable)ex2);
                this.columbusServiceGestureListener = null;
                this.notifyListener();
            }
        }
    }
    
    protected void onServiceConnected() {
    }
    
    protected void onServiceDisconnected() {
    }
    
    protected abstract void triggerAction();
    
    private final class ColumbusServiceConnection implements ServiceConnection
    {
        public ColumbusServiceConnection() {
        }
        
        public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
            ServiceAction.access$setColumbusService$p(ServiceAction.this, IColumbusService.Stub.asInterface(binder));
            try {
                final IColumbusService access$getColumbusService$p = ServiceAction.access$getColumbusService$p(ServiceAction.this);
                if (access$getColumbusService$p != null) {
                    access$getColumbusService$p.registerGestureListener(ServiceAction.access$getToken$p(ServiceAction.this), (IBinder)ServiceAction.access$getColumbusServiceListener$p(ServiceAction.this));
                }
            }
            catch (RemoteException ex) {
                Log.e("Columbus/ServiceAction", "Error registering listener", (Throwable)ex);
            }
            ServiceAction.this.onServiceConnected();
        }
        
        public void onServiceDisconnected(final ComponentName componentName) {
            ServiceAction.access$setColumbusService$p(ServiceAction.this, null);
            ServiceAction.this.onServiceDisconnected();
        }
    }
    
    private final class ColumbusServiceListener extends Stub
    {
        public ColumbusServiceListener() {
        }
        
        public void setListener(final IBinder binder, final IBinder binder2) {
            if (!ServiceAction.this.checkSupportedCaller()) {
                return;
            }
            if (binder2 == null && ServiceAction.access$getColumbusServiceGestureListener$p(ServiceAction.this) == null) {
                return;
            }
            final IColumbusServiceGestureListener interface1 = IColumbusServiceGestureListener.Stub.asInterface(binder2);
            if (Intrinsics.areEqual(interface1, ServiceAction.access$getColumbusServiceGestureListener$p(ServiceAction.this)) ^ true) {
                ServiceAction.access$setColumbusServiceGestureListener$p(ServiceAction.this, interface1);
                ServiceAction.this.notifyListener();
            }
            if (binder != null) {
                Label_0084: {
                    if (binder2 == null) {
                        break Label_0084;
                    }
                    try {
                        binder.linkToDeath((IBinder$DeathRecipient)ServiceAction.this, 0);
                        return;
                        binder.unlinkToDeath((IBinder$DeathRecipient)ServiceAction.this, 0);
                    }
                    catch (NoSuchElementException ex) {
                        Log.e("Columbus/ServiceAction", "NoSuchElementException during linkToDeath", (Throwable)ex);
                    }
                    catch (RemoteException ex2) {
                        Log.e("Columbus/ServiceAction", "RemoteException during linkToDeath", (Throwable)ex2);
                    }
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
