// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

public class SliceViewPolicy
{
    private PolicyChangeListener mListener;
    private int mMaxHeight;
    private int mMaxSmallHeight;
    private int mMode;
    private boolean mScrollable;
    
    public SliceViewPolicy() {
        this.mMaxHeight = 0;
        this.mMaxSmallHeight = 0;
        this.mScrollable = true;
        this.mMode = 2;
    }
    
    public int getMaxHeight() {
        return this.mMaxHeight;
    }
    
    public int getMaxSmallHeight() {
        return this.mMaxSmallHeight;
    }
    
    public int getMode() {
        return this.mMode;
    }
    
    public boolean isScrollable() {
        return this.mScrollable;
    }
    
    public void setListener(final PolicyChangeListener mListener) {
        this.mListener = mListener;
    }
    
    public void setMaxHeight(final int mMaxHeight) {
        if (mMaxHeight != this.mMaxHeight) {
            this.mMaxHeight = mMaxHeight;
            final PolicyChangeListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onMaxHeightChanged(mMaxHeight);
            }
        }
    }
    
    public void setMaxSmallHeight(final int mMaxSmallHeight) {
        if (this.mMaxSmallHeight != mMaxSmallHeight) {
            this.mMaxSmallHeight = mMaxSmallHeight;
            final PolicyChangeListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onMaxSmallChanged(mMaxSmallHeight);
            }
        }
    }
    
    public interface PolicyChangeListener
    {
        void onMaxHeightChanged(final int p0);
        
        void onMaxSmallChanged(final int p0);
    }
}
