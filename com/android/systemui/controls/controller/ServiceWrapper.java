// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import java.util.List;
import android.service.controls.IControlsSubscriber;
import android.service.controls.IControlsSubscription;
import android.util.Log;
import android.service.controls.actions.ControlActionWrapper;
import android.service.controls.IControlsActionCallback;
import android.service.controls.actions.ControlAction;
import kotlin.jvm.internal.Intrinsics;
import android.service.controls.IControlsProvider;

public final class ServiceWrapper
{
    private final IControlsProvider service;
    
    public ServiceWrapper(final IControlsProvider service) {
        Intrinsics.checkParameterIsNotNull(service, "service");
        this.service = service;
    }
    
    public final boolean action(final String s, final ControlAction controlAction, final IControlsActionCallback controlsActionCallback) {
        Intrinsics.checkParameterIsNotNull(s, "controlId");
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        Intrinsics.checkParameterIsNotNull(controlsActionCallback, "cb");
        boolean b;
        try {
            this.service.action(s, new ControlActionWrapper(controlAction), controlsActionCallback);
            b = true;
        }
        catch (Exception ex) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", (Throwable)ex);
            b = false;
        }
        return b;
    }
    
    public final boolean cancel(final IControlsSubscription controlsSubscription) {
        Intrinsics.checkParameterIsNotNull(controlsSubscription, "subscription");
        boolean b;
        try {
            controlsSubscription.cancel();
            b = true;
        }
        catch (Exception ex) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", (Throwable)ex);
            b = false;
        }
        return b;
    }
    
    public final boolean load(final IControlsSubscriber controlsSubscriber) {
        Intrinsics.checkParameterIsNotNull(controlsSubscriber, "subscriber");
        boolean b;
        try {
            this.service.load(controlsSubscriber);
            b = true;
        }
        catch (Exception ex) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", (Throwable)ex);
            b = false;
        }
        return b;
    }
    
    public final boolean loadSuggested(final IControlsSubscriber controlsSubscriber) {
        Intrinsics.checkParameterIsNotNull(controlsSubscriber, "subscriber");
        boolean b;
        try {
            this.service.loadSuggested(controlsSubscriber);
            b = true;
        }
        catch (Exception ex) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", (Throwable)ex);
            b = false;
        }
        return b;
    }
    
    public final boolean request(final IControlsSubscription controlsSubscription, final long n) {
        Intrinsics.checkParameterIsNotNull(controlsSubscription, "subscription");
        boolean b;
        try {
            controlsSubscription.request(n);
            b = true;
        }
        catch (Exception ex) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", (Throwable)ex);
            b = false;
        }
        return b;
    }
    
    public final boolean subscribe(final List<String> list, final IControlsSubscriber controlsSubscriber) {
        Intrinsics.checkParameterIsNotNull(list, "controlIds");
        Intrinsics.checkParameterIsNotNull(controlsSubscriber, "subscriber");
        boolean b;
        try {
            this.service.subscribe((List)list, controlsSubscriber);
            b = true;
        }
        catch (Exception ex) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", (Throwable)ex);
            b = false;
        }
        return b;
    }
}
