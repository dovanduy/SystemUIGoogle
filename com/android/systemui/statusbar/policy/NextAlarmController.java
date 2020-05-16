// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.app.AlarmManager$AlarmClockInfo;
import com.android.systemui.Dumpable;

public interface NextAlarmController extends CallbackController<NextAlarmChangeCallback>, Dumpable
{
    public interface NextAlarmChangeCallback
    {
        void onNextAlarmChanged(final AlarmManager$AlarmClockInfo p0);
    }
}
