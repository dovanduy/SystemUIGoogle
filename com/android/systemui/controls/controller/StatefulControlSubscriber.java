// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import android.service.controls.Control;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import android.os.IBinder;
import kotlin.jvm.internal.Intrinsics;
import android.service.controls.IControlsSubscription;
import com.android.systemui.util.concurrency.DelayableExecutor;
import android.service.controls.IControlsSubscriber$Stub;

public final class StatefulControlSubscriber extends IControlsSubscriber$Stub
{
    private final DelayableExecutor bgExecutor;
    private final ControlsController controller;
    private final ControlsProviderLifecycleManager provider;
    private final long requestLimit;
    private IControlsSubscription subscription;
    private boolean subscriptionOpen;
    
    public StatefulControlSubscriber(final ControlsController controller, final ControlsProviderLifecycleManager provider, final DelayableExecutor bgExecutor, final long requestLimit) {
        Intrinsics.checkParameterIsNotNull(controller, "controller");
        Intrinsics.checkParameterIsNotNull(provider, "provider");
        Intrinsics.checkParameterIsNotNull(bgExecutor, "bgExecutor");
        this.controller = controller;
        this.provider = provider;
        this.bgExecutor = bgExecutor;
        this.requestLimit = requestLimit;
    }
    
    private final void run(final IBinder binder, final Function0<Unit> function0) {
        if (Intrinsics.areEqual(this.provider.getToken(), binder)) {
            this.bgExecutor.execute((Runnable)new StatefulControlSubscriber$run.StatefulControlSubscriber$run$1((Function0)function0));
        }
    }
    
    public final void cancel() {
        if (!this.subscriptionOpen) {
            return;
        }
        this.bgExecutor.execute((Runnable)new StatefulControlSubscriber$cancel.StatefulControlSubscriber$cancel$1(this));
    }
    
    public void onComplete(final IBinder binder) {
        Intrinsics.checkParameterIsNotNull(binder, "token");
        this.run(binder, (Function0<Unit>)new StatefulControlSubscriber$onComplete.StatefulControlSubscriber$onComplete$1(this));
    }
    
    public void onError(final IBinder binder, final String s) {
        Intrinsics.checkParameterIsNotNull(binder, "token");
        Intrinsics.checkParameterIsNotNull(s, "error");
        this.run(binder, (Function0<Unit>)new StatefulControlSubscriber$onError.StatefulControlSubscriber$onError$1(this, s));
    }
    
    public void onNext(final IBinder binder, final Control control) {
        Intrinsics.checkParameterIsNotNull(binder, "token");
        Intrinsics.checkParameterIsNotNull(control, "control");
        this.run(binder, (Function0<Unit>)new StatefulControlSubscriber$onNext.StatefulControlSubscriber$onNext$1(this, binder, control));
    }
    
    public void onSubscribe(final IBinder binder, final IControlsSubscription controlsSubscription) {
        Intrinsics.checkParameterIsNotNull(binder, "token");
        Intrinsics.checkParameterIsNotNull(controlsSubscription, "subs");
        this.run(binder, (Function0<Unit>)new StatefulControlSubscriber$onSubscribe.StatefulControlSubscriber$onSubscribe$1(this, controlsSubscription));
    }
}
