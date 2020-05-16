// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.os.Handler;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public abstract class TransientGate extends Gate
{
    private boolean blocking;
    private final Function0<Unit> resetGate;
    private final Handler resetGateHandler;
    
    public TransientGate(final Context context, final Handler resetGateHandler) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(resetGateHandler, "resetGateHandler");
        super(context);
        this.resetGateHandler = resetGateHandler;
        this.resetGate = (Function0<Unit>)new TransientGate$resetGate.TransientGate$resetGate$1(this);
    }
    
    protected final void blockForMillis(final long n) {
        this.blocking = true;
        this.notifyListener();
        final Handler resetGateHandler = this.resetGateHandler;
        TransientGate$sam$java_lang_Runnable$0 resetGate;
        final Function0<Unit> function0 = (Function0<Unit>)(resetGate = (TransientGate$sam$java_lang_Runnable$0)this.resetGate);
        if (function0 != null) {
            resetGate = new TransientGate$sam$java_lang_Runnable$0(function0);
        }
        resetGateHandler.removeCallbacks((Runnable)resetGate);
        final Handler resetGateHandler2 = this.resetGateHandler;
        final Function0<Unit> resetGate2 = this.resetGate;
        Runnable runnable;
        if ((runnable = (Runnable)resetGate2) != null) {
            runnable = new TransientGate$sam$java_lang_Runnable$0(resetGate2);
        }
        resetGateHandler2.postDelayed((Runnable)runnable, n);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.blocking;
    }
}
