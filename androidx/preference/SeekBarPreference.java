// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import android.view.View$OnKeyListener;
import android.widget.SeekBar$OnSeekBarChangeListener;
import android.widget.SeekBar;

public class SeekBarPreference extends Preference
{
    boolean mAdjustable;
    private int mMax;
    int mMin;
    SeekBar mSeekBar;
    private SeekBar$OnSeekBarChangeListener mSeekBarChangeListener;
    private int mSeekBarIncrement;
    private View$OnKeyListener mSeekBarKeyListener;
    int mSeekBarValue;
    private TextView mSeekBarValueTextView;
    private boolean mShowSeekBarValue;
    boolean mTrackingTouch;
    boolean mUpdatesContinuously;
    
    public SeekBarPreference(final Context context, final AttributeSet set) {
        this(context, set, R$attr.seekBarPreferenceStyle);
    }
    
    public SeekBarPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public SeekBarPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mSeekBarChangeListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
                if (b) {
                    final SeekBarPreference this$0 = SeekBarPreference.this;
                    if (this$0.mUpdatesContinuously || !this$0.mTrackingTouch) {
                        SeekBarPreference.this.syncValueInternal(seekBar);
                        return;
                    }
                }
                final SeekBarPreference this$2 = SeekBarPreference.this;
                this$2.updateLabelValue(n + this$2.mMin);
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
                SeekBarPreference.this.mTrackingTouch = true;
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
                SeekBarPreference.this.mTrackingTouch = false;
                final int progress = seekBar.getProgress();
                final SeekBarPreference this$0 = SeekBarPreference.this;
                if (progress + this$0.mMin != this$0.mSeekBarValue) {
                    this$0.syncValueInternal(seekBar);
                }
            }
        };
        this.mSeekBarKeyListener = (View$OnKeyListener)new View$OnKeyListener() {
            public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
                if (keyEvent.getAction() != 0) {
                    return false;
                }
                if (!SeekBarPreference.this.mAdjustable && (n == 21 || n == 22)) {
                    return false;
                }
                if (n == 23 || n == 66) {
                    return false;
                }
                final SeekBar mSeekBar = SeekBarPreference.this.mSeekBar;
                if (mSeekBar == null) {
                    Log.e("SeekBarPreference", "SeekBar view is null and hence cannot be adjusted.");
                    return false;
                }
                return mSeekBar.onKeyDown(n, keyEvent);
            }
        };
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.SeekBarPreference, n, n2);
        this.mMin = obtainStyledAttributes.getInt(R$styleable.SeekBarPreference_min, 0);
        this.setMax(obtainStyledAttributes.getInt(R$styleable.SeekBarPreference_android_max, 100));
        this.setSeekBarIncrement(obtainStyledAttributes.getInt(R$styleable.SeekBarPreference_seekBarIncrement, 0));
        this.mAdjustable = obtainStyledAttributes.getBoolean(R$styleable.SeekBarPreference_adjustable, true);
        this.mShowSeekBarValue = obtainStyledAttributes.getBoolean(R$styleable.SeekBarPreference_showSeekBarValue, false);
        this.mUpdatesContinuously = obtainStyledAttributes.getBoolean(R$styleable.SeekBarPreference_updatesContinuously, false);
        obtainStyledAttributes.recycle();
    }
    
    private void setValueInternal(int mSeekBarValue, final boolean b) {
        final int mMin = this.mMin;
        int n = mSeekBarValue;
        if (mSeekBarValue < mMin) {
            n = mMin;
        }
        final int mMax = this.mMax;
        if ((mSeekBarValue = n) > mMax) {
            mSeekBarValue = mMax;
        }
        if (mSeekBarValue != this.mSeekBarValue) {
            this.updateLabelValue(this.mSeekBarValue = mSeekBarValue);
            this.persistInt(mSeekBarValue);
            if (b) {
                this.notifyChanged();
            }
        }
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setOnKeyListener(this.mSeekBarKeyListener);
        this.mSeekBar = (SeekBar)preferenceViewHolder.findViewById(R$id.seekbar);
        final TextView mSeekBarValueTextView = (TextView)preferenceViewHolder.findViewById(R$id.seekbar_value);
        this.mSeekBarValueTextView = mSeekBarValueTextView;
        if (this.mShowSeekBarValue) {
            mSeekBarValueTextView.setVisibility(0);
        }
        else {
            mSeekBarValueTextView.setVisibility(8);
            this.mSeekBarValueTextView = null;
        }
        final SeekBar mSeekBar = this.mSeekBar;
        if (mSeekBar == null) {
            Log.e("SeekBarPreference", "SeekBar view is null in onBindViewHolder.");
            return;
        }
        mSeekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
        this.mSeekBar.setMax(this.mMax - this.mMin);
        final int mSeekBarIncrement = this.mSeekBarIncrement;
        if (mSeekBarIncrement != 0) {
            this.mSeekBar.setKeyProgressIncrement(mSeekBarIncrement);
        }
        else {
            this.mSeekBarIncrement = this.mSeekBar.getKeyProgressIncrement();
        }
        this.mSeekBar.setProgress(this.mSeekBarValue - this.mMin);
        this.updateLabelValue(this.mSeekBarValue);
        this.mSeekBar.setEnabled(this.isEnabled());
    }
    
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, final int n) {
        return typedArray.getInt(n, 0);
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (!parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mSeekBarValue = savedState.mSeekBarValue;
        this.mMin = savedState.mMin;
        this.mMax = savedState.mMax;
        this.notifyChanged();
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (this.isPersistent()) {
            return onSaveInstanceState;
        }
        final SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.mSeekBarValue = this.mSeekBarValue;
        savedState.mMin = this.mMin;
        savedState.mMax = this.mMax;
        return (Parcelable)savedState;
    }
    
    @Override
    protected void onSetInitialValue(final Object o) {
        Object value = o;
        if (o == null) {
            value = 0;
        }
        this.setValue(this.getPersistedInt((int)value));
    }
    
    public final void setMax(final int n) {
        final int mMin = this.mMin;
        int mMax = n;
        if (n < mMin) {
            mMax = mMin;
        }
        if (mMax != this.mMax) {
            this.mMax = mMax;
            this.notifyChanged();
        }
    }
    
    public final void setSeekBarIncrement(final int a) {
        if (a != this.mSeekBarIncrement) {
            this.mSeekBarIncrement = Math.min(this.mMax - this.mMin, Math.abs(a));
            this.notifyChanged();
        }
    }
    
    public void setValue(final int n) {
        this.setValueInternal(n, true);
    }
    
    void syncValueInternal(final SeekBar seekBar) {
        final int i = this.mMin + seekBar.getProgress();
        if (i != this.mSeekBarValue) {
            if (this.callChangeListener(i)) {
                this.setValueInternal(i, false);
            }
            else {
                seekBar.setProgress(this.mSeekBarValue - this.mMin);
                this.updateLabelValue(this.mSeekBarValue);
            }
        }
    }
    
    void updateLabelValue(final int i) {
        final TextView mSeekBarValueTextView = this.mSeekBarValueTextView;
        if (mSeekBarValueTextView != null) {
            mSeekBarValueTextView.setText((CharSequence)String.valueOf(i));
        }
    }
    
    private static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int mMax;
        int mMin;
        int mSeekBarValue;
        
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
            this.mSeekBarValue = parcel.readInt();
            this.mMin = parcel.readInt();
            this.mMax = parcel.readInt();
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.mSeekBarValue);
            parcel.writeInt(this.mMin);
            parcel.writeInt(this.mMax);
        }
    }
}
