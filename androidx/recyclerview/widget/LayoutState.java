// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import android.view.View;

class LayoutState
{
    int mAvailable;
    int mCurrentPosition;
    int mEndLine;
    boolean mInfinite;
    int mItemDirection;
    int mLayoutDirection;
    boolean mRecycle;
    int mStartLine;
    boolean mStopInFocusable;
    
    LayoutState() {
        this.mRecycle = true;
        this.mStartLine = 0;
        this.mEndLine = 0;
    }
    
    boolean hasMore(final RecyclerView.State state) {
        final int mCurrentPosition = this.mCurrentPosition;
        return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
    }
    
    View next(final RecyclerView.Recycler recycler) {
        final View viewForPosition = recycler.getViewForPosition(this.mCurrentPosition);
        this.mCurrentPosition += this.mItemDirection;
        return viewForPosition;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LayoutState{mAvailable=");
        sb.append(this.mAvailable);
        sb.append(", mCurrentPosition=");
        sb.append(this.mCurrentPosition);
        sb.append(", mItemDirection=");
        sb.append(this.mItemDirection);
        sb.append(", mLayoutDirection=");
        sb.append(this.mLayoutDirection);
        sb.append(", mStartLine=");
        sb.append(this.mStartLine);
        sb.append(", mEndLine=");
        sb.append(this.mEndLine);
        sb.append('}');
        return sb.toString();
    }
}
