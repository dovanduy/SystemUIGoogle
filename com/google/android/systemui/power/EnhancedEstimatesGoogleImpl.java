// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.power;

import android.provider.Settings$Global;
import android.content.pm.PackageManager$NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.android.settingslib.utils.PowerUtil;
import java.time.Duration;
import android.net.Uri$Builder;
import com.android.settingslib.fuelgauge.Estimate;
import android.util.KeyValueListParser;
import android.content.Context;
import com.android.systemui.power.EnhancedEstimates;

public class EnhancedEstimatesGoogleImpl implements EnhancedEstimates
{
    private Context mContext;
    private final KeyValueListParser mParser;
    
    public EnhancedEstimatesGoogleImpl(final Context mContext) {
        this.mContext = mContext;
        this.mParser = new KeyValueListParser(',');
    }
    
    @Override
    public Estimate getEstimate() {
        final Uri build = new Uri$Builder().scheme("content").authority("com.google.android.apps.turbo.estimated_time_remaining").appendPath("time_remaining").build();
        try {
            final Cursor query = this.mContext.getContentResolver().query(build, (String[])null, (String)null, (String[])null, (String)null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        final int columnIndex = query.getColumnIndex("is_based_on_usage");
                        boolean b = true;
                        if (columnIndex != -1) {
                            b = (query.getInt(query.getColumnIndex("is_based_on_usage")) != 0 && b);
                        }
                        final int columnIndex2 = query.getColumnIndex("average_battery_life");
                        long roundTimeToNearestThreshold = -1L;
                        if (columnIndex2 != -1) {
                            final long long1 = query.getLong(columnIndex2);
                            roundTimeToNearestThreshold = roundTimeToNearestThreshold;
                            if (long1 != -1L) {
                                long n = Duration.ofMinutes(15L).toMillis();
                                if (Duration.ofMillis(long1).compareTo(Duration.ofDays(1L)) >= 0) {
                                    n = Duration.ofHours(1L).toMillis();
                                }
                                roundTimeToNearestThreshold = PowerUtil.roundTimeToNearestThreshold(long1, n);
                            }
                        }
                        final Estimate estimate = new Estimate(query.getLong(query.getColumnIndex("battery_estimate")), b, roundTimeToNearestThreshold);
                        if (query != null) {
                            query.close();
                        }
                        return estimate;
                    }
                }
                finally {
                    if (query != null) {
                        try {
                            query.close();
                        }
                        finally {
                            final Throwable t;
                            t.addSuppressed((Throwable)query);
                        }
                    }
                }
            }
            if (query != null) {
                query.close();
            }
        }
        catch (Exception ex) {
            Log.d("EnhancedEstimates", "Something went wrong when getting an estimate from Turbo", (Throwable)ex);
        }
        return null;
    }
    
    @Override
    public boolean getLowWarningEnabled() {
        this.updateFlags();
        return this.mParser.getBoolean("low_warning_enabled", false);
    }
    
    @Override
    public long getLowWarningThreshold() {
        this.updateFlags();
        return this.mParser.getLong("low_threshold", Duration.ofHours(3L).toMillis());
    }
    
    @Override
    public long getSevereWarningThreshold() {
        this.updateFlags();
        return this.mParser.getLong("severe_threshold", Duration.ofHours(1L).toMillis());
    }
    
    @Override
    public boolean isHybridNotificationEnabled() {
        try {
            if (!this.mContext.getPackageManager().getPackageInfo("com.google.android.apps.turbo", 512).applicationInfo.enabled) {
                return false;
            }
            this.updateFlags();
            return this.mParser.getBoolean("hybrid_enabled", true);
        }
        catch (PackageManager$NameNotFoundException ex) {
            return false;
        }
    }
    
    protected void updateFlags() {
        final String string = Settings$Global.getString(this.mContext.getContentResolver(), "hybrid_sysui_battery_warning_flags");
        try {
            this.mParser.setString(string);
        }
        catch (IllegalArgumentException ex) {
            Log.e("EnhancedEstimates", "Bad hybrid sysui warning flags");
        }
    }
}
