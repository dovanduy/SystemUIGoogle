// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.view.View;
import android.content.Intent;
import android.app.PendingIntent;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(version = 2)
public interface ActivityStarter
{
    public static final int VERSION = 2;
    
    void dismissKeyguardThenExecute(final OnDismissAction p0, final Runnable p1, final boolean p2);
    
    void postQSRunnableDismissingKeyguard(final Runnable p0);
    
    void postStartActivityDismissingKeyguard(final PendingIntent p0);
    
    void postStartActivityDismissingKeyguard(final Intent p0, final int p1);
    
    void startActivity(final Intent p0, final boolean p1);
    
    void startActivity(final Intent p0, final boolean p1, final Callback p2);
    
    void startActivity(final Intent p0, final boolean p1, final boolean p2);
    
    void startActivity(final Intent p0, final boolean p1, final boolean p2, final int p3);
    
    void startPendingIntentDismissingKeyguard(final PendingIntent p0);
    
    void startPendingIntentDismissingKeyguard(final PendingIntent p0, final Runnable p1);
    
    void startPendingIntentDismissingKeyguard(final PendingIntent p0, final Runnable p1, final View p2);
    
    public interface Callback
    {
        void onActivityStarted(final int p0);
    }
    
    public interface OnDismissAction
    {
        boolean onDismiss();
    }
}
