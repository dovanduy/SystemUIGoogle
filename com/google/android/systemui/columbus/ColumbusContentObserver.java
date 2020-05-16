// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import kotlin.jvm.internal.Intrinsics;
import android.os.RemoteException;
import android.util.Log;
import android.app.IUserSwitchObserver;
import android.os.Handler;
import android.os.Looper;
import kotlin.Unit;
import android.net.Uri;
import kotlin.jvm.functions.Function1;
import android.app.IActivityManager;
import android.database.ContentObserver;

public final class ColumbusContentObserver extends ContentObserver
{
    private final IActivityManager activityManagerService;
    private final Function1<Uri, Unit> callback;
    private final ContentResolverWrapper contentResolver;
    private final Uri settingsUri;
    private final ColumbusContentObserver$userSwitchCallback.ColumbusContentObserver$userSwitchCallback$1 userSwitchCallback;
    
    private ColumbusContentObserver(final ContentResolverWrapper contentResolver, final Uri settingsUri, final Function1<? super Uri, Unit> callback, final IActivityManager activityManagerService) {
        super(new Handler(Looper.getMainLooper()));
        this.contentResolver = contentResolver;
        this.settingsUri = settingsUri;
        this.callback = (Function1<Uri, Unit>)callback;
        this.activityManagerService = activityManagerService;
        this.userSwitchCallback = new ColumbusContentObserver$userSwitchCallback.ColumbusContentObserver$userSwitchCallback$1(this);
    }
    
    private final void updateContentObserver() {
        this.contentResolver.unregisterContentObserver(this);
        this.contentResolver.registerContentObserver(this.settingsUri, false, this, -2);
    }
    
    public final void activate() {
        this.updateContentObserver();
        try {
            this.activityManagerService.registerUserSwitchObserver((IUserSwitchObserver)this.userSwitchCallback, "Columbus/ColumbusContentObserver");
        }
        catch (RemoteException ex) {
            Log.e("Columbus/ColumbusContentObserver", "Failed to register user switch observer", (Throwable)ex);
        }
    }
    
    public final void deactivate() {
        this.contentResolver.unregisterContentObserver(this);
        try {
            this.activityManagerService.unregisterUserSwitchObserver((IUserSwitchObserver)this.userSwitchCallback);
        }
        catch (RemoteException ex) {
            Log.e("Columbus/ColumbusContentObserver", "Failed to unregister user switch observer", (Throwable)ex);
        }
    }
    
    public void onChange(final boolean b, final Uri uri) {
        Intrinsics.checkParameterIsNotNull(uri, "uri");
        this.callback.invoke(uri);
    }
    
    public static final class Factory
    {
        private final IActivityManager activityManagerService;
        private final ContentResolverWrapper contentResolver;
        
        public Factory(final ContentResolverWrapper contentResolver, final IActivityManager activityManagerService) {
            Intrinsics.checkParameterIsNotNull(contentResolver, "contentResolver");
            Intrinsics.checkParameterIsNotNull(activityManagerService, "activityManagerService");
            this.contentResolver = contentResolver;
            this.activityManagerService = activityManagerService;
        }
        
        public final ColumbusContentObserver create(final Uri uri, final Function1<? super Uri, Unit> function1) {
            Intrinsics.checkParameterIsNotNull(uri, "settingsUri");
            Intrinsics.checkParameterIsNotNull(function1, "callback");
            return new ColumbusContentObserver(this.contentResolver, uri, function1, this.activityManagerService, null);
        }
    }
}
