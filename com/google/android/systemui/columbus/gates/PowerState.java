// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import dagger.Lazy;
import android.os.PowerManager;

public class PowerState extends Gate
{
    private final PowerManager powerManager;
    private final Lazy<WakefulnessLifecycle> wakefulnessLifecycle;
    private final PowerState$wakefulnessLifecycleObserver.PowerState$wakefulnessLifecycleObserver$1 wakefulnessLifecycleObserver;
    
    public PowerState(final Context context, final Lazy<WakefulnessLifecycle> wakefulnessLifecycle) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(wakefulnessLifecycle, "wakefulnessLifecycle");
        super(context);
        this.wakefulnessLifecycle = wakefulnessLifecycle;
        this.powerManager = (PowerManager)context.getSystemService("power");
        this.wakefulnessLifecycleObserver = new PowerState$wakefulnessLifecycleObserver.PowerState$wakefulnessLifecycleObserver$1(this);
    }
    
    @Override
    protected boolean isBlocked() {
        final PowerManager powerManager = this.powerManager;
        return powerManager != null && !powerManager.isInteractive();
    }
    
    @Override
    protected void onActivate() {
        this.wakefulnessLifecycle.get().addObserver((WakefulnessLifecycle.Observer)this.wakefulnessLifecycleObserver);
    }
    
    @Override
    protected void onDeactivate() {
        this.wakefulnessLifecycle.get().removeObserver((WakefulnessLifecycle.Observer)this.wakefulnessLifecycleObserver);
    }
}
