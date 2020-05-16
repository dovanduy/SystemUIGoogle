// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

public final class RowContentBindParams
{
    private int mContentViews;
    private int mDirtyContentViews;
    private boolean mUseChildInGroup;
    private boolean mUseIncreasedHeadsUpHeight;
    private boolean mUseIncreasedHeight;
    private boolean mUseLowPriority;
    private boolean mViewsNeedReinflation;
    
    public RowContentBindParams() {
        this.mContentViews = 3;
        this.mDirtyContentViews = 3;
    }
    
    void clearDirtyContentViews() {
        this.mDirtyContentViews = 0;
    }
    
    public int getContentViews() {
        return this.mContentViews;
    }
    
    public int getDirtyContentViews() {
        return this.mDirtyContentViews;
    }
    
    public void markContentViewsFreeable(int n) {
        final int mContentViews = this.mContentViews;
        n = n;
        this.mContentViews = (mContentViews & n);
        this.mDirtyContentViews &= n;
    }
    
    public boolean needsReinflation() {
        return this.mViewsNeedReinflation;
    }
    
    public void rebindAllContentViews() {
        this.mDirtyContentViews = this.mContentViews;
    }
    
    public void requireContentViews(int n) {
        final int mContentViews = this.mContentViews;
        n &= mContentViews;
        this.mContentViews = (mContentViews | n);
        this.mDirtyContentViews |= n;
    }
    
    public void setNeedsReinflation(final boolean mViewsNeedReinflation) {
        this.mViewsNeedReinflation = mViewsNeedReinflation;
        this.mDirtyContentViews |= this.mContentViews;
    }
    
    public void setUseIncreasedCollapsedHeight(final boolean mUseIncreasedHeight) {
        if (this.mUseIncreasedHeight != mUseIncreasedHeight) {
            this.mDirtyContentViews |= 0x1;
        }
        this.mUseIncreasedHeight = mUseIncreasedHeight;
    }
    
    public void setUseIncreasedHeadsUpHeight(final boolean mUseIncreasedHeadsUpHeight) {
        if (this.mUseIncreasedHeadsUpHeight != mUseIncreasedHeadsUpHeight) {
            this.mDirtyContentViews |= 0x4;
        }
        this.mUseIncreasedHeadsUpHeight = mUseIncreasedHeadsUpHeight;
    }
    
    public void setUseLowPriority(final boolean mUseLowPriority) {
        if (this.mUseLowPriority != mUseLowPriority) {
            this.mDirtyContentViews |= 0x3;
        }
        this.mUseLowPriority = mUseLowPriority;
    }
    
    @Override
    public String toString() {
        return String.format("RowContentBindParams[mContentViews=%x mDirtyContentViews=%x mUseLowPriority=%b mUseChildInGroup=%b mUseIncreasedHeight=%b mUseIncreasedHeadsUpHeight=%b mViewsNeedReinflation=%b]", this.mContentViews, this.mDirtyContentViews, this.mUseLowPriority, this.mUseChildInGroup, this.mUseIncreasedHeight, this.mUseIncreasedHeadsUpHeight, this.mViewsNeedReinflation);
    }
    
    public boolean useChildInGroup() {
        return this.mUseChildInGroup;
    }
    
    public boolean useIncreasedHeadsUpHeight() {
        return this.mUseIncreasedHeadsUpHeight;
    }
    
    public boolean useIncreasedHeight() {
        return this.mUseIncreasedHeight;
    }
    
    public boolean useLowPriority() {
        return this.mUseLowPriority;
    }
}
