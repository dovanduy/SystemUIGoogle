// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import java.util.function.Consumer;
import android.service.controls.IControlsSubscription;
import android.service.controls.Control;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import android.os.IBinder;
import java.util.Iterator;
import android.service.controls.IControlsSubscriber;
import java.util.List;
import java.util.ArrayList;
import kotlin.collections.CollectionsKt;
import android.service.controls.IControlsActionCallback$Stub;
import android.service.controls.IControlsSubscriber$Stub;
import android.util.Log;
import android.service.controls.actions.ControlAction;
import android.content.ComponentName;
import android.app.ActivityManager;
import kotlin.jvm.internal.Intrinsics;
import dagger.Lazy;
import android.os.UserHandle;
import android.content.Context;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.internal.annotations.VisibleForTesting;

@VisibleForTesting
public class ControlsBindingControllerImpl implements ControlsBindingController
{
    private final ControlsBindingControllerImpl$actionCallbackService.ControlsBindingControllerImpl$actionCallbackService$1 actionCallbackService;
    private final DelayableExecutor backgroundExecutor;
    private final Context context;
    private ControlsProviderLifecycleManager currentProvider;
    private UserHandle currentUser;
    private final Lazy<ControlsController> lazyController;
    private StatefulControlSubscriber statefulControlSubscriber;
    
