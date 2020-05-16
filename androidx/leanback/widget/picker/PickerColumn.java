// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget.picker;

public class PickerColumn
{
    private int mCurrentValue;
    private String mLabelFormat;
    private int mMaxValue;
    private int mMinValue;
    private CharSequence[] mStaticLabels;
    
    public int getCount() {
        return this.mMaxValue - this.mMinValue + 1;
    }
    
    public int getCurrentValue() {
        return this.mCurrentValue;
    }
    
    public CharSequence getLabelFor(final int i) {
        final CharSequence[] mStaticLabels = this.mStaticLabels;
        if (mStaticLabels == null) {
            return String.format(this.mLabelFormat, i);
        }
        return mStaticLabels[i];
    }
    
    public int getMaxValue() {
        return this.mMaxValue;
    }
    
    public int getMinValue() {
        return this.mMinValue;
    }
    
    public void setCurrentValue(final int mCurrentValue) {
        this.mCurrentValue = mCurrentValue;
    }
    
    public void setLabelFormat(final String mLabelFormat) {
        this.mLabelFormat = mLabelFormat;
    }
    
    public void setMaxValue(final int mMaxValue) {
        this.mMaxValue = mMaxValue;
    }
    
    public void setMinValue(final int mMinValue) {
        this.mMinValue = mMinValue;
    }
    
    public void setStaticLabels(final CharSequence[] mStaticLabels) {
        this.mStaticLabels = mStaticLabels;
    }
}
