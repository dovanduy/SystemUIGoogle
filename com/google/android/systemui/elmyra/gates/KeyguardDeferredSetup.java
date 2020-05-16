// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import android.content.ContentResolver;
import android.net.Uri;
import java.util.function.Consumer;
import android.provider.Settings$Secure;
import java.util.Collection;
import java.util.ArrayList;
import android.content.Context;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;

public class KeyguardDeferredSetup extends Gate
{
    private boolean mDeferredSetupComplete;
    private final List<Action> mExceptions;
    private final KeyguardVisibility mKeyguardGate;
    private final Listener mKeyguardGateListener;
    private final UserContentObserver mSettingsObserver;
    
    public KeyguardDeferredSetup(final Context context, final List<Action> c) {
        super(context);
        this.mKeyguardGateListener = new Listener() {
            @Override
            public void onGateChanged(final Gate gate) {
                KeyguardDeferredSetup.this.notifyListener();
            }
        };
        this.mExceptions = new ArrayList<Action>(c);
        (this.mKeyguardGate = new KeyguardVisibility(context)).setListener(this.mKeyguardGateListener);
        this.mSettingsObserver = new UserContentObserver(context, Settings$Secure.getUriFor("assist_gesture_setup_complete"), new _$$Lambda$KeyguardDeferredSetup$XgzT2zBsBCjxQnRb5RrgDqiHavM(this), false);
    }
    
    private boolean isDeferredSetupComplete() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean b = false;
        if (Settings$Secure.getIntForUser(contentResolver, "assist_gesture_setup_complete", 0, -2) != 0) {
            b = true;
        }
        return b;
    }
    
    private void updateSetupComplete() {
        final boolean deferredSetupComplete = this.isDeferredSetupComplete();
        if (this.mDeferredSetupComplete != deferredSetupComplete) {
            this.mDeferredSetupComplete = deferredSetupComplete;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        final boolean b = false;
        for (int i = 0; i < this.mExceptions.size(); ++i) {
            if (this.mExceptions.get(i).isAvailable()) {
                return false;
            }
        }
        boolean b2 = b;
        if (!this.mDeferredSetupComplete) {
            b2 = b;
            if (this.mKeyguardGate.isBlocking()) {
                b2 = true;
            }
        }
        return b2;
    }
    
    public boolean isSuwComplete() {
        return this.mDeferredSetupComplete;
    }
    
    @Override
    protected void onActivate() {
        this.mKeyguardGate.activate();
        this.mDeferredSetupComplete = this.isDeferredSetupComplete();
        this.mSettingsObserver.activate();
    }
    
    @Override
    protected void onDeactivate() {
        this.mKeyguardGate.deactivate();
        this.mSettingsObserver.deactivate();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mDeferredSetupComplete -> ");
        sb.append(this.mDeferredSetupComplete);
        sb.append("]");
        return sb.toString();
    }
}