    public ControlsBindingControllerImpl(final Context context, final DelayableExecutor backgroundExecutor, final Lazy<ControlsController> lazyController) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(backgroundExecutor, "backgroundExecutor");
        Intrinsics.checkParameterIsNotNull(lazyController, "lazyController");
        this.context = context;
        this.backgroundExecutor = backgroundExecutor;
        this.lazyController = lazyController;
        this.currentUser = UserHandle.of(ActivityManager.getCurrentUser());
        this.actionCallbackService = new ControlsBindingControllerImpl$actionCallbackService.ControlsBindingControllerImpl$actionCallbackService$1(this);
    }
    
    public static final /* synthetic */ DelayableExecutor access$getBackgroundExecutor$p(final ControlsBindingControllerImpl controlsBindingControllerImpl) {
        return controlsBindingControllerImpl.backgroundExecutor;
    }
    
    public static final /* synthetic */ ControlsProviderLifecycleManager access$getCurrentProvider$p(final ControlsBindingControllerImpl controlsBindingControllerImpl) {
        return controlsBindingControllerImpl.currentProvider;
    }
    
    public static final /* synthetic */ UserHandle access$getCurrentUser$p(final ControlsBindingControllerImpl controlsBindingControllerImpl) {
        return controlsBindingControllerImpl.currentUser;
    }
    
    public static final /* synthetic */ Lazy access$getLazyController$p(final ControlsBindingControllerImpl controlsBindingControllerImpl) {
        return controlsBindingControllerImpl.lazyController;
    }
    
    private final ControlsProviderLifecycleManager retrieveLifecycleManager(final ComponentName componentName) {
        final ControlsProviderLifecycleManager currentProvider = this.currentProvider;
        if (currentProvider != null) {
            ComponentName componentName2;
            if (currentProvider != null) {
                componentName2 = currentProvider.getComponentName();
            }
            else {
                componentName2 = null;
            }
            if (Intrinsics.areEqual(componentName2, componentName) ^ true) {
                this.unbind();
            }
        }
        final ControlsProviderLifecycleManager currentProvider2 = this.currentProvider;
        ControlsProviderLifecycleManager providerManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core;
        if (currentProvider2 != null) {
            providerManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core = currentProvider2;
        }
        else {
            providerManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core = this.createProviderManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core(componentName);
        }
        return this.currentProvider = providerManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core;
    }
    
    private final void unbind() {
        final ControlsProviderLifecycleManager currentProvider = this.currentProvider;
        if (currentProvider != null) {
            currentProvider.unbindService();
        }
        this.currentProvider = null;
    }
    
    @Override
    public void action(final ComponentName componentName, final ControlInfo controlInfo, final ControlAction controlAction) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        Intrinsics.checkParameterIsNotNull(controlInfo, "controlInfo");
        Intrinsics.checkParameterIsNotNull(controlAction, "action");
        if (this.statefulControlSubscriber == null) {
            Log.w("ControlsBindingControllerImpl", "No actions can occur outside of an active subscription. Ignoring.");
        }
        else {
            this.retrieveLifecycleManager(componentName).maybeBindAndSendAction(controlInfo.getControlId(), controlAction);
        }
    }
    
    @Override
    public Runnable bindAndLoad(final ComponentName componentName, final LoadCallback loadCallback) {
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        Intrinsics.checkParameterIsNotNull(loadCallback, "callback");
        final LoadSubscriber loadSubscriber = new LoadSubscriber(loadCallback, 100000L);
        this.retrieveLifecycleManager(componentName).maybeBindAndLoad(loadSubscriber);
        return loadSubscriber.loadCancel();
    }
    
    @Override
    public void bindAndLoadSuggested(final ComponentName componentName, final LoadCallback loadCallback) {
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        Intrinsics.checkParameterIsNotNull(loadCallback, "callback");
        this.retrieveLifecycleManager(componentName).maybeBindAndLoadSuggested(new LoadSubscriber(loadCallback, 4L));
    }
    
    @Override
    public void changeUser(final UserHandle currentUser) {
        Intrinsics.checkParameterIsNotNull(currentUser, "newUser");
        if (Intrinsics.areEqual(currentUser, this.currentUser)) {
            return;
        }
        this.unsubscribe();
        this.unbind();
        this.currentProvider = null;
        this.currentUser = currentUser;
    }
    
    @VisibleForTesting
    public ControlsProviderLifecycleManager createProviderManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        final Context context = this.context;
        final DelayableExecutor backgroundExecutor = this.backgroundExecutor;
        final ControlsBindingControllerImpl$actionCallbackService.ControlsBindingControllerImpl$actionCallbackService$1 actionCallbackService = this.actionCallbackService;
        final UserHandle currentUser = this.currentUser;
        Intrinsics.checkExpressionValueIsNotNull(currentUser, "currentUser");
        return new ControlsProviderLifecycleManager(context, backgroundExecutor, (IControlsActionCallback$Stub)actionCallbackService, currentUser, componentName);
    }
    
    @Override
    public int getCurrentUserId() {
        final UserHandle currentUser = this.currentUser;
        Intrinsics.checkExpressionValueIsNotNull(currentUser, "currentUser");
        return currentUser.getIdentifier();
    }
    
    @Override
    public void onComponentRemoved(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "componentName");
        this.backgroundExecutor.execute((Runnable)new ControlsBindingControllerImpl$onComponentRemoved.ControlsBindingControllerImpl$onComponentRemoved$1(this, componentName));
    }
    
    @Override
    public void subscribe(final StructureInfo structureInfo) {
        Intrinsics.checkParameterIsNotNull(structureInfo, "structureInfo");
        this.unsubscribe();
        final ControlsProviderLifecycleManager retrieveLifecycleManager = this.retrieveLifecycleManager(structureInfo.getComponentName());
        final ControlsController value = this.lazyController.get();
        Intrinsics.checkExpressionValueIsNotNull(value, "lazyController.get()");
        final StatefulControlSubscriber statefulControlSubscriber = new StatefulControlSubscriber(value, retrieveLifecycleManager, this.backgroundExecutor, 100000L);
        this.statefulControlSubscriber = statefulControlSubscriber;
        final List<ControlInfo> controls = structureInfo.getControls();
        final ArrayList list = new ArrayList<String>(CollectionsKt.collectionSizeOrDefault((Iterable<?>)controls, 10));
        final Iterator<Object> iterator = controls.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getControlId());
        }
        retrieveLifecycleManager.maybeBindAndSubscribe((List<String>)list, (IControlsSubscriber)statefulControlSubscriber);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("  ControlsBindingController:\n");
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("    currentUser=");
        sb2.append(this.currentUser);
        sb2.append('\n');
        sb.append(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("    StatefulControlSubscriber=");
        sb3.append(this.statefulControlSubscriber);
        sb.append(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("    Providers=");
        sb4.append(this.currentProvider);
        sb4.append('\n');
        sb.append(sb4.toString());
        final String string = sb.toString();
        Intrinsics.checkExpressionValueIsNotNull(string, "StringBuilder(\"  Control\u2026\\n\")\n        }.toString()");
        return string;
    }
    
    @Override
    public void unsubscribe() {
        final StatefulControlSubscriber statefulControlSubscriber = this.statefulControlSubscriber;
        if (statefulControlSubscriber != null) {
            statefulControlSubscriber.cancel();
        }
        this.statefulControlSubscriber = null;
    }
    
    private abstract class CallbackRunnable implements Runnable
    {
        private final ControlsProviderLifecycleManager provider;
        private final IBinder token;
        
        public CallbackRunnable(final IBinder token) {
            Intrinsics.checkParameterIsNotNull(token, "token");
            this.token = token;
            this.provider = ControlsBindingControllerImpl.access$getCurrentProvider$p(ControlsBindingControllerImpl.this);
        }
        
        public abstract void doRun();
        
        protected final ControlsProviderLifecycleManager getProvider() {
            return this.provider;
        }
        
        @Override
        public void run() {
            final ControlsProviderLifecycleManager provider = this.provider;
            if (provider == null) {
                Log.e("ControlsBindingControllerImpl", "No current provider set");
                return;
            }
            if (Intrinsics.areEqual(provider.getUser(), ControlsBindingControllerImpl.access$getCurrentUser$p(ControlsBindingControllerImpl.this)) ^ true) {
                final StringBuilder sb = new StringBuilder();
                sb.append("User ");
                sb.append(this.provider.getUser());
                sb.append(" is not current user");
                Log.e("ControlsBindingControllerImpl", sb.toString());
                return;
            }
            if (Intrinsics.areEqual(this.token, this.provider.getToken()) ^ true) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Provider for token:");
                sb2.append(this.token);
                sb2.append(" does not exist anymore");
                Log.e("ControlsBindingControllerImpl", sb2.toString());
                return;
            }
            this.doRun();
        }
    }
    
    private final class LoadSubscriber extends IControlsSubscriber$Stub
    {
        private Function0<Unit> _loadCancelInternal;
        private final LoadCallback callback;
        private boolean hasError;
        private final ArrayList<Control> loadedControls;
        private final long requestLimit;
        
        public LoadSubscriber(final LoadCallback callback, final long requestLimit) {
            Intrinsics.checkParameterIsNotNull(callback, "callback");
            this.callback = callback;
            this.requestLimit = requestLimit;
            this.loadedControls = new ArrayList<Control>();
        }
        
        public final ArrayList<Control> getLoadedControls() {
            return this.loadedControls;
        }
        
        public final Runnable loadCancel() {
            return (Runnable)new ControlsBindingControllerImpl$LoadSubscriber$loadCancel.ControlsBindingControllerImpl$LoadSubscriber$loadCancel$1(this);
        }
        
        public void onComplete(final IBinder binder) {
            Intrinsics.checkParameterIsNotNull(binder, "token");
            this._loadCancelInternal = (Function0<Unit>)ControlsBindingControllerImpl$LoadSubscriber$onComplete.ControlsBindingControllerImpl$LoadSubscriber$onComplete$1.INSTANCE;
            if (!this.hasError) {
                final ControlsProviderLifecycleManager access$getCurrentProvider$p = ControlsBindingControllerImpl.access$getCurrentProvider$p(ControlsBindingControllerImpl.this);
                if (access$getCurrentProvider$p != null) {
                    access$getCurrentProvider$p.cancelLoadTimeout();
                }
                ControlsBindingControllerImpl.access$getBackgroundExecutor$p(ControlsBindingControllerImpl.this).execute(new OnLoadRunnable(binder, this.loadedControls, this.callback));
            }
        }
        
        public void onError(final IBinder binder, final String s) {
            Intrinsics.checkParameterIsNotNull(binder, "token");
            Intrinsics.checkParameterIsNotNull(s, "s");
            this.hasError = true;
            this._loadCancelInternal = (Function0<Unit>)ControlsBindingControllerImpl$LoadSubscriber$onError.ControlsBindingControllerImpl$LoadSubscriber$onError$1.INSTANCE;
            final ControlsProviderLifecycleManager access$getCurrentProvider$p = ControlsBindingControllerImpl.access$getCurrentProvider$p(ControlsBindingControllerImpl.this);
            if (access$getCurrentProvider$p != null) {
                access$getCurrentProvider$p.cancelLoadTimeout();
            }
            ControlsBindingControllerImpl.access$getBackgroundExecutor$p(ControlsBindingControllerImpl.this).execute(new OnLoadErrorRunnable(binder, s, this.callback));
        }
        
        public void onNext(final IBinder binder, final Control control) {
            Intrinsics.checkParameterIsNotNull(binder, "token");
            Intrinsics.checkParameterIsNotNull(control, "c");
            ControlsBindingControllerImpl.access$getBackgroundExecutor$p(ControlsBindingControllerImpl.this).execute((Runnable)new ControlsBindingControllerImpl$LoadSubscriber$onNext.ControlsBindingControllerImpl$LoadSubscriber$onNext$1(this, control));
        }
        
        public void onSubscribe(final IBinder binder, final IControlsSubscription controlsSubscription) {
            Intrinsics.checkParameterIsNotNull(binder, "token");
            Intrinsics.checkParameterIsNotNull(controlsSubscription, "subs");
            this._loadCancelInternal = (Function0<Unit>)new ControlsBindingControllerImpl$LoadSubscriber$onSubscribe.ControlsBindingControllerImpl$LoadSubscriber$onSubscribe$1(controlsSubscription);
            ControlsBindingControllerImpl.access$getBackgroundExecutor$p(ControlsBindingControllerImpl.this).execute(new OnSubscribeRunnable(binder, controlsSubscription, this.requestLimit));
        }
    }
    
    private final class OnActionResponseRunnable extends CallbackRunnable
    {
        private final String controlId;
        private final int response;
        
        public OnActionResponseRunnable(final IBinder binder, final String controlId, final int response) {
            Intrinsics.checkParameterIsNotNull(binder, "token");
            Intrinsics.checkParameterIsNotNull(controlId, "controlId");
            super(binder);
            this.controlId = controlId;
            this.response = response;
        }
        
        @Override
        public void doRun() {
            final ControlsProviderLifecycleManager provider = ((CallbackRunnable)this).getProvider();
            if (provider != null) {
                ControlsBindingControllerImpl.access$getLazyController$p(ControlsBindingControllerImpl.this).get().onActionResponse(provider.getComponentName(), this.controlId, this.response);
            }
        }
    }
    
    private final class OnLoadErrorRunnable extends CallbackRunnable
    {
        private final LoadCallback callback;
        private final String error;
        
        public OnLoadErrorRunnable(final ControlsBindingControllerImpl controlsBindingControllerImpl, final IBinder binder, final String error, final LoadCallback callback) {
            Intrinsics.checkParameterIsNotNull(binder, "token");
            Intrinsics.checkParameterIsNotNull(error, "error");
            Intrinsics.checkParameterIsNotNull(callback, "callback");
            super(binder);
            this.error = error;
            this.callback = callback;
        }
        
        @Override
        public void doRun() {
            this.callback.error(this.error);
            final ControlsProviderLifecycleManager provider = ((CallbackRunnable)this).getProvider();
            if (provider != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onError receive from '");
                sb.append(provider.getComponentName());
                sb.append("': ");
                sb.append(this.error);
                Log.e("ControlsBindingControllerImpl", sb.toString());
            }
        }
    }
    
    private final class OnLoadRunnable extends CallbackRunnable
    {
        private final LoadCallback callback;
        private final List<Control> list;
        
        public OnLoadRunnable(final ControlsBindingControllerImpl controlsBindingControllerImpl, final IBinder binder, final List<Control> list, final LoadCallback callback) {
            Intrinsics.checkParameterIsNotNull(binder, "token");
            Intrinsics.checkParameterIsNotNull(list, "list");
            Intrinsics.checkParameterIsNotNull(callback, "callback");
            super(binder);
            this.list = list;
            this.callback = callback;
        }
        
        @Override
        public void doRun() {
            ((Consumer<List<Control>>)this.callback).accept(this.list);
        }
    }
    
    private final class OnSubscribeRunnable extends CallbackRunnable
    {
        private final long requestLimit;
        private final IControlsSubscription subscription;
        
        public OnSubscribeRunnable(final ControlsBindingControllerImpl controlsBindingControllerImpl, final IBinder binder, final IControlsSubscription subscription, final long requestLimit) {
            Intrinsics.checkParameterIsNotNull(binder, "token");
            Intrinsics.checkParameterIsNotNull(subscription, "subscription");
            super(binder);
            this.subscription = subscription;
            this.requestLimit = requestLimit;
        }
        
        @Override
        public void doRun() {
            final ControlsProviderLifecycleManager provider = ((CallbackRunnable)this).getProvider();
            if (provider != null) {
                provider.startSubscription(this.subscription, this.requestLimit);
            }
        }
    }
}
