// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.assist.AssistManager;
import android.content.Context;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import com.google.android.systemui.assist.AssistManagerGoogle;

public final class NavigationBarVisibility extends Gate
{
    private final AssistManagerGoogle assistManager;
    private final NavigationBarVisibility$commandQueueCallbacks.NavigationBarVisibility$commandQueueCallbacks$1 commandQueueCallbacks;
    private final int displayId;
    private final List<Action> exceptions;
    private final NavigationBarVisibility$gateListener.NavigationBarVisibility$gateListener$1 gateListener;
    private boolean isKeyguardShowing;
    private boolean isNavigationGestural;
    private boolean isNavigationHidden;
    private final KeyguardVisibility keyguardGate;
    private final NonGesturalNavigation navigationModeGate;
    
    public NavigationBarVisibility(final Context context, final List<Action> exceptions, final AssistManager assistManager, final KeyguardVisibility keyguardGate, final NonGesturalNavigation navigationModeGate, final CommandQueue commandQueue) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(exceptions, "exceptions");
        Intrinsics.checkParameterIsNotNull(assistManager, "assistManager");
        Intrinsics.checkParameterIsNotNull(keyguardGate, "keyguardGate");
        Intrinsics.checkParameterIsNotNull(navigationModeGate, "navigationModeGate");
        Intrinsics.checkParameterIsNotNull(commandQueue, "commandQueue");
        super(context);
        this.exceptions = exceptions;
        this.keyguardGate = keyguardGate;
        this.navigationModeGate = navigationModeGate;
        this.displayId = context.getDisplayId();
        this.commandQueueCallbacks = new NavigationBarVisibility$commandQueueCallbacks.NavigationBarVisibility$commandQueueCallbacks$1(this);
        AssistManager assistManager2 = assistManager;
        if (!(assistManager instanceof AssistManagerGoogle)) {
            assistManager2 = null;
        }
        this.assistManager = (AssistManagerGoogle)assistManager2;
        this.gateListener = new NavigationBarVisibility$gateListener.NavigationBarVisibility$gateListener$1(this);
        commandQueue.addCallback((CommandQueue.Callbacks)this.commandQueueCallbacks);
        this.keyguardGate.setListener((Listener)this.gateListener);
        this.navigationModeGate.setListener((Listener)this.gateListener);
    }
    
    private final boolean isActiveAssistantNga() {
        final AssistManagerGoogle assistManager = this.assistManager;
        return assistManager != null && assistManager.isActiveAssistantNga();
    }
    
    private final void updateKeyguardState() {
        this.isKeyguardShowing = this.keyguardGate.isKeyguardShowing();
    }
    
    private final void updateNavigationModeState() {
        this.isNavigationGestural = this.navigationModeGate.isNavigationGestural();
    }
    
    @Override
    protected boolean isBlocked() {
        final boolean isKeyguardShowing = this.isKeyguardShowing;
        boolean isNavigationHidden = false;
        if (isKeyguardShowing) {
            return false;
        }
        if (this.isNavigationGestural && this.isActiveAssistantNga()) {
            return false;
        }
        while (true) {
            for (final Action next : this.exceptions) {
                if (next.isAvailable()) {
                    if (next == null) {
                        isNavigationHidden = this.isNavigationHidden;
                    }
                    return isNavigationHidden;
                }
            }
            Action next = null;
            continue;
        }
    }
    
    @Override
    protected void onActivate() {
        this.keyguardGate.activate();
        this.updateKeyguardState();
        this.navigationModeGate.activate();
        this.updateNavigationModeState();
    }
    
    @Override
    protected void onDeactivate() {
        this.navigationModeGate.deactivate();
        this.updateNavigationModeState();
        this.keyguardGate.deactivate();
        this.updateKeyguardState();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isNavigationHidden -> ");
        sb.append(this.isNavigationHidden);
        sb.append("; exceptions -> ");
        sb.append(this.exceptions);
        sb.append("; isNavigationGestural -> ");
        sb.append(this.isNavigationGestural);
        sb.append("; isActiveAssistantNga() -> ");
        sb.append(this.isActiveAssistantNga());
        sb.append("]");
        return sb.toString();
    }
}
