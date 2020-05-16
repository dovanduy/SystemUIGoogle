// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.os.SystemClock;
import android.os.Handler;
import android.app.AlarmManager;
import android.app.AlarmManager$OnAlarmListener;

public class AlarmTimeout implements AlarmManager$OnAlarmListener
{
    private final AlarmManager mAlarmManager;
    private final Handler mHandler;
    private final AlarmManager$OnAlarmListener mListener;
    private boolean mScheduled;
    private final String mTag;
    
    public AlarmTimeout(final AlarmManager mAlarmManager, final AlarmManager$OnAlarmListener mListener, final String mTag, final Handler mHandler) {
        this.mAlarmManager = mAlarmManager;
        this.mListener = mListener;
        this.mTag = mTag;
        this.mHandler = mHandler;
    }
    
    public void cancel() {
        if (this.mScheduled) {
            this.mAlarmManager.cancel((AlarmManager$OnAlarmListener)this);
            this.mScheduled = false;
        }
    }
    
    public boolean isScheduled() {
        return this.mScheduled;
    }
    
    public void onAlarm() {
        if (!this.mScheduled) {
            return;
        }
        this.mScheduled = false;
        this.mListener.onAlarm();
    }
    
    public boolean schedule(final long n, final int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Illegal mode: ");
                    sb.append(i);
                    throw new IllegalArgumentException(sb.toString());
                }
                if (this.mScheduled) {
                    this.cancel();
                }
            }
            else if (this.mScheduled) {
                return false;
            }
        }
        else if (this.mScheduled) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(this.mTag);
            sb2.append(" timeout is already scheduled");
            throw new IllegalStateException(sb2.toString());
        }
        this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + n, this.mTag, (AlarmManager$OnAlarmListener)this, this.mHandler);
        return this.mScheduled = true;
    }
}
