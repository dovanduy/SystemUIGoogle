// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import android.service.controls.IControlsActionCallback;
import java.util.Iterator;
import android.service.controls.IControlsSubscriber;
import android.service.controls.actions.ControlAction;
import java.util.concurrent.TimeUnit;
import android.service.controls.IControlsSubscriber$Stub;
import android.util.Log;
import java.util.Collection;
import android.os.Bundle;
import android.util.ArraySet;
import java.util.ArrayList;
import android.os.Binder;
import kotlin.jvm.internal.Intrinsics;
import android.os.UserHandle;
import android.os.IBinder;
import android.service.controls.IControlsSubscription;
import java.util.List;
import com.android.internal.annotations.GuardedBy;
import java.util.Set;
import android.content.Intent;
import com.android.systemui.util.concurrency.DelayableExecutor;
import android.content.Context;
import android.content.ComponentName;
import android.service.controls.IControlsActionCallback$Stub;
import android.os.IBinder$DeathRecipient;

public final class ControlsProviderLifecycleManager implements IBinder$DeathRecipient
{
    private static final int BIND_FLAGS = 67108865;
    private final String TAG;
    private final IControlsActionCallback$Stub actionCallbackService;
    private int bindTryCount;
    private final ComponentName componentName;
    private final Context context;
    private final DelayableExecutor executor;
    private final Intent intent;
    private Runnable onLoadCanceller;
    @GuardedBy({ "queuedServiceMethods" })
    private final Set<ServiceMethod> queuedServiceMethods;
    private boolean requiresBound;
    private final ControlsProviderLifecycleManager$serviceConnection.ControlsProviderLifecycleManager$serviceConnection$1 serviceConnection;
    @GuardedBy({ "subscriptions" })
    private final List<IControlsSubscription> subscriptions;
    private final IBinder token;
    private final UserHandle user;
    private ServiceWrapper wrapper;
    
    public ControlsProviderLifecycleManager(final Context context, final DelayableExecutor executor, final IControlsActionCallback$Stub actionCallbackService, final UserHandle user, final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        Intrinsics.checkParameterIsNotNull(actionCallbackService, "actionCallbackService");
        Intrinsics.checkParameterIsNotNull(user, "user");
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        this.context = context;
        this.executor = executor;
        this.actionCallbackService = actionCallbackService;
        this.user = user;
        this.componentName = componentName;
        this.token = (IBinder)new Binder();
        this.subscriptions = new ArrayList<IControlsSubscription>();
        this.queuedServiceMethods = (Set<ServiceMethod>)new ArraySet();
        this.TAG = ControlsProviderLifecycleManager.class.getSimpleName();
        final Intent intent = new Intent();
        intent.setComponent(this.componentName);
        final Bundle bundle = new Bundle();
        bundle.putBinder("CALLBACK_TOKEN", this.token);
        intent.putExtra("CALLBACK_BUNDLE", bundle);
        this.intent = intent;
        this.serviceConnection = new ControlsProviderLifecycleManager$serviceConnection.ControlsProviderLifecycleManager$serviceConnection$1(this);
    }
    
    public static final /* synthetic */ IControlsActionCallback$Stub access$getActionCallbackService$p(final ControlsProviderLifecycleManager controlsProviderLifecycleManager) {
        return controlsProviderLifecycleManager.actionCallbackService;
    }
    
    public static final /* synthetic */ String access$getTAG$p(final ControlsProviderLifecycleManager controlsProviderLifecycleManager) {
        return controlsProviderLifecycleManager.TAG;
    }
    
    public static final /* synthetic */ ServiceWrapper access$getWrapper$p(final ControlsProviderLifecycleManager controlsProviderLifecycleManager) {
        return controlsProviderLifecycleManager.wrapper;
    }
    
    private final void bindService(final boolean b) {
        this.executor.execute((Runnable)new ControlsProviderLifecycleManager$bindService.ControlsProviderLifecycleManager$bindService$1(this, b));
    }
    
    private final void handlePendingServiceMethods() {
        Object o = this.queuedServiceMethods;
        synchronized (o) {
            final ArraySet set = new ArraySet((Collection)this.queuedServiceMethods);
            this.queuedServiceMethods.clear();
            // monitorexit(o)
            o = ((Iterable<ServiceMethod>)set).iterator();
            while (((Iterator)o).hasNext()) {
                ((Iterator<ServiceMethod>)o).next().run();
            }
        }
    }
    
    private final void invokeOrQueue(final ServiceMethod serviceMethod) {
        if (this.wrapper != null) {
            serviceMethod.run();
        }
        else {
            this.queueServiceMethod(serviceMethod);
            this.bindService(true);
        }
    }
    
