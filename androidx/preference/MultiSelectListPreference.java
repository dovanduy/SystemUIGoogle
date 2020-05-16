// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import java.util.Collections;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import java.util.Collection;
import android.os.Parcelable;
import android.content.res.TypedArray;
import java.util.HashSet;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;
import java.util.Set;

public class MultiSelectListPreference extends DialogPreference
{
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private Set<String> mValues;
    
    public MultiSelectListPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.dialogPreferenceStyle, 16842897));
    }
    
    public MultiSelectListPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public MultiSelectListPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mValues = new HashSet<String>();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.MultiSelectListPreference, n, n2);
        this.mEntries = TypedArrayUtils.getTextArray(obtainStyledAttributes, R$styleable.MultiSelectListPreference_entries, R$styleable.MultiSelectListPreference_android_entries);
        this.mEntryValues = TypedArrayUtils.getTextArray(obtainStyledAttributes, R$styleable.MultiSelectListPreference_entryValues, R$styleable.MultiSelectListPreference_android_entryValues);
        obtainStyledAttributes.recycle();
    }
    
    public CharSequence[] getEntries() {
        return this.mEntries;
    }
    
    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }
    
    public Set<String> getValues() {
        return this.mValues;
    }
    
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, int i) {
        final CharSequence[] textArray = typedArray.getTextArray(i);
        final HashSet<String> set = new HashSet<String>();
        int length;
        for (length = textArray.length, i = 0; i < length; ++i) {
            set.add(textArray[i].toString());
        }
        return set;
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable.getClass().equals(SavedState.class)) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.setValues(savedState.mValues);
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
        savedState.mValues = this.getValues();
        return (Parcelable)savedState;
    }
    
    @Override
    protected void onSetInitialValue(final Object o) {
        this.setValues(this.getPersistedStringSet((Set<String>)o));
    }
    
    public void setValues(final Set<String> set) {
        this.mValues.clear();
        this.mValues.addAll(set);
        this.persistStringSet(set);
        this.notifyChanged();
    }
    
    private static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Set<String> mValues;
        
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
            final int int1 = parcel.readInt();
            this.mValues = new HashSet<String>();
            final String[] elements = new String[int1];
            parcel.readStringArray(elements);
            Collections.addAll(this.mValues, elements);
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.mValues.size());
            final Set<String> mValues = this.mValues;
            parcel.writeStringArray((String[])mValues.toArray(new String[mValues.size()]));
        }
    }
}
