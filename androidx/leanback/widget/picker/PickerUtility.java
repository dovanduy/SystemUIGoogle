// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget.picker;

import java.text.DateFormatSymbols;
import android.content.res.Resources;
import java.util.Locale;
import java.util.Calendar;
import android.os.Build$VERSION;

class PickerUtility
{
    static final boolean SUPPORTS_BEST_DATE_TIME_PATTERN;
    
    static {
        SUPPORTS_BEST_DATE_TIME_PATTERN = (Build$VERSION.SDK_INT >= 18);
    }
    
    public static String[] createStringIntArrays(final int n, final int n2, final String format) {
        final String[] array = new String[n2 - n + 1];
        for (int i = n; i <= n2; ++i) {
            if (format != null) {
                array[i - n] = String.format(format, i);
            }
            else {
                array[i - n] = String.valueOf(i);
            }
        }
        return array;
    }
    
    public static Calendar getCalendarForLocale(Calendar instance, final Locale locale) {
        if (instance == null) {
            return Calendar.getInstance(locale);
        }
        final long timeInMillis = instance.getTimeInMillis();
        instance = Calendar.getInstance(locale);
        instance.setTimeInMillis(timeInMillis);
        return instance;
    }
    
    public static DateConstant getDateConstantInstance(final Locale locale, final Resources resources) {
        return new DateConstant(locale, resources);
    }
    
    public static TimeConstant getTimeConstantInstance(final Locale locale, final Resources resources) {
        return new TimeConstant(locale, resources);
    }
    
    public static class DateConstant
    {
        public final Locale locale;
        public final String[] months;
        
        DateConstant(final Locale aLocale, final Resources resources) {
            this.locale = aLocale;
            this.months = DateFormatSymbols.getInstance(aLocale).getShortMonths();
            final Calendar instance = Calendar.getInstance(aLocale);
            PickerUtility.createStringIntArrays(instance.getMinimum(5), instance.getMaximum(5), "%02d");
        }
    }
    
    public static class TimeConstant
    {
        public final String[] ampm;
        public final String[] hours24;
        public final Locale locale;
        public final String[] minutes;
        
        TimeConstant(final Locale locale, final Resources resources) {
            this.locale = locale;
            final DateFormatSymbols instance = DateFormatSymbols.getInstance(locale);
            PickerUtility.createStringIntArrays(1, 12, "%02d");
            this.hours24 = PickerUtility.createStringIntArrays(0, 23, "%02d");
            this.minutes = PickerUtility.createStringIntArrays(0, 59, "%02d");
            this.ampm = instance.getAmPmStrings();
        }
    }
}