    private final void queueServiceMethod(final ServiceMethod serviceMethod) {
        synchronized (this.queuedServiceMethods) {
            this.queuedServiceMethods.add(serviceMethod);
        }
    }
    
    public void binderDied() {
        if (this.wrapper == null) {
            return;
        }
        this.wrapper = null;
        if (this.requiresBound) {
            Log.d(this.TAG, "binderDied");
        }
    }
    
    public final void cancelLoadTimeout() {
        final Runnable onLoadCanceller = this.onLoadCanceller;
        if (onLoadCanceller != null) {
            onLoadCanceller.run();
        }
        this.onLoadCanceller = null;
    }
    
    public final void cancelSubscription(final IControlsSubscription obj) {
        Intrinsics.checkParameterIsNotNull(obj, "subscription");
        final String tag = this.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("cancelSubscription: ");
        sb.append(obj);
        Log.d(tag, sb.toString());
        Object o = this.subscriptions;
        synchronized (o) {
            this.subscriptions.remove(obj);
            // monitorexit(o)
            o = this.wrapper;
            if (o != null) {
                ((ServiceWrapper)o).cancel(obj);
            }
        }
    }
    
    public final ComponentName getComponentName() {
        return this.componentName;
    }
    
    public final IBinder getToken() {
        return this.token;
    }
    
    public final UserHandle getUser() {
        return this.user;
    }
    
    public final void maybeBindAndLoad(final IControlsSubscriber$Stub controlsSubscriber$Stub) {
        Intrinsics.checkParameterIsNotNull(controlsSubscriber$Stub, "subscriber");
        this.onLoadCanceller = this.executor.executeDelayed((Runnable)new ControlsProviderLifecycleManager$maybeBindAndLoad.ControlsProviderLifecycleManager$maybeBindAndLoad$1(this, controlsSubscriber$Stub), 30L, TimeUnit.SECONDS);
        this.invokeOrQueue((ServiceMethod)new Load(controlsSubscriber$Stub));
    }
    
    public final void maybeBindAndLoadSuggested(final IControlsSubscriber$Stub controlsSubscriber$Stub) {
        Intrinsics.checkParameterIsNotNull(controlsSubscriber$Stub, "subscriber");
        this.onLoadCanceller = this.executor.executeDelayed((Runnable)new ControlsProviderLifecycleManager$maybeBindAndLoadSuggested.ControlsProviderLifecycleManager$maybeBindAndLoadSuggested$1(this, controlsSubscriber$Stub), 30L, TimeUnit.SECONDS);
        this.invokeOrQueue((ServiceMethod)new Suggest(controlsSubscriber$Stub));
    }
    
    public final void maybeBindAndSendAction(final String s, final ControlAction controlAction) {
        Intrinsics.checkParameterIsNotNull(s, "controlId");
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        this.invokeOrQueue((ServiceMethod)new Action(s, controlAction));
    }
    
    public final void maybeBindAndSubscribe(final List<String> list, final IControlsSubscriber controlsSubscriber) {
        Intrinsics.checkParameterIsNotNull(list, "controlIds");
        Intrinsics.checkParameterIsNotNull(controlsSubscriber, "subscriber");
        this.invokeOrQueue((ServiceMethod)new Subscribe(list, controlsSubscriber));
    }
    
