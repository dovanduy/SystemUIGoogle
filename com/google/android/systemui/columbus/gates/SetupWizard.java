// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import java.util.Iterator;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import java.util.Set;
import android.content.Context;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.Lazy;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;

public final class SetupWizard extends Gate
{
    private final List<Action> exceptions;
    private final Lazy<DeviceProvisionedController> provisionedController;
    private final SetupWizard$provisionedListener.SetupWizard$provisionedListener$1 provisionedListener;
    private boolean setupComplete;
    
    public SetupWizard(final Context context, final Set<Action> set, final Lazy<DeviceProvisionedController> provisionedController) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(set, "setupWizardExceptions");
        Intrinsics.checkParameterIsNotNull(provisionedController, "provisionedController");
        super(context);
        this.provisionedController = provisionedController;
        this.exceptions = CollectionsKt.toList((Iterable<? extends Action>)set);
        this.provisionedListener = new SetupWizard$provisionedListener.SetupWizard$provisionedListener$1(this);
    }
    
    private final boolean isSetupComplete() {
        final DeviceProvisionedController value = this.provisionedController.get();
        Intrinsics.checkExpressionValueIsNotNull(value, "provisionedController.get()");
        if (value.isDeviceProvisioned()) {
            final DeviceProvisionedController value2 = this.provisionedController.get();
            Intrinsics.checkExpressionValueIsNotNull(value2, "provisionedController.get()");
            if (value2.isCurrentUserSetup()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected boolean isBlocked() {
        while (true) {
            for (final Action next : this.exceptions) {
                if (next.isAvailable()) {
                    final Action action = next;
                    boolean b = false;
                    if (action == null) {
                        b = b;
                        if (!this.setupComplete) {
                            b = true;
                        }
                    }
                    return b;
                }
            }
            Action next = null;
            continue;
        }
    }
    
    @Override
    protected void onActivate() {
        this.setupComplete = this.isSetupComplete();
        this.provisionedController.get().addCallback((DeviceProvisionedController.DeviceProvisionedListener)this.provisionedListener);
    }
    
    @Override
    protected void onDeactivate() {
        this.provisionedController.get().removeCallback((DeviceProvisionedController.DeviceProvisionedListener)this.provisionedListener);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isDeviceProvisioned -> ");
        final DeviceProvisionedController value = this.provisionedController.get();
        Intrinsics.checkExpressionValueIsNotNull(value, "provisionedController.get()");
        sb.append(value.isDeviceProvisioned());
        sb.append("; isCurrentUserSetup -> ");
        final DeviceProvisionedController value2 = this.provisionedController.get();
        Intrinsics.checkExpressionValueIsNotNull(value2, "provisionedController.get()");
        sb.append(value2.isCurrentUserSetup());
        sb.append("]");
        return sb.toString();
    }
}
