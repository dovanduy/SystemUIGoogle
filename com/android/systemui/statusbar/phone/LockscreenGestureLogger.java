// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.EventLogTags;
import android.metrics.LogMaker;
import com.android.systemui.EventLogConstants;
import com.android.systemui.Dependency;
import com.android.internal.logging.MetricsLogger;
import android.util.ArrayMap;

public class LockscreenGestureLogger
{
    private ArrayMap<Integer, Integer> mLegacyMap;
    private final MetricsLogger mMetricsLogger;
    
    public LockscreenGestureLogger() {
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mLegacyMap = (ArrayMap<Integer, Integer>)new ArrayMap(EventLogConstants.METRICS_GESTURE_TYPE_MAP.length);
        int i = 0;
        while (true) {
            final int[] metrics_GESTURE_TYPE_MAP = EventLogConstants.METRICS_GESTURE_TYPE_MAP;
            if (i >= metrics_GESTURE_TYPE_MAP.length) {
                break;
            }
            this.mLegacyMap.put((Object)metrics_GESTURE_TYPE_MAP[i], (Object)i);
            ++i;
        }
    }
    
    private int safeLookup(final int i) {
        final Integer n = (Integer)this.mLegacyMap.get((Object)i);
        if (n == null) {
            return 0;
        }
        return n;
    }
    
    public void write(final int n, final int i, final int j) {
        this.mMetricsLogger.write(new LogMaker(n).setType(4).addTaggedData(826, (Object)i).addTaggedData(827, (Object)j));
        EventLogTags.writeSysuiLockscreenGesture(this.safeLookup(n), i, j);
    }
    
    public void writeAtFractionalPosition(final int n, final int i, final int j, final int k) {
        this.mMetricsLogger.write(new LogMaker(n).setType(4).addTaggedData(1326, (Object)i).addTaggedData(1327, (Object)j).addTaggedData(1329, (Object)k));
    }
}