    public final void startSubscription(final IControlsSubscription obj, final long n) {
        Intrinsics.checkParameterIsNotNull(obj, "subscription");
        final String tag = this.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("startSubscription: ");
        sb.append(obj);
        Log.d(tag, sb.toString());
        Object o = this.subscriptions;
        synchronized (o) {
            this.subscriptions.add(obj);
            // monitorexit(o)
            o = this.wrapper;
            if (o != null) {
                ((ServiceWrapper)o).request(obj, n);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ControlsProviderLifecycleManager(");
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("component=");
        sb2.append(this.componentName);
        sb.append(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(", user=");
        sb3.append(this.user);
        sb.append(sb3.toString());
        sb.append(")");
        final String string = sb.toString();
        Intrinsics.checkExpressionValueIsNotNull(string, "StringBuilder(\"ControlsP\u2026\")\")\n        }.toString()");
        return string;
    }
    
    public final void unbindService() {
        final Runnable onLoadCanceller = this.onLoadCanceller;
        if (onLoadCanceller != null) {
            onLoadCanceller.run();
        }
        this.onLoadCanceller = null;
        Object subscriptions = this.subscriptions;
        synchronized (subscriptions) {
            final ArrayList<IControlsSubscription> list = new ArrayList<IControlsSubscription>(this.subscriptions);
            this.subscriptions.clear();
            // monitorexit(subscriptions)
            final Iterator<Object> iterator = list.iterator();
            while (iterator.hasNext()) {
                subscriptions = iterator.next();
                final ServiceWrapper wrapper = this.wrapper;
                if (wrapper != null) {
                    Intrinsics.checkExpressionValueIsNotNull(subscriptions, "it");
                    wrapper.cancel((IControlsSubscription)subscriptions);
                }
            }
            this.bindService(false);
        }
    }
    
    public final class Action extends ServiceMethod
    {
        private final ControlAction action;
        private final String id;
        
        public Action(final String id, final ControlAction action) {
            Intrinsics.checkParameterIsNotNull(id, "id");
            Intrinsics.checkParameterIsNotNull(action, "action");
            this.id = id;
            this.action = action;
        }
        
        @Override
        public boolean callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            final String access$getTAG$p = ControlsProviderLifecycleManager.access$getTAG$p(ControlsProviderLifecycleManager.this);
            final StringBuilder sb = new StringBuilder();
            sb.append("onAction ");
            sb.append(ControlsProviderLifecycleManager.this.getComponentName());
            sb.append(" - ");
            sb.append(this.id);
            Log.d(access$getTAG$p, sb.toString());
            final ServiceWrapper access$getWrapper$p = ControlsProviderLifecycleManager.access$getWrapper$p(ControlsProviderLifecycleManager.this);
            return access$getWrapper$p != null && access$getWrapper$p.action(this.id, this.action, (IControlsActionCallback)ControlsProviderLifecycleManager.access$getActionCallbackService$p(ControlsProviderLifecycleManager.this));
        }
    }
    
    public final class Load extends ServiceMethod
    {
        private final IControlsSubscriber$Stub subscriber;
        
        public Load(final IControlsSubscriber$Stub subscriber) {
            Intrinsics.checkParameterIsNotNull(subscriber, "subscriber");
            this.subscriber = subscriber;
        }
        
        @Override
        public boolean callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            final String access$getTAG$p = ControlsProviderLifecycleManager.access$getTAG$p(ControlsProviderLifecycleManager.this);
            final StringBuilder sb = new StringBuilder();
            sb.append("load ");
            sb.append(ControlsProviderLifecycleManager.this.getComponentName());
            Log.d(access$getTAG$p, sb.toString());
            final ServiceWrapper access$getWrapper$p = ControlsProviderLifecycleManager.access$getWrapper$p(ControlsProviderLifecycleManager.this);
            return access$getWrapper$p != null && access$getWrapper$p.load((IControlsSubscriber)this.subscriber);
        }
    }
    
    public abstract class ServiceMethod
    {
        public abstract boolean callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core();
        
        public final void run() {
            if (!this.callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core()) {
                ControlsProviderLifecycleManager.this.queueServiceMethod(this);
                ControlsProviderLifecycleManager.this.binderDied();
            }
        }
    }
    
    public final class Subscribe extends ServiceMethod
    {
        private final List<String> list;
        private final IControlsSubscriber subscriber;
        
        public Subscribe(final List<String> list, final IControlsSubscriber subscriber) {
            Intrinsics.checkParameterIsNotNull(list, "list");
            Intrinsics.checkParameterIsNotNull(subscriber, "subscriber");
            this.list = list;
            this.subscriber = subscriber;
        }
        
        @Override
        public boolean callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            final String access$getTAG$p = ControlsProviderLifecycleManager.access$getTAG$p(ControlsProviderLifecycleManager.this);
            final StringBuilder sb = new StringBuilder();
            sb.append("subscribe ");
            sb.append(ControlsProviderLifecycleManager.this.getComponentName());
            sb.append(" - ");
            sb.append(this.list);
            Log.d(access$getTAG$p, sb.toString());
            final ServiceWrapper access$getWrapper$p = ControlsProviderLifecycleManager.access$getWrapper$p(ControlsProviderLifecycleManager.this);
            return access$getWrapper$p != null && access$getWrapper$p.subscribe(this.list, this.subscriber);
        }
    }
    
    public final class Suggest extends ServiceMethod
    {
        private final IControlsSubscriber$Stub subscriber;
        
        public Suggest(final IControlsSubscriber$Stub subscriber) {
            Intrinsics.checkParameterIsNotNull(subscriber, "subscriber");
            this.subscriber = subscriber;
        }
        
        @Override
        public boolean callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core() {
            final String access$getTAG$p = ControlsProviderLifecycleManager.access$getTAG$p(ControlsProviderLifecycleManager.this);
            final StringBuilder sb = new StringBuilder();
            sb.append("suggest ");
            sb.append(ControlsProviderLifecycleManager.this.getComponentName());
            Log.d(access$getTAG$p, sb.toString());
            final ServiceWrapper access$getWrapper$p = ControlsProviderLifecycleManager.access$getWrapper$p(ControlsProviderLifecycleManager.this);
            return access$getWrapper$p != null && access$getWrapper$p.loadSuggested((IControlsSubscriber)this.subscriber);
        }
    }
}
