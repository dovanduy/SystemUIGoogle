// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.text.TextUtils;
import android.widget.TextView;
import android.view.View;
import android.os.Parcelable;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.content.Context;

public abstract class TwoStatePreference extends Preference
{
    protected boolean mChecked;
    private boolean mCheckedSet;
    private boolean mDisableDependentsState;
    private CharSequence mSummaryOff;
    private CharSequence mSummaryOn;
    
    public TwoStatePreference(final Context context) {
        this(context, null);
    }
    
    public TwoStatePreference(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public TwoStatePreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public TwoStatePreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public boolean isChecked() {
        return this.mChecked;
    }
    
    @Override
    protected void onClick() {
        super.onClick();
        final boolean b = this.isChecked() ^ true;
        if (this.callChangeListener(b)) {
            this.setChecked(b);
        }
    }
    
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, final int n) {
        return typedArray.getBoolean(n, false);
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable.getClass().equals(SavedState.class)) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.setChecked(savedState.mChecked);
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
        savedState.mChecked = this.isChecked();
        return (Parcelable)savedState;
    }
    
    @Override
    protected void onSetInitialValue(final Object o) {
        Object false = o;
        if (o == null) {
            false = Boolean.FALSE;
        }
        this.setChecked(this.getPersistedBoolean((boolean)false));
    }
    
    public void setChecked(final boolean mChecked) {
        final boolean b = this.mChecked != mChecked;
        if (b || !this.mCheckedSet) {
            this.mChecked = mChecked;
            this.mCheckedSet = true;
            this.persistBoolean(mChecked);
            if (b) {
                this.notifyDependencyChange(this.shouldDisableDependents());
                this.notifyChanged();
            }
        }
    }
    
    public void setDisableDependentsState(final boolean mDisableDependentsState) {
        this.mDisableDependentsState = mDisableDependentsState;
    }
    
    public void setSummaryOff(final CharSequence mSummaryOff) {
        this.mSummaryOff = mSummaryOff;
        if (!this.isChecked()) {
            this.notifyChanged();
        }
    }
    
    public void setSummaryOn(final CharSequence mSummaryOn) {
        this.mSummaryOn = mSummaryOn;
        if (this.isChecked()) {
            this.notifyChanged();
        }
    }
    
    @Override
    public boolean shouldDisableDependents() {
        final boolean mDisableDependentsState = this.mDisableDependentsState;
        final boolean b = true;
        boolean mChecked;
        if (mDisableDependentsState) {
            mChecked = this.mChecked;
        }
        else {
            mChecked = !this.mChecked;
        }
        boolean b2 = b;
        if (!mChecked) {
            b2 = (super.shouldDisableDependents() && b);
        }
        return b2;
    }
    
    protected void syncSummaryView(final View view) {
        if (!(view instanceof TextView)) {
            return;
        }
        final TextView textView = (TextView)view;
        final int n = 1;
        final boolean mChecked = this.mChecked;
        final int n2 = 0;
        int n3 = 0;
        Label_0085: {
            if (mChecked && !TextUtils.isEmpty(this.mSummaryOn)) {
                textView.setText(this.mSummaryOn);
            }
            else {
                n3 = n;
                if (this.mChecked) {
                    break Label_0085;
                }
                n3 = n;
                if (TextUtils.isEmpty(this.mSummaryOff)) {
                    break Label_0085;
                }
                textView.setText(this.mSummaryOff);
            }
            n3 = 0;
        }
        int n4;
        if ((n4 = n3) != 0) {
            final CharSequence summary = this.getSummary();
            n4 = n3;
            if (!TextUtils.isEmpty(summary)) {
                textView.setText(summary);
                n4 = 0;
            }
        }
        int visibility;
        if (n4 == 0) {
            visibility = n2;
        }
        else {
            visibility = 8;
        }
        if (visibility != textView.getVisibility()) {
            textView.setVisibility(visibility);
        }
    }
    
    protected void syncSummaryView(final PreferenceViewHolder preferenceViewHolder) {
        this.syncSummaryView(preferenceViewHolder.findViewById(16908304));
    }
    
    static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean mChecked;
        
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
            boolean mChecked = true;
            if (int1 != 1) {
                mChecked = false;
            }
            this.mChecked = mChecked;
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt((int)(this.mChecked ? 1 : 0));
        }
    }
}
