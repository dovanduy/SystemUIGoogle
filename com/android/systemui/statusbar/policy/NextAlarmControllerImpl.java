// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.Intent;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Handler;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.content.Context;
import android.app.AlarmManager$AlarmClockInfo;
import java.util.ArrayList;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;

public class NextAlarmControllerImpl extends BroadcastReceiver implements NextAlarmController
{
    private AlarmManager mAlarmManager;
    private final ArrayList<NextAlarmChangeCallback> mChangeCallbacks;
    private AlarmManager$AlarmClockInfo mNextAlarm;
    
    public NextAlarmControllerImpl(final Context context) {
        this.mChangeCallbacks = new ArrayList<NextAlarmChangeCallback>();
        this.mAlarmManager = (AlarmManager)context.getSystemService("alarm");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        context.registerReceiverAsUser((BroadcastReceiver)this, UserHandle.ALL, intentFilter, (String)null, (Handler)null);
        this.updateNextAlarm();
    }
    
    private void fireNextAlarmChanged() {
        for (int size = this.mChangeCallbacks.size(), i = 0; i < size; ++i) {
            this.mChangeCallbacks.get(i).onNextAlarmChanged(this.mNextAlarm);
        }
    }
    
    private void updateNextAlarm() {
        this.mNextAlarm = this.mAlarmManager.getNextAlarmClock(-2);
        this.fireNextAlarmChanged();
    }
    
    public void addCallback(final NextAlarmChangeCallback e) {
        this.mChangeCallbacks.add(e);
        e.onNextAlarmChanged(this.mNextAlarm);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("NextAlarmController state:");
        printWriter.print("  mNextAlarm=");
        printWriter.println(this.mNextAlarm);
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        if (action.equals("android.intent.action.USER_SWITCHED") || action.equals("android.app.action.NEXT_ALARM_CLOCK_CHANGED")) {
            this.updateNextAlarm();
        }
    }
    
    public void removeCallback(final NextAlarmChangeCallback o) {
        this.mChangeCallbacks.remove(o);
    }
}
