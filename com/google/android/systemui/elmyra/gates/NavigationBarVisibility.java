// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import com.android.systemui.assist.AssistManager;
import com.android.systemui.Dependency;
import java.util.Collection;
import java.util.ArrayList;
import android.content.Context;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;
import com.android.systemui.statusbar.CommandQueue;
import com.google.android.systemui.assist.AssistManagerGoogle;

public class NavigationBarVisibility extends Gate
{
    private final AssistManagerGoogle mAssistManager;
    private final CommandQueue mCommandQueue;
    private final CommandQueue.Callbacks mCommandQueueCallbacks;
    private final int mDisplayId;
    private final List<Action> mExceptions;
    private final Listener mGateListener;
    private boolean mIsKeyguardShowing;
    private boolean mIsNavigationGestural;
    private boolean mIsNavigationHidden;
    private final KeyguardVisibility mKeyguardGate;
    private final NonGesturalNavigation mNavigationModeGate;
    
    public NavigationBarVisibility(final Context context, final List<Action> c) {
        super(context);
        this.mCommandQueueCallbacks = new CommandQueue.Callbacks() {
            @Override
            public void setWindowState(final int n, final int n2, final int n3) {
                if (NavigationBarVisibility.this.mDisplayId == n && n2 == 2) {
                    final boolean b = n3 != 0;
                    if (b != NavigationBarVisibility.this.mIsNavigationHidden) {
                        NavigationBarVisibility.this.mIsNavigationHidden = b;
                        NavigationBarVisibility.this.notifyListener();
                    }
                }
            }
        };
        this.mGateListener = new Listener() {
            @Override
            public void onGateChanged(final Gate gate) {
                if (gate.equals(NavigationBarVisibility.this.mKeyguardGate)) {
                    NavigationBarVisibility.this.updateKeyguardState();
                }
                else if (gate.equals(NavigationBarVisibility.this.mNavigationModeGate)) {
                    NavigationBarVisibility.this.updateNavigationModeState();
                }
            }
        };
        this.mExceptions = new ArrayList<Action>(c);
        this.mIsNavigationHidden = false;
        (this.mCommandQueue = Dependency.get(CommandQueue.class)).addCallback(this.mCommandQueueCallbacks);
        this.mDisplayId = context.getDisplayId();
        this.mAssistManager = Dependency.get((Class<AssistManagerGoogle>)AssistManager.class);
        (this.mKeyguardGate = new KeyguardVisibility(context)).setListener(this.mGateListener);
        (this.mNavigationModeGate = new NonGesturalNavigation(context)).setListener(this.mGateListener);
    }
    
    private boolean isActiveAssistantNga() {
        return this.mAssistManager.isActiveAssistantNga();
    }
    
    private void updateKeyguardState() {
        this.mIsKeyguardShowing = this.mKeyguardGate.isKeyguardShowing();
    }
    
    private void updateNavigationModeState() {
        this.mIsNavigationGestural = this.mNavigationModeGate.isNavigationGestural();
    }
    
    @Override
    protected boolean isBlocked() {
        if (this.mIsKeyguardShowing) {
            return false;
        }
        if (this.mIsNavigationGestural && this.isActiveAssistantNga()) {
            return false;
        }
        for (int i = 0; i < this.mExceptions.size(); ++i) {
            if (this.mExceptions.get(i).isAvailable()) {
                return false;
            }
        }
        return this.mIsNavigationHidden;
    }
    
    @Override
    protected void onActivate() {
        this.mKeyguardGate.activate();
        this.updateKeyguardState();
        this.mNavigationModeGate.activate();
        this.updateNavigationModeState();
    }
    
    @Override
    protected void onDeactivate() {
        this.mNavigationModeGate.deactivate();
        this.updateNavigationModeState();
        this.mKeyguardGate.deactivate();
        this.updateKeyguardState();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mIsNavigationHidden -> ");
        sb.append(this.mIsNavigationHidden);
        sb.append("; mExceptions -> ");
        sb.append(this.mExceptions);
        sb.append("; mIsNavigationGestural -> ");
        sb.append(this.mIsNavigationGestural);
        sb.append("; isActiveAssistantNga() -> ");
        sb.append(this.isActiveAssistantNga());
        sb.append("]");
        return sb.toString();
    }
}
