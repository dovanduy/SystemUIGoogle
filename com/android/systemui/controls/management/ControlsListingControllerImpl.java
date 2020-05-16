// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import java.util.ArrayList;
import java.util.Iterator;
import com.android.systemui.controls.ControlsServiceInfo;
import android.content.ComponentName;
import android.os.UserHandle;
import com.android.internal.annotations.VisibleForTesting;
import java.util.LinkedHashSet;
import android.app.ActivityManager;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.functions.Function1;
import com.android.settingslib.applications.ServiceListing;
import android.content.Context;
import java.util.Set;
import java.util.concurrent.Executor;
import android.content.pm.ServiceInfo;
import java.util.List;

public final class ControlsListingControllerImpl implements ControlsListingController
{
    private List<? extends ServiceInfo> availableServices;
    private final Executor backgroundExecutor;
    private final Set<ControlsListingCallback> callbacks;
    private final Context context;
    private int currentUserId;
    private ServiceListing serviceListing;
    private final Function1<Context, ServiceListing> serviceListingBuilder;
    private final ServiceListing.Callback serviceListingCallback;
    
    public ControlsListingControllerImpl(final Context context, final Executor executor) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        this(context, executor, ControlsListingControllerImpl$1.INSTANCE);
    }
    
    @VisibleForTesting
    public ControlsListingControllerImpl(final Context context, final Executor backgroundExecutor, final Function1<? super Context, ? extends ServiceListing> serviceListingBuilder) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(backgroundExecutor, "backgroundExecutor");
        Intrinsics.checkParameterIsNotNull(serviceListingBuilder, "serviceListingBuilder");
        this.context = context;
        this.backgroundExecutor = backgroundExecutor;
        this.serviceListingBuilder = (Function1<Context, ServiceListing>)serviceListingBuilder;
        this.serviceListing = serviceListingBuilder.invoke(context);
        this.availableServices = CollectionsKt.emptyList();
        this.currentUserId = ActivityManager.getCurrentUser();
        final ControlsListingControllerImpl$serviceListingCallback.ControlsListingControllerImpl$serviceListingCallback$1 serviceListingCallback = new ControlsListingControllerImpl$serviceListingCallback.ControlsListingControllerImpl$serviceListingCallback$1(this);
        this.serviceListingCallback = (ServiceListing.Callback)serviceListingCallback;
        this.serviceListing.addCallback((ServiceListing.Callback)serviceListingCallback);
        this.serviceListing.setListening(true);
        this.serviceListing.reload();
        this.callbacks = new LinkedHashSet<ControlsListingCallback>();
    }
    
    @Override
    public void addCallback(final ControlsListingCallback controlsListingCallback) {
        Intrinsics.checkParameterIsNotNull(controlsListingCallback, "listener");
        this.backgroundExecutor.execute((Runnable)new ControlsListingControllerImpl$addCallback.ControlsListingControllerImpl$addCallback$1(this, controlsListingCallback));
    }
    
    @Override
    public void changeUser(final UserHandle userHandle) {
        Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
        this.backgroundExecutor.execute((Runnable)new ControlsListingControllerImpl$changeUser.ControlsListingControllerImpl$changeUser$1(this, userHandle));
    }
    
    @Override
    public CharSequence getAppLabel(final ComponentName componentName) {
        Intrinsics.checkParameterIsNotNull(componentName, "name");
        final Iterator<Object> iterator = this.getCurrentServices().iterator();
        while (true) {
            ControlsServiceInfo next;
            do {
                final boolean hasNext = iterator.hasNext();
                final CharSequence charSequence = null;
                if (!hasNext) {
                    final ControlsServiceInfo controlsServiceInfo = null;
                    final ControlsServiceInfo controlsServiceInfo2 = controlsServiceInfo;
                    CharSequence loadLabel = charSequence;
                    if (controlsServiceInfo2 != null) {
                        loadLabel = controlsServiceInfo2.loadLabel();
                    }
                    return loadLabel;
                }
                next = iterator.next();
            } while (!Intrinsics.areEqual(next.componentName, componentName));
            final ControlsServiceInfo controlsServiceInfo = next;
            continue;
        }
    }
    
    public List<ControlsServiceInfo> getCurrentServices() {
        final List<? extends ServiceInfo> availableServices = this.availableServices;
        final ArrayList list = new ArrayList<ControlsServiceInfo>(CollectionsKt.collectionSizeOrDefault((Iterable<?>)availableServices, 10));
        final Iterator<ServiceInfo> iterator = availableServices.iterator();
        while (iterator.hasNext()) {
            list.add(new ControlsServiceInfo(this.context, iterator.next()));
        }
        return (List<ControlsServiceInfo>)list;
    }
    
    @Override
    public int getCurrentUserId() {
        return this.currentUserId;
    }
    
    @Override
    public void removeCallback(final ControlsListingCallback controlsListingCallback) {
        Intrinsics.checkParameterIsNotNull(controlsListingCallback, "listener");
        this.backgroundExecutor.execute((Runnable)new ControlsListingControllerImpl$removeCallback.ControlsListingControllerImpl$removeCallback$1(this, controlsListingCallback));
    }
}
