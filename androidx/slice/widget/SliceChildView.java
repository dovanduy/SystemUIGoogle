// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.slice.core.SliceAction;
import java.util.List;
import java.util.Set;
import androidx.slice.SliceItem;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public abstract class SliceChildView extends FrameLayout
{
    protected int mInsetBottom;
    protected int mInsetEnd;
    protected int mInsetStart;
    protected int mInsetTop;
    protected long mLastUpdated;
    protected SliceActionView.SliceActionLoadingListener mLoadingListener;
    protected SliceView.OnSliceActionListener mObserver;
    protected boolean mShowLastUpdated;
    protected SliceStyle mSliceStyle;
    protected int mTintColor;
    protected SliceViewPolicy mViewPolicy;
    
    public SliceChildView(final Context context) {
        super(context);
        this.mTintColor = -1;
        this.mLastUpdated = -1L;
    }
    
    public SliceChildView(final Context context, final AttributeSet set) {
        this(context);
    }
    
    public int getMode() {
        final SliceViewPolicy mViewPolicy = this.mViewPolicy;
        int mode;
        if (mViewPolicy != null) {
            mode = mViewPolicy.getMode();
        }
        else {
            mode = 2;
        }
        return mode;
    }
    
    public abstract void resetView();
    
    public void setActionLoading(final SliceItem sliceItem) {
    }
    
    public void setAllowTwoLines(final boolean b) {
    }
    
    public void setInsets(final int mInsetStart, final int mInsetTop, final int mInsetEnd, final int mInsetBottom) {
        this.mInsetStart = mInsetStart;
        this.mInsetTop = mInsetTop;
        this.mInsetEnd = mInsetEnd;
        this.mInsetBottom = mInsetBottom;
    }
    
    public void setLastUpdated(final long mLastUpdated) {
        this.mLastUpdated = mLastUpdated;
    }
    
    public void setLoadingActions(final Set<SliceItem> set) {
    }
    
    public void setPolicy(final SliceViewPolicy mViewPolicy) {
        this.mViewPolicy = mViewPolicy;
    }
    
    public void setShowLastUpdated(final boolean mShowLastUpdated) {
        this.mShowLastUpdated = mShowLastUpdated;
    }
    
    public void setSliceActionListener(final SliceView.OnSliceActionListener mObserver) {
        this.mObserver = mObserver;
    }
    
    public void setSliceActionLoadingListener(final SliceActionView.SliceActionLoadingListener mLoadingListener) {
        this.mLoadingListener = mLoadingListener;
    }
    
    public void setSliceActions(final List<SliceAction> list) {
    }
    
    public void setSliceContent(final ListContent listContent) {
    }
    
    public void setSliceItem(final SliceContent sliceContent, final boolean b, final int n, final int n2, final SliceView.OnSliceActionListener onSliceActionListener) {
    }
    
    public void setStyle(final SliceStyle mSliceStyle) {
        this.mSliceStyle = mSliceStyle;
    }
    
    public void setTint(final int mTintColor) {
        this.mTintColor = mTintColor;
    }
}
