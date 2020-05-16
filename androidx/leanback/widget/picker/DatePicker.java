// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget.picker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import android.util.Log;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import java.text.SimpleDateFormat;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import java.text.DateFormat;
import java.util.Calendar;

public class DatePicker extends Picker
{
    private static final int[] DATE_FIELDS;
    private int mColDayIndex;
    private int mColMonthIndex;
    private int mColYearIndex;
    private PickerUtility.DateConstant mConstant;
    private Calendar mCurrentDate;
    private final DateFormat mDateFormat;
    private String mDatePickerFormat;
    private PickerColumn mDayColumn;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private PickerColumn mMonthColumn;
    private Calendar mTempDate;
    private PickerColumn mYearColumn;
    
    static {
        DATE_FIELDS = new int[] { 5, 2, 1 };
    }
    
    public DatePicker(final Context context, final AttributeSet set) {
        this(context, set, R$attr.datePickerStyle);
    }
    
    public DatePicker(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        this.updateCurrentLocale();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbDatePicker);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.lbDatePicker, set, obtainStyledAttributes, 0, 0);
        try {
            final String string = obtainStyledAttributes.getString(R$styleable.lbDatePicker_android_minDate);
            final String string2 = obtainStyledAttributes.getString(R$styleable.lbDatePicker_android_maxDate);
            final String string3 = obtainStyledAttributes.getString(R$styleable.lbDatePicker_datePickerFormat);
            obtainStyledAttributes.recycle();
            this.mTempDate.clear();
            if (!TextUtils.isEmpty((CharSequence)string)) {
                if (!this.parseDate(string, this.mTempDate)) {
                    this.mTempDate.set(1900, 0, 1);
                }
            }
            else {
                this.mTempDate.set(1900, 0, 1);
            }
            this.mMinDate.setTimeInMillis(this.mTempDate.getTimeInMillis());
            this.mTempDate.clear();
            if (!TextUtils.isEmpty((CharSequence)string2)) {
                if (!this.parseDate(string2, this.mTempDate)) {
                    this.mTempDate.set(2100, 0, 1);
                }
            }
            else {
                this.mTempDate.set(2100, 0, 1);
            }
            this.mMaxDate.setTimeInMillis(this.mTempDate.getTimeInMillis());
            String datePickerFormat = string3;
            if (TextUtils.isEmpty((CharSequence)string3)) {
                datePickerFormat = new String(android.text.format.DateFormat.getDateFormatOrder(context));
            }
            this.setDatePickerFormat(datePickerFormat);
        }
        finally {
            obtainStyledAttributes.recycle();
        }
    }
    
    private static boolean isAnyOf(final char c, final char[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (c == array[i]) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isNewDate(final int n, final int n2, final int n3) {
        final Calendar mCurrentDate = this.mCurrentDate;
        boolean b2;
        final boolean b = b2 = true;
        if (mCurrentDate.get(1) == n) {
            b2 = b;
            if (this.mCurrentDate.get(2) == n3) {
                b2 = (this.mCurrentDate.get(5) != n2 && b);
            }
        }
        return b2;
    }
    
    private boolean parseDate(final String s, final Calendar calendar) {
        try {
            calendar.setTime(this.mDateFormat.parse(s));
            return true;
        }
        catch (ParseException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Date: ");
            sb.append(s);
            sb.append(" not in format: ");
            sb.append("MM/dd/yyyy");
            Log.w("DatePicker", sb.toString());
            return false;
        }
    }
    
    private void setDate(final int n, final int n2, final int n3) {
        this.setDate(n, n2, n3, false);
    }
    
    private void updateCurrentLocale() {
        final PickerUtility.DateConstant dateConstantInstance = PickerUtility.getDateConstantInstance(Locale.getDefault(), this.getContext().getResources());
        this.mConstant = dateConstantInstance;
        this.mTempDate = PickerUtility.getCalendarForLocale(this.mTempDate, dateConstantInstance.locale);
        this.mMinDate = PickerUtility.getCalendarForLocale(this.mMinDate, this.mConstant.locale);
        this.mMaxDate = PickerUtility.getCalendarForLocale(this.mMaxDate, this.mConstant.locale);
        this.mCurrentDate = PickerUtility.getCalendarForLocale(this.mCurrentDate, this.mConstant.locale);
        final PickerColumn mMonthColumn = this.mMonthColumn;
        if (mMonthColumn != null) {
            mMonthColumn.setStaticLabels(this.mConstant.months);
            this.setColumnAt(this.mColMonthIndex, this.mMonthColumn);
        }
    }
    
    private static boolean updateMax(final PickerColumn pickerColumn, final int maxValue) {
        if (maxValue != pickerColumn.getMaxValue()) {
            pickerColumn.setMaxValue(maxValue);
            return true;
        }
        return false;
    }
    
    private static boolean updateMin(final PickerColumn pickerColumn, final int minValue) {
        if (minValue != pickerColumn.getMinValue()) {
            pickerColumn.setMinValue(minValue);
            return true;
        }
        return false;
    }
    
    private void updateSpinners(final boolean b) {
        this.post((Runnable)new Runnable() {
            @Override
            public void run() {
                DatePicker.this.updateSpinnersImpl(b);
            }
        });
    }
    
    List<CharSequence> extractSeparators() {
        final String bestYearMonthDayPattern = this.getBestYearMonthDayPattern(this.mDatePickerFormat);
        final ArrayList<String> list = (ArrayList<String>)new ArrayList<CharSequence>();
        final StringBuilder sb = new StringBuilder();
        char index = '\0';
        char c;
        int n = c = index;
        while (index < bestYearMonthDayPattern.length()) {
            final char char1 = bestYearMonthDayPattern.charAt(index);
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
                    else if (isAnyOf(char1, new char[] { 'Y', 'y', 'M', 'm', 'D', 'd' })) {
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
    
    String getBestYearMonthDayPattern(String s) {
        final boolean supports_BEST_DATE_TIME_PATTERN = PickerUtility.SUPPORTS_BEST_DATE_TIME_PATTERN;
        final String s2 = "MM/dd/yyyy";
        if (supports_BEST_DATE_TIME_PATTERN) {
            s = android.text.format.DateFormat.getBestDateTimePattern(this.mConstant.locale, s);
        }
        else {
            final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this.getContext());
            if (dateFormat instanceof SimpleDateFormat) {
                s = ((SimpleDateFormat)dateFormat).toLocalizedPattern();
            }
            else {
                s = "MM/dd/yyyy";
            }
        }
        if (TextUtils.isEmpty((CharSequence)s)) {
            s = s2;
        }
        return s;
    }
    
    @Override
    public final void onColumnValueChanged(final int n, final int n2) {
        this.mTempDate.setTimeInMillis(this.mCurrentDate.getTimeInMillis());
        final int currentValue = this.getColumnAt(n).getCurrentValue();
        if (n == this.mColDayIndex) {
            this.mTempDate.add(5, n2 - currentValue);
        }
        else if (n == this.mColMonthIndex) {
            this.mTempDate.add(2, n2 - currentValue);
        }
        else {
            if (n != this.mColYearIndex) {
                throw new IllegalArgumentException();
            }
            this.mTempDate.add(1, n2 - currentValue);
        }
        this.setDate(this.mTempDate.get(1), this.mTempDate.get(2), this.mTempDate.get(5));
    }
    
    public void setDate(final int year, final int month, final int date, final boolean b) {
        if (!this.isNewDate(year, month, date)) {
            return;
        }
        this.mCurrentDate.set(year, month, date);
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
        }
        else if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
        }
        this.updateSpinners(b);
    }
    
    public void setDatePickerFormat(String upperCase) {
        String mDatePickerFormat = upperCase;
        if (TextUtils.isEmpty((CharSequence)upperCase)) {
            mDatePickerFormat = new String(android.text.format.DateFormat.getDateFormatOrder(this.getContext()));
        }
        if (TextUtils.equals((CharSequence)this.mDatePickerFormat, (CharSequence)mDatePickerFormat)) {
            return;
        }
        this.mDatePickerFormat = mDatePickerFormat;
        final List<CharSequence> separators = this.extractSeparators();
        if (separators.size() == mDatePickerFormat.length() + 1) {
            this.setSeparators(separators);
            this.mDayColumn = null;
            this.mMonthColumn = null;
            this.mYearColumn = null;
            this.mColMonthIndex = -1;
            this.mColDayIndex = -1;
            this.mColYearIndex = -1;
            upperCase = mDatePickerFormat.toUpperCase();
            final ArrayList<PickerColumn> columns = new ArrayList<PickerColumn>(3);
            for (int i = 0; i < upperCase.length(); ++i) {
                final char char1 = upperCase.charAt(i);
                if (char1 != 'D') {
                    if (char1 != 'M') {
                        if (char1 != 'Y') {
                            throw new IllegalArgumentException("datePicker format error");
                        }
                        if (this.mYearColumn != null) {
                            throw new IllegalArgumentException("datePicker format error");
                        }
                        columns.add(this.mYearColumn = new PickerColumn());
                        this.mColYearIndex = i;
                        this.mYearColumn.setLabelFormat("%d");
                    }
                    else {
                        if (this.mMonthColumn != null) {
                            throw new IllegalArgumentException("datePicker format error");
                        }
                        columns.add(this.mMonthColumn = new PickerColumn());
                        this.mMonthColumn.setStaticLabels(this.mConstant.months);
                        this.mColMonthIndex = i;
                    }
                }
                else {
                    if (this.mDayColumn != null) {
                        throw new IllegalArgumentException("datePicker format error");
                    }
                    columns.add(this.mDayColumn = new PickerColumn());
                    this.mDayColumn.setLabelFormat("%02d");
                    this.mColDayIndex = i;
                }
            }
            this.setColumns(columns);
            this.updateSpinners(false);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Separators size: ");
        sb.append(separators.size());
        sb.append(" must equal the size of datePickerFormat: ");
        sb.append(mDatePickerFormat.length());
        sb.append(" + 1");
        throw new IllegalStateException(sb.toString());
    }
    
    void updateSpinnersImpl(final boolean b) {
        final int[] date_FIELDS = DatePicker.DATE_FIELDS;
        final int[] array = { this.mColDayIndex, this.mColMonthIndex, this.mColYearIndex };
        int i = date_FIELDS.length - 1;
        boolean b3;
        boolean b2 = b3 = true;
        while (i >= 0) {
            if (array[i] >= 0) {
                final int field = date_FIELDS[i];
                final PickerColumn column = this.getColumnAt(array[i]);
                boolean b4;
                if (b2) {
                    b4 = updateMin(column, this.mMinDate.get(field));
                }
                else {
                    b4 = updateMin(column, this.mCurrentDate.getActualMinimum(field));
                }
                boolean b5;
                if (b3) {
                    b5 = updateMax(column, this.mMaxDate.get(field));
                }
                else {
                    b5 = updateMax(column, this.mCurrentDate.getActualMaximum(field));
                }
                final boolean b6 = b2 & this.mCurrentDate.get(field) == this.mMinDate.get(field);
                b3 &= (this.mCurrentDate.get(field) == this.mMaxDate.get(field));
                if (b4 | false | b5) {
                    this.setColumnAt(array[i], column);
                }
                this.setColumnValue(array[i], this.mCurrentDate.get(field), b);
                b2 = b6;
            }
            --i;
        }
    }
}
