// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget.picker;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import android.text.TextUtils;
import android.content.res.TypedArray;
import java.util.Calendar;
import android.text.format.DateFormat;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import java.util.Locale;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;

public class TimePicker extends Picker
{
    PickerColumn mAmPmColumn;
    int mColAmPmIndex;
    int mColHourIndex;
    int mColMinuteIndex;
    private final PickerUtility.TimeConstant mConstant;
    private int mCurrentAmPmIndex;
    private int mCurrentHour;
    private int mCurrentMinute;
    PickerColumn mHourColumn;
    private boolean mIs24hFormat;
    PickerColumn mMinuteColumn;
    private String mTimePickerFormat;
    
    public TimePicker(final Context context, final AttributeSet set) {
        this(context, set, R$attr.timePickerStyle);
    }
    
    public TimePicker(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mConstant = PickerUtility.getTimeConstantInstance(Locale.getDefault(), context.getResources());
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbTimePicker);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.lbTimePicker, set, obtainStyledAttributes, 0, 0);
        try {
            this.mIs24hFormat = obtainStyledAttributes.getBoolean(R$styleable.lbTimePicker_is24HourFormat, DateFormat.is24HourFormat(context));
            final boolean boolean1 = obtainStyledAttributes.getBoolean(R$styleable.lbTimePicker_useCurrentTime, true);
            obtainStyledAttributes.recycle();
            this.updateColumns();
            this.updateColumnsRange();
            if (boolean1) {
                final Calendar calendarForLocale = PickerUtility.getCalendarForLocale(null, this.mConstant.locale);
                this.setHour(calendarForLocale.get(11));
                this.setMinute(calendarForLocale.get(12));
                this.setAmPmValue();
            }
        }
        finally {
            obtainStyledAttributes.recycle();
        }
    }
    
    private String extractTimeFields() {
        final String bestHourMinutePattern = this.getBestHourMinutePattern();
        final int layoutDirectionFromLocale = TextUtils.getLayoutDirectionFromLocale(this.mConstant.locale);
        boolean b = false;
        final boolean b2 = layoutDirectionFromLocale == 1;
        if (bestHourMinutePattern.indexOf(97) < 0 || bestHourMinutePattern.indexOf("a") > bestHourMinutePattern.indexOf("m")) {
            b = true;
        }
        String s;
        if (b2) {
            s = "mh";
        }
        else {
            s = "hm";
        }
        if (this.is24Hour()) {
            return s;
        }
        StringBuilder sb;
        if (b) {
            sb = new StringBuilder();
            sb.append(s);
            sb.append("a");
        }
        else {
            sb = new StringBuilder();
            sb.append("a");
            sb.append(s);
        }
        return sb.toString();
    }
    
    private static boolean isAnyOf(final char c, final char[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (c == array[i]) {
                return true;
            }
        }
        return false;
    }
    
    private void setAmPmValue() {
        if (!this.is24Hour()) {
            this.setColumnValue(this.mColAmPmIndex, this.mCurrentAmPmIndex, false);
        }
    }
    
    private void updateColumns() {
        final String bestHourMinutePattern = this.getBestHourMinutePattern();
        if (TextUtils.equals((CharSequence)bestHourMinutePattern, (CharSequence)this.mTimePickerFormat)) {
            return;
        }
        this.mTimePickerFormat = bestHourMinutePattern;
        final String timeFields = this.extractTimeFields();
        final List<CharSequence> separators = this.extractSeparators();
        if (separators.size() == timeFields.length() + 1) {
            this.setSeparators(separators);
            final String upperCase = timeFields.toUpperCase();
            this.mAmPmColumn = null;
            this.mMinuteColumn = null;
            this.mHourColumn = null;
            this.mColAmPmIndex = -1;
            this.mColMinuteIndex = -1;
            this.mColHourIndex = -1;
            final ArrayList<PickerColumn> columns = new ArrayList<PickerColumn>(3);
            for (int i = 0; i < upperCase.length(); ++i) {
                final char char1 = upperCase.charAt(i);
                if (char1 != 'A') {
                    if (char1 != 'H') {
                        if (char1 != 'M') {
                            throw new IllegalArgumentException("Invalid time picker format.");
                        }
                        columns.add(this.mMinuteColumn = new PickerColumn());
                        this.mMinuteColumn.setStaticLabels(this.mConstant.minutes);
                        this.mColMinuteIndex = i;
                    }
                    else {
                        columns.add(this.mHourColumn = new PickerColumn());
                        this.mHourColumn.setStaticLabels(this.mConstant.hours24);
                        this.mColHourIndex = i;
                    }
                }
                else {
                    columns.add(this.mAmPmColumn = new PickerColumn());
                    this.mAmPmColumn.setStaticLabels(this.mConstant.ampm);
                    this.mColAmPmIndex = i;
                    updateMin(this.mAmPmColumn, 0);
                    updateMax(this.mAmPmColumn, 1);
                }
            }
            this.setColumns(columns);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Separators size: ");
        sb.append(separators.size());
        sb.append(" must equal the size of timeFieldsPattern: ");
        sb.append(timeFields.length());
        sb.append(" + 1");
        throw new IllegalStateException(sb.toString());
    }
    
    private void updateColumnsRange() {
        updateMin(this.mHourColumn, (this.mIs24hFormat ^ true) ? 1 : 0);
        final PickerColumn mHourColumn = this.mHourColumn;
        int n;
        if (this.mIs24hFormat) {
            n = 23;
        }
        else {
            n = 12;
        }
        updateMax(mHourColumn, n);
        updateMin(this.mMinuteColumn, 0);
        updateMax(this.mMinuteColumn, 59);
        final PickerColumn mAmPmColumn = this.mAmPmColumn;
        if (mAmPmColumn != null) {
            updateMin(mAmPmColumn, 0);
            updateMax(this.mAmPmColumn, 1);
        }
    }
    
    private static void updateMax(final PickerColumn pickerColumn, final int maxValue) {
        if (maxValue != pickerColumn.getMaxValue()) {
            pickerColumn.setMaxValue(maxValue);
        }
    }
    
    private static void updateMin(final PickerColumn pickerColumn, final int minValue) {
        if (minValue != pickerColumn.getMinValue()) {
            pickerColumn.setMinValue(minValue);
        }
    }
    
    List<CharSequence> extractSeparators() {
        final String bestHourMinutePattern = this.getBestHourMinutePattern();
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<CharSequence>();
        final StringBuilder sb = new StringBuilder();
        char index = '\0';
        char c;
        int n = c = index;
        while (index < bestHourMinutePattern.length()) {
            final char char1 = bestHourMinutePattern.charAt(index);
            if (char1 != ' ') {
                if (char1 == '\'') {
                    if (n == 0) {
                        sb.setLength(0);
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                }
                else {
                    if (n != 0) {
                        sb.append(char1);
                    }
                    else if (isAnyOf(char1, new char[] { 'H', 'h', 'K', 'k', 'm', 'M', 'a' })) {
                        if (char1 != c) {
                            list.add(sb.toString());
                            sb.setLength(0);
                        }
                    }
                    else {
                        sb.append(char1);
                    }
                    c = char1;
                }
            }
            ++index;
        }
        list.add(sb.toString());
        return (List<CharSequence>)list;
    }
    
    String getBestHourMinutePattern() {
        final boolean supports_BEST_DATE_TIME_PATTERN = PickerUtility.SUPPORTS_BEST_DATE_TIME_PATTERN;
        final String s = "h:mma";
        String s3;
        if (supports_BEST_DATE_TIME_PATTERN) {
            final Locale locale = this.mConstant.locale;
            String s2;
            if (this.mIs24hFormat) {
                s2 = "Hma";
            }
            else {
                s2 = "hma";
            }
            s3 = DateFormat.getBestDateTimePattern(locale, s2);
        }
        else {
            final java.text.DateFormat timeInstance = java.text.DateFormat.getTimeInstance(3, this.mConstant.locale);
            if (timeInstance instanceof SimpleDateFormat) {
                s3 = ((SimpleDateFormat)timeInstance).toPattern().replace("s", "");
                if (this.mIs24hFormat) {
                    s3 = s3.replace('h', 'H').replace("a", "");
                }
            }
            else if (this.mIs24hFormat) {
                s3 = "H:mma";
            }
            else {
                s3 = "h:mma";
            }
        }
        if (TextUtils.isEmpty((CharSequence)s3)) {
            s3 = s;
        }
        return s3;
    }
    
    public boolean is24Hour() {
        return this.mIs24hFormat;
    }
    
    @Override
    public void onColumnValueChanged(final int n, final int mCurrentAmPmIndex) {
        if (n == this.mColHourIndex) {
            this.mCurrentHour = mCurrentAmPmIndex;
        }
        else if (n == this.mColMinuteIndex) {
            this.mCurrentMinute = mCurrentAmPmIndex;
        }
        else {
            if (n != this.mColAmPmIndex) {
                throw new IllegalArgumentException("Invalid column index.");
            }
            this.mCurrentAmPmIndex = mCurrentAmPmIndex;
        }
    }
    
    public void setHour(int mCurrentHour) {
        if (mCurrentHour >= 0 && mCurrentHour <= 23) {
            this.mCurrentHour = mCurrentHour;
            if (!this.is24Hour()) {
                mCurrentHour = this.mCurrentHour;
                if (mCurrentHour >= 12) {
                    this.mCurrentAmPmIndex = 1;
                    if (mCurrentHour > 12) {
                        this.mCurrentHour = mCurrentHour - 12;
                    }
                }
                else {
                    this.mCurrentAmPmIndex = 0;
                    if (mCurrentHour == 0) {
                        this.mCurrentHour = 12;
                    }
                }
                this.setAmPmValue();
            }
            this.setColumnValue(this.mColHourIndex, this.mCurrentHour, false);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("hour: ");
        sb.append(mCurrentHour);
        sb.append(" is not in [0-23] range in");
        throw new IllegalArgumentException(sb.toString());
    }
    
    public void setMinute(final int n) {
        if (n >= 0 && n <= 59) {
            this.mCurrentMinute = n;
            this.setColumnValue(this.mColMinuteIndex, n, false);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("minute: ");
        sb.append(n);
        sb.append(" is not in [0-59] range.");
        throw new IllegalArgumentException(sb.toString());
    }
}
