// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log.dagger;

import com.android.systemui.log.LogcatEchoTrackerProd;
import com.android.systemui.log.LogcatEchoTrackerDebug;
import android.os.Build;
import android.os.Looper;
import android.content.ContentResolver;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.log.LogcatEchoTracker;

public class LogModule
{
    public static LogBuffer provideDozeLogBuffer(final LogcatEchoTracker logcatEchoTracker, final DumpManager dumpManager) {
        final LogBuffer logBuffer = new LogBuffer("DozeLog", 100, 10, logcatEchoTracker);
        logBuffer.attach(dumpManager);
        return logBuffer;
    }
    
    public static LogcatEchoTracker provideLogcatEchoTracker(final ContentResolver contentResolver, final Looper looper) {
        if (Build.IS_DEBUGGABLE) {
            return LogcatEchoTrackerDebug.create(contentResolver, looper);
        }
        return new LogcatEchoTrackerProd();
    }
    
    public static LogBuffer provideNotificationsLogBuffer(final LogcatEchoTracker logcatEchoTracker, final DumpManager dumpManager) {
        final LogBuffer logBuffer = new LogBuffer("NotifLog", 1000, 10, logcatEchoTracker);
        logBuffer.attach(dumpManager);
        return logBuffer;
    }
    
    public static LogBuffer provideQuickSettingsLogBuffer(final LogcatEchoTracker logcatEchoTracker, final DumpManager dumpManager) {
        final LogBuffer logBuffer = new LogBuffer("QSLog", 500, 10, logcatEchoTracker);
        logBuffer.attach(dumpManager);
        return logBuffer;
    }
}
