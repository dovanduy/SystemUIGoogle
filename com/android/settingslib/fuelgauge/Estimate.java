// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.fuelgauge;

import android.content.ContentResolver;
import java.time.temporal.Temporal;
import java.time.Duration;
import java.time.Instant;
import android.provider.Settings$Global;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;

public final class Estimate
{
    public static final Companion Companion;
    private final long averageDischargeTime;
    private final long estimateMillis;
    private final boolean isBasedOnUsage;
    
    static {
        Companion = new Companion(null);
    }
    
    public Estimate(final long estimateMillis, final boolean isBasedOnUsage, final long averageDischargeTime) {
        this.estimateMillis = estimateMillis;
        this.isBasedOnUsage = isBasedOnUsage;
        this.averageDischargeTime = averageDischargeTime;
    }
    
    public static final Estimate getCachedEstimateIfAvailable(final Context context) {
        return Estimate.Companion.getCachedEstimateIfAvailable(context);
    }
    
    public static final void storeCachedEstimate(final Context context, final Estimate estimate) {
        Estimate.Companion.storeCachedEstimate(context, estimate);
    }
    
    public final long getAverageDischargeTime() {
        return this.averageDischargeTime;
    }
    
    public final long getEstimateMillis() {
        return this.estimateMillis;
    }
    
    public final boolean isBasedOnUsage() {
        return this.isBasedOnUsage;
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final Estimate getCachedEstimateIfAvailable(final Context context) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            final ContentResolver contentResolver = context.getContentResolver();
            Estimate estimate;
            if (Duration.between(Instant.ofEpochMilli(Settings$Global.getLong(contentResolver, "battery_estimates_last_update_time", -1L)), Instant.now()).compareTo(Duration.ofMinutes(1L)) > 0) {
                estimate = null;
            }
            else {
                final long n = -1;
                final long long1 = Settings$Global.getLong(contentResolver, "time_remaining_estimate_millis", n);
                boolean b = false;
                if (Settings$Global.getInt(contentResolver, "time_remaining_estimate_based_on_usage", 0) == 1) {
                    b = true;
                }
                estimate = new Estimate(long1, b, Settings$Global.getLong(contentResolver, "average_time_to_discharge", n));
            }
            return estimate;
        }
        
        public final void storeCachedEstimate(final Context context, final Estimate estimate) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(estimate, "estimate");
            final ContentResolver contentResolver = context.getContentResolver();
            Settings$Global.putLong(contentResolver, "time_remaining_estimate_millis", estimate.getEstimateMillis());
            Settings$Global.putInt(contentResolver, "time_remaining_estimate_based_on_usage", (int)(estimate.isBasedOnUsage() ? 1 : 0));
            Settings$Global.putLong(contentResolver, "average_time_to_discharge", estimate.getAverageDischargeTime());
            Settings$Global.putLong(contentResolver, "battery_estimates_last_update_time", System.currentTimeMillis());
        }
    }
}
