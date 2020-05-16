// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.utils;

import android.icu.util.TimeUnit;
import android.text.style.TtsSpan$MeasureBuilder;
import android.icu.text.MeasureFormat;
import android.icu.text.MeasureFormat$FormatWidth;
import android.icu.util.MeasureUnit;
import android.icu.util.Measure;
import java.util.ArrayList;
import android.text.SpannableStringBuilder;
import android.content.Context;

public class StringUtil
{
    public static CharSequence formatElapsedTime(final Context context, final double n, final boolean b) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int n3;
        final int n2 = n3 = (int)Math.floor(n / 1000.0);
        if (!b) {
            n3 = n2 + 30;
        }
        int i;
        if (n3 >= 86400) {
            i = n3 / 86400;
            n3 -= 86400 * i;
        }
        else {
            i = 0;
        }
        int j;
        if (n3 >= 3600) {
            j = n3 / 3600;
            n3 -= j * 3600;
        }
        else {
            j = 0;
        }
        int k;
        int l;
        if (n3 >= 60) {
            final int n4 = n3 / 60;
            final int n5 = n3 - n4 * 60;
            k = n4;
            l = n5;
        }
        else {
            final int n6 = 0;
            l = n3;
            k = n6;
        }
        final ArrayList<Measure> list = new ArrayList<Measure>(4);
        if (i > 0) {
            list.add(new Measure((Number)i, (MeasureUnit)MeasureUnit.DAY));
        }
        if (j > 0) {
            list.add(new Measure((Number)j, (MeasureUnit)MeasureUnit.HOUR));
        }
        if (k > 0) {
            list.add(new Measure((Number)k, (MeasureUnit)MeasureUnit.MINUTE));
        }
        if (b && l > 0) {
            list.add(new Measure((Number)l, (MeasureUnit)MeasureUnit.SECOND));
        }
        if (list.size() == 0) {
            TimeUnit timeUnit;
            if (b) {
                timeUnit = MeasureUnit.SECOND;
            }
            else {
                timeUnit = MeasureUnit.MINUTE;
            }
            list.add(new Measure((Number)0, (MeasureUnit)timeUnit));
        }
        final Measure[] array = list.toArray(new Measure[list.size()]);
        spannableStringBuilder.append((CharSequence)MeasureFormat.getInstance(context.getResources().getConfiguration().locale, MeasureFormat$FormatWidth.SHORT).formatMeasures(array));
        if (array.length == 1 && MeasureUnit.MINUTE.equals((Object)array[0].getUnit())) {
            spannableStringBuilder.setSpan((Object)new TtsSpan$MeasureBuilder().setNumber((long)k).setUnit("minute").build(), 0, spannableStringBuilder.length(), 33);
        }
        return (CharSequence)spannableStringBuilder;
    }
}
