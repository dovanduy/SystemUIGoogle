// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.util.EventLog;

public class EventLogTags
{
    public static void writeSysuiLockscreenGesture(final int i, final int j, final int k) {
        EventLog.writeEvent(36021, new Object[] { i, j, k });
    }
    
    public static void writeSysuiStatusBarState(final int i, final int j, final int k, final int l, final int m, final int i2) {
        EventLog.writeEvent(36004, new Object[] { i, j, k, l, m, i2 });
    }
}
