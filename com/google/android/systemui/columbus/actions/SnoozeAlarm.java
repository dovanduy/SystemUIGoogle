// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import android.content.Intent;
import kotlin.jvm.internal.Intrinsics;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import android.content.Context;

public final class SnoozeAlarm extends DeskClockAction
{
    public SnoozeAlarm(final Context context, final ColumbusContentObserver.Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, factory);
    }
    
    @Override
    protected Intent createDismissIntent() {
        return new Intent("android.intent.action.SNOOZE_ALARM");
    }
    
    @Override
    protected String getAlertAction() {
        return "com.google.android.deskclock.action.ALARM_ALERT";
    }
    
    @Override
    protected String getDoneAction() {
        return "com.google.android.deskclock.action.ALARM_DONE";
    }
}
