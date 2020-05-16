// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;
import android.util.Log;
import android.text.TextUtils;
import android.content.res.TypedArray;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class ListPreference extends DialogPreference
{
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private String mSummary;
    private String mValue;
    private boolean mValueSet;
    
    public ListPreference(final Context context) {
        this(context, null);
    }
    
    public ListPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.dialogPreferenceStyle, 16842897));
    }
    
    public ListPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ListPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ListPreference, n, n2);
        this.mEntries = TypedArrayUtils.getTextArray(obtainStyledAttributes, R$styleable.ListPreference_entries, R$styleable.ListPreference_android_entries);
        this.mEntryValues = TypedArrayUtils.getTextArray(obtainStyledAttributes, R$styleable.ListPreference_entryValues, R$styleable.ListPreference_android_entryValues);
        final int listPreference_useSimpleSummaryProvider = R$styleable.ListPreference_useSimpleSummaryProvider;
        if (TypedArrayUtils.getBoolean(obtainStyledAttributes, listPreference_useSimpleSummaryProvider, listPreference_useSimpleSummaryProvider, false)) {
            this.setSummaryProvider((SummaryProvider)SimpleSummaryProvider.getInstance());
        }
        obtainStyledAttributes.recycle();
        final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(set, R$styleable.Preference, n, n2);
        this.mSummary = TypedArrayUtils.getString(obtainStyledAttributes2, R$styleable.Preference_summary, R$styleable.Preference_android_summary);
        obtainStyledAttributes2.recycle();
    }
    
    private int getValueIndex() {
        return this.findIndexOfValue(this.mValue);
    }
    
    public int findIndexOfValue(final String s) {
        if (s != null) {
            final CharSequence[] mEntryValues = this.mEntryValues;
            if (mEntryValues != null) {
                for (int i = mEntryValues.length - 1; i >= 0; --i) {
                    if (TextUtils.equals((CharSequence)this.mEntryValues[i].toString(), (CharSequence)s)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    public CharSequence[] getEntries() {
        return this.mEntries;
    }
    
    public CharSequence getEntry() {
        final int valueIndex = this.getValueIndex();
        if (valueIndex >= 0) {
            final CharSequence[] mEntries = this.mEntries;
            if (mEntries != null) {
                return mEntries[valueIndex];
            }
        }
        return null;
    }
    
    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }
    
    @Override
    public CharSequence getSummary() {
        if (this.getSummaryProvider() != null) {
            return this.getSummaryProvider().provideSummary(this);
        }
        final CharSequence entry = this.getEntry();
        final CharSequence summary = super.getSummary();
        final String mSummary = this.mSummary;
        if (mSummary == null) {
            return summary;
        }
        CharSequence charSequence;
        if ((charSequence = entry) == null) {
            charSequence = "";
        }
        final String format = String.format(mSummary, charSequence);
        if (TextUtils.equals((CharSequence)format, summary)) {
            return summary;
        }
        Log.w("ListPreference", "Setting a summary with a String formatting marker is no longer supported. You should use a SummaryProvider instead.");
        return format;
    }
    
    public String getValue() {
        return this.mValue;
    }
    
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, final int n) {
        return typedArray.getString(n);
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable.getClass().equals(SavedState.class)) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.setValue(savedState.mValue);
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (this.isPersistent()) {
            return onSaveInstanceState;
        }
        final SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.mValue = this.getValue();
        return (Parcelable)savedState;
    }
    
    @Override
    protected void onSetInitialValue(final Object o) {
        this.setValue(this.getPersistedString((String)o));
    }
    
    public void setEntries(final CharSequence[] mEntries) {
        this.mEntries = mEntries;
    }
    
    public void setEntryValues(final CharSequence[] mEntryValues) {
        this.mEntryValues = mEntryValues;
    }
    
    @Override
    public void setSummary(final CharSequence summary) {
        super.setSummary(summary);
        if (summary == null) {
            this.mSummary = null;
        }
        else {
            this.mSummary = summary.toString();
        }
    }
    
    public void setValue(final String mValue) {
        final boolean b = TextUtils.equals((CharSequence)this.mValue, (CharSequence)mValue) ^ true;
        if (b || !this.mValueSet) {
            this.mValue = mValue;
            this.mValueSet = true;
            this.persistString(mValue);
            if (b) {
                this.notifyChanged();
            }
        }
    }
    
    private static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        String mValue;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel) {
            super(parcel);
            this.mValue = parcel.readString();
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeString(this.mValue);
        }
    }
    
    public static final class SimpleSummaryProvider implements SummaryProvider<ListPreference>
    {
        private static SimpleSummaryProvider sSimpleSummaryProvider;
        
        private SimpleSummaryProvider() {
        }
        
        public static SimpleSummaryProvider getInstance() {
            if (SimpleSummaryProvider.sSimpleSummaryProvider == null) {
                SimpleSummaryProvider.sSimpleSummaryProvider = new SimpleSummaryProvider();
            }
            return SimpleSummaryProvider.sSimpleSummaryProvider;
        }
        
        public CharSequence provideSummary(final ListPreference listPreference) {
            if (TextUtils.isEmpty(listPreference.getEntry())) {
                return listPreference.getContext().getString(R$string.not_set);
            }
            return listPreference.getEntry();
        }
    }
}
