// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.utils;

import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.icu.text.MeasureFormat;
import android.icu.text.MeasureFormat$FormatWidth;
import android.text.TextUtils;
import java.util.Date;
import java.time.Instant;
import android.text.format.DateFormat;
import com.android.settingslib.R$string;
import android.content.Context;
import java.util.concurrent.TimeUnit;

public class PowerUtil
{
    private static final long FIFTEEN_MINUTES_MILLIS;
    private static final long ONE_DAY_MILLIS;
    private static final long ONE_HOUR_MILLIS;
    private static final long SEVEN_MINUTES_MILLIS;
    private static final long TWO_DAYS_MILLIS;
    
    static {
        SEVEN_MINUTES_MILLIS = TimeUnit.MINUTES.toMillis(7L);
        FIFTEEN_MINUTES_MILLIS = TimeUnit.MINUTES.toMillis(15L);
        ONE_DAY_MILLIS = TimeUnit.DAYS.toMillis(1L);
        TWO_DAYS_MILLIS = TimeUnit.DAYS.toMillis(2L);
        ONE_HOUR_MILLIS = TimeUnit.HOURS.toMillis(1L);
    }
    
    public static String getBatteryRemainingShortStringFormatted(final Context context, final long n) {
        if (n <= 0L) {
            return null;
        }
        if (n <= PowerUtil.ONE_DAY_MILLIS) {
            return getRegularTimeRemainingShortString(context, n);
        }
        return getMoreThanOneDayShortString(context, n, R$string.power_remaining_duration_only_short);
    }
    
    public static String getBatteryRemainingStringFormatted(final Context context, final long n, final String s, final boolean b) {
        if (n <= 0L) {
            return null;
        }
        if (n <= PowerUtil.SEVEN_MINUTES_MILLIS) {
            return getShutdownImminentString(context, s);
        }
        final long fifteen_MINUTES_MILLIS = PowerUtil.FIFTEEN_MINUTES_MILLIS;
        if (n <= fifteen_MINUTES_MILLIS) {
            return getUnderFifteenString(context, StringUtil.formatElapsedTime(context, (double)fifteen_MINUTES_MILLIS, false), s);
        }
        if (n >= PowerUtil.TWO_DAYS_MILLIS) {
            return getMoreThanTwoDaysString(context, s);
        }
        if (n >= PowerUtil.ONE_DAY_MILLIS) {
            return getMoreThanOneDayString(context, n, s, b);
        }
        return getRegularTimeRemainingString(context, n, s, b);
    }
    
    private static CharSequence getDateTimeStringFromMs(final Context context, long roundTimeToNearestThreshold) {
        roundTimeToNearestThreshold = roundTimeToNearestThreshold(System.currentTimeMillis() + roundTimeToNearestThreshold, PowerUtil.FIFTEEN_MINUTES_MILLIS);
        return android.icu.text.DateFormat.getInstanceForSkeleton(DateFormat.getTimeFormatString(context)).format(Date.from(Instant.ofEpochMilli(roundTimeToNearestThreshold)));
    }
    
    private static String getMoreThanOneDayShortString(final Context context, final long n, final int n2) {
        return context.getString(n2, new Object[] { StringUtil.formatElapsedTime(context, (double)roundTimeToNearestThreshold(n, PowerUtil.ONE_HOUR_MILLIS), false) });
    }
    
    private static String getMoreThanOneDayString(final Context context, final long n, final String s, final boolean b) {
        final CharSequence formatElapsedTime = StringUtil.formatElapsedTime(context, (double)roundTimeToNearestThreshold(n, PowerUtil.ONE_HOUR_MILLIS), false);
        if (TextUtils.isEmpty((CharSequence)s)) {
            int n2;
            if (b) {
                n2 = R$string.power_remaining_duration_only_enhanced;
            }
            else {
                n2 = R$string.power_remaining_duration_only;
            }
            return context.getString(n2, new Object[] { formatElapsedTime });
        }
        int n3;
        if (b) {
            n3 = R$string.power_discharging_duration_enhanced;
        }
        else {
            n3 = R$string.power_discharging_duration;
        }
        return context.getString(n3, new Object[] { formatElapsedTime, s });
    }
    
    private static String getMoreThanTwoDaysString(final Context context, final String s) {
        final MeasureFormat instance = MeasureFormat.getInstance(context.getResources().getConfiguration().getLocales().get(0), MeasureFormat$FormatWidth.SHORT);
        final Measure measure = new Measure((Number)2, (MeasureUnit)MeasureUnit.DAY);
        String s2;
        if (TextUtils.isEmpty((CharSequence)s)) {
            s2 = context.getString(R$string.power_remaining_only_more_than_subtext, new Object[] { instance.formatMeasures(new Measure[] { measure }) });
        }
        else {
            s2 = context.getString(R$string.power_remaining_more_than_subtext, new Object[] { instance.formatMeasures(new Measure[] { measure }), s });
        }
        return s2;
    }
    
    private static String getRegularTimeRemainingShortString(final Context context, long roundTimeToNearestThreshold) {
        roundTimeToNearestThreshold = roundTimeToNearestThreshold(System.currentTimeMillis() + roundTimeToNearestThreshold, PowerUtil.FIFTEEN_MINUTES_MILLIS);
        return context.getString(R$string.power_discharge_by_only_short, new Object[] { android.icu.text.DateFormat.getInstanceForSkeleton(DateFormat.getTimeFormatString(context)).format(Date.from(Instant.ofEpochMilli(roundTimeToNearestThreshold))) });
    }
    
    private static String getRegularTimeRemainingString(final Context context, final long n, final String s, final boolean b) {
        final CharSequence dateTimeStringFromMs = getDateTimeStringFromMs(context, n);
        if (TextUtils.isEmpty((CharSequence)s)) {
            int n2;
            if (b) {
                n2 = R$string.power_discharge_by_only_enhanced;
            }
            else {
                n2 = R$string.power_discharge_by_only;
            }
            return context.getString(n2, new Object[] { dateTimeStringFromMs });
        }
        int n3;
        if (b) {
            n3 = R$string.power_discharge_by_enhanced;
        }
        else {
            n3 = R$string.power_discharge_by;
        }
        return context.getString(n3, new Object[] { dateTimeStringFromMs, s });
    }
    
    private static String getShutdownImminentString(final Context context, final String s) {
        String s2;
        if (TextUtils.isEmpty((CharSequence)s)) {
            s2 = context.getString(R$string.power_remaining_duration_only_shutdown_imminent);
        }
        else {
            s2 = context.getString(R$string.power_remaining_duration_shutdown_imminent, new Object[] { s });
        }
        return s2;
    }
    
    private static String getUnderFifteenString(final Context context, final CharSequence charSequence, final String s) {
        String s2;
        if (TextUtils.isEmpty((CharSequence)s)) {
            s2 = context.getString(R$string.power_remaining_less_than_duration_only, new Object[] { charSequence });
        }
        else {
            s2 = context.getString(R$string.power_remaining_less_than_duration, new Object[] { charSequence, s });
        }
        return s2;
    }
    
    public static long roundTimeToNearestThreshold(long abs, long abs2) {
        abs = Math.abs(abs);
        abs2 = Math.abs(abs2);
        final long n = abs % abs2;
        if (n < abs2 / 2L) {
            return abs - n;
        }
        return abs - n + abs2;
    }
}
