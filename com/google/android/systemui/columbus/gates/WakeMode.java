// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import android.content.ContentResolver;
import kotlin.Unit;
import android.net.Uri;
import kotlin.jvm.functions.Function1;
import android.provider.Settings$Secure;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import dagger.Lazy;
import android.content.Context;
import com.google.android.systemui.columbus.ColumbusContentObserver;

public final class WakeMode extends PowerState
{
    private final ColumbusContentObserver settingsObserver;
    private boolean wakeSettingEnabled;
    
    public WakeMode(final Context context, final Lazy<WakefulnessLifecycle> lazy, final ColumbusContentObserver.Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(lazy, "wakefulnessLifecycle");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, lazy);
        final Uri uri = Settings$Secure.getUriFor("assist_gesture_wake_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uri, "Settings.Secure.getUriFo\u2026IST_GESTURE_WAKE_ENABLED)");
        this.settingsObserver = factory.create(uri, (Function1<? super Uri, Unit>)new WakeMode$settingsObserver.WakeMode$settingsObserver$1(this));
        this.wakeSettingEnabled = this.isWakeSettingEnabled();
    }
    
    private final boolean isWakeSettingEnabled() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean b = true;
        if (Settings$Secure.getIntForUser(contentResolver, "assist_gesture_wake_enabled", 1, -2) == 0) {
            b = false;
        }
        return b;
    }
    
    private final void updateWakeSetting() {
        final boolean wakeSettingEnabled = this.isWakeSettingEnabled();
        if (wakeSettingEnabled != this.wakeSettingEnabled) {
            this.wakeSettingEnabled = wakeSettingEnabled;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        return !this.wakeSettingEnabled && super.isBlocked();
    }
    
    @Override
    protected void onActivate() {
        this.wakeSettingEnabled = this.isWakeSettingEnabled();
        this.settingsObserver.activate();
    }
    
    @Override
    protected void onDeactivate() {
        this.settingsObserver.deactivate();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [wakeSettingEnabled -> ");
        sb.append(this.wakeSettingEnabled);
        sb.append("]");
        return sb.toString();
    }
}
