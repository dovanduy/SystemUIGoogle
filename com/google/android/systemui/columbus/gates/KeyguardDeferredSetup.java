// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import java.util.Iterator;
import android.content.ContentResolver;
import kotlin.Unit;
import android.net.Uri;
import kotlin.jvm.functions.Function1;
import android.provider.Settings$Secure;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;

public final class KeyguardDeferredSetup extends Gate
{
    private boolean deferredSetupComplete;
    private final List<Action> exceptions;
    private final KeyguardVisibility keyguardGate;
    private final ColumbusContentObserver settingsObserver;
    
    public KeyguardDeferredSetup(final Context context, final List<Action> exceptions, final KeyguardVisibility keyguardGate, final ColumbusContentObserver.Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(exceptions, "exceptions");
        Intrinsics.checkParameterIsNotNull(keyguardGate, "keyguardGate");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context);
        this.exceptions = exceptions;
        this.keyguardGate = keyguardGate;
        final Uri uri = Settings$Secure.getUriFor("assist_gesture_setup_complete");
        Intrinsics.checkExpressionValueIsNotNull(uri, "Settings.Secure.getUriFo\u2026T_GESTURE_SETUP_COMPLETE)");
        this.settingsObserver = factory.create(uri, (Function1<? super Uri, Unit>)new KeyguardDeferredSetup$settingsObserver.KeyguardDeferredSetup$settingsObserver$1(this));
        this.keyguardGate.setListener((Listener)new Listener() {
            final /* synthetic */ KeyguardDeferredSetup this$0;
            
            @Override
            public void onGateChanged(final Gate gate) {
                Intrinsics.checkParameterIsNotNull(gate, "gate");
                this.this$0.notifyListener();
            }
        });
    }
    
    private final boolean isDeferredSetupComplete() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean b = false;
        if (Settings$Secure.getIntForUser(contentResolver, "assist_gesture_setup_complete", 0, -2) != 0) {
            b = true;
        }
        return b;
    }
    
    private final void updateSetupComplete() {
        final boolean deferredSetupComplete = this.isDeferredSetupComplete();
        if (this.deferredSetupComplete != deferredSetupComplete) {
            this.deferredSetupComplete = deferredSetupComplete;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        while (true) {
            for (final Action next : this.exceptions) {
                if (next.isAvailable()) {
                    final Action action = next;
                    boolean b2;
                    final boolean b = b2 = false;
                    if (action == null) {
                        b2 = b;
                        if (!this.deferredSetupComplete) {
                            b2 = b;
                            if (this.keyguardGate.isBlocking()) {
                                b2 = true;
                            }
                        }
                    }
                    return b2;
                }
            }
            Action next = null;
            continue;
        }
    }
    
    @Override
    protected void onActivate() {
        this.keyguardGate.activate();
        this.deferredSetupComplete = this.isDeferredSetupComplete();
        this.settingsObserver.activate();
    }
    
    @Override
    protected void onDeactivate() {
        this.keyguardGate.deactivate();
        this.settingsObserver.deactivate();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [deferredSetupComplete -> ");
        sb.append(this.deferredSetupComplete);
        sb.append("]");
        return sb.toString();
    }
}
