// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import java.util.function.Consumer;
import android.view.View;
import android.app.PendingIntent;
import android.content.Intent;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import com.android.systemui.plugins.ActivityStarter;

public class ActivityStarterDelegate implements ActivityStarter
{
    private Optional<Lazy<StatusBar>> mActualStarter;
    
    public ActivityStarterDelegate(final Optional<Lazy<StatusBar>> mActualStarter) {
        this.mActualStarter = mActualStarter;
    }
    
    @Override
    public void dismissKeyguardThenExecute(final OnDismissAction onDismissAction, final Runnable runnable, final boolean b) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$EdR7EnJaQsucB6gVTu3f0VVIJG0(onDismissAction, runnable, b));
    }
    
    @Override
    public void postQSRunnableDismissingKeyguard(final Runnable runnable) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$nAMiUKIuJCQJlUCym9gIzdU3mxI(runnable));
    }
    
    @Override
    public void postStartActivityDismissingKeyguard(final PendingIntent pendingIntent) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$ntMGdPXHlgGHJa34MKvZ31nUwKY(pendingIntent));
    }
    
    @Override
    public void postStartActivityDismissingKeyguard(final Intent intent, final int n) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$Bkt5K0j7l11YRIlpia_xFvXNPbk(intent, n));
    }
    
    @Override
    public void startActivity(final Intent intent, final boolean b) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$EQWsLMWn8q7rwvIKj7BUOEWOer0(intent, b));
    }
    
    @Override
    public void startActivity(final Intent intent, final boolean b, final Callback callback) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$oudv1wNK3Nlq7Lmdo4di21Zs8MY(intent, b, callback));
    }
    
    @Override
    public void startActivity(final Intent intent, final boolean b, final boolean b2) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$6Sj7OMH4lNAnb8MJLTpMcmyzi58(intent, b, b2));
    }
    
    @Override
    public void startActivity(final Intent intent, final boolean b, final boolean b2, final int n) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$ILGza7s66HZ0nctdJ0wnDebSRW8(intent, b, b2, n));
    }
    
    @Override
    public void startPendingIntentDismissingKeyguard(final PendingIntent pendingIntent) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$ADi9yiVtZ_7ObMe5Z0tk1YjrdVA(pendingIntent));
    }
    
    @Override
    public void startPendingIntentDismissingKeyguard(final PendingIntent pendingIntent, final Runnable runnable) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$INm749Eqo5FOmTBr8joulwrrt64(pendingIntent, runnable));
    }
    
    @Override
    public void startPendingIntentDismissingKeyguard(final PendingIntent pendingIntent, final Runnable runnable, final View view) {
        this.mActualStarter.ifPresent(new _$$Lambda$ActivityStarterDelegate$wcup9XfV8BD_xZsAFv2kWIfmGN0(pendingIntent, runnable, view));
    }
}
