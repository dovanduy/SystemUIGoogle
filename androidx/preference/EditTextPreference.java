// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.text.TextUtils;
import android.os.Parcelable;
import android.content.res.TypedArray;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class EditTextPreference extends DialogPreference
{
    private String mText;
    
    public EditTextPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.editTextPreferenceStyle, 16842898));
    }
    
    public EditTextPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public EditTextPreference(final Context context, final AttributeSet set, int editTextPreference_useSimpleSummaryProvider, final int n) {
        super(context, set, editTextPreference_useSimpleSummaryProvider, n);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.EditTextPreference, editTextPreference_useSimpleSummaryProvider, n);
        editTextPreference_useSimpleSummaryProvider = R$styleable.EditTextPreference_useSimpleSummaryProvider;
        if (TypedArrayUtils.getBoolean(obtainStyledAttributes, editTextPreference_useSimpleSummaryProvider, editTextPreference_useSimpleSummaryProvider, false)) {
            this.setSummaryProvider((SummaryProvider)SimpleSummaryProvider.getInstance());
        }
        obtainStyledAttributes.recycle();
    }
    
    public String getText() {
        return this.mText;
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
            this.setText(savedState.mText);
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
        savedState.mText = this.getText();
        return (Parcelable)savedState;
    }
    
    @Override
    protected void onSetInitialValue(final Object o) {
        this.setText(this.getPersistedString((String)o));
    }
    
    public void setText(final String mText) {
        final boolean shouldDisableDependents = this.shouldDisableDependents();
        this.persistString(this.mText = mText);
        final boolean shouldDisableDependents2 = this.shouldDisableDependents();
        if (shouldDisableDependents2 != shouldDisableDependents) {
            this.notifyDependencyChange(shouldDisableDependents2);
        }
        this.notifyChanged();
    }
    
    @Override
    public boolean shouldDisableDependents() {
        return TextUtils.isEmpty((CharSequence)this.mText) || super.shouldDisableDependents();
    }
    
    private static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        String mText;
        
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
            this.mText = parcel.readString();
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeString(this.mText);
        }
    }
    
    public static final class SimpleSummaryProvider implements SummaryProvider<EditTextPreference>
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
        
        public CharSequence provideSummary(final EditTextPreference editTextPreference) {
            if (TextUtils.isEmpty((CharSequence)editTextPreference.getText())) {
                return editTextPreference.getContext().getString(R$string.not_set);
            }
            return editTextPreference.getText();
        }
    }
}
