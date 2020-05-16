// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.widget;

import android.view.ViewGroup$LayoutParams;
import android.view.View;
import java.util.Locale;
import android.view.ViewGroup$MarginLayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

final class ScrollEventAdapter extends OnScrollListener
{
    private int mAdapterState;
    private ViewPager2.OnPageChangeCallback mCallback;
    private boolean mDataSetChangeHappened;
    private boolean mDispatchSelected;
    private int mDragStartPosition;
    private boolean mFakeDragging;
    private final LinearLayoutManager mLayoutManager;
    private final RecyclerView mRecyclerView;
    private boolean mScrollHappened;
    private int mScrollState;
    private ScrollEventValues mScrollValues;
    private int mTarget;
    private final ViewPager2 mViewPager;
    
    ScrollEventAdapter(final ViewPager2 mViewPager) {
        this.mViewPager = mViewPager;
        final RecyclerView mRecyclerView = mViewPager.mRecyclerView;
        this.mRecyclerView = mRecyclerView;
        this.mLayoutManager = (LinearLayoutManager)mRecyclerView.getLayoutManager();
        this.mScrollValues = new ScrollEventValues();
        this.resetState();
    }
    
    private void dispatchScrolled(final int n, final float n2, final int n3) {
        final ViewPager2.OnPageChangeCallback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onPageScrolled(n, n2, n3);
        }
    }
    
    private void dispatchSelected(final int n) {
        final ViewPager2.OnPageChangeCallback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onPageSelected(n);
        }
    }
    
    private void dispatchStateChanged(final int mScrollState) {
        if (this.mAdapterState == 3 && this.mScrollState == 0) {
            return;
        }
        if (this.mScrollState == mScrollState) {
            return;
        }
        this.mScrollState = mScrollState;
        final ViewPager2.OnPageChangeCallback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onPageScrollStateChanged(mScrollState);
        }
    }
    
    private int getPosition() {
        return this.mLayoutManager.findFirstVisibleItemPosition();
    }
    
    private boolean isInAnyDraggingState() {
        final int mAdapterState = this.mAdapterState;
        boolean b = true;
        if (mAdapterState != 1) {
            b = (mAdapterState == 4 && b);
        }
        return b;
    }
    
    private void resetState() {
        this.mAdapterState = 0;
        this.mScrollState = 0;
        this.mScrollValues.reset();
        this.mDragStartPosition = -1;
        this.mTarget = -1;
        this.mDispatchSelected = false;
        this.mScrollHappened = false;
        this.mFakeDragging = false;
        this.mDataSetChangeHappened = false;
    }
    
    private void startDrag(final boolean mFakeDragging) {
        this.mFakeDragging = mFakeDragging;
        int mAdapterState;
        if (mFakeDragging) {
            mAdapterState = 4;
        }
        else {
            mAdapterState = 1;
        }
        this.mAdapterState = mAdapterState;
        final int mTarget = this.mTarget;
        if (mTarget != -1) {
            this.mDragStartPosition = mTarget;
            this.mTarget = -1;
        }
        else if (this.mDragStartPosition == -1) {
            this.mDragStartPosition = this.getPosition();
        }
        this.dispatchStateChanged(1);
    }
    
    private void updateScrollEventValues() {
        final ScrollEventValues mScrollValues = this.mScrollValues;
        final int firstVisibleItemPosition = this.mLayoutManager.findFirstVisibleItemPosition();
        mScrollValues.mPosition = firstVisibleItemPosition;
        if (firstVisibleItemPosition == -1) {
            mScrollValues.reset();
            return;
        }
        final View viewByPosition = this.mLayoutManager.findViewByPosition(firstVisibleItemPosition);
        if (viewByPosition == null) {
            mScrollValues.reset();
            return;
        }
        final int leftDecorationWidth = ((RecyclerView.LayoutManager)this.mLayoutManager).getLeftDecorationWidth(viewByPosition);
        final int rightDecorationWidth = ((RecyclerView.LayoutManager)this.mLayoutManager).getRightDecorationWidth(viewByPosition);
        final int topDecorationHeight = ((RecyclerView.LayoutManager)this.mLayoutManager).getTopDecorationHeight(viewByPosition);
        final int bottomDecorationHeight = ((RecyclerView.LayoutManager)this.mLayoutManager).getBottomDecorationHeight(viewByPosition);
        final ViewGroup$LayoutParams layoutParams = viewByPosition.getLayoutParams();
        int n = leftDecorationWidth;
        int n2 = rightDecorationWidth;
        int n3 = topDecorationHeight;
        int n4 = bottomDecorationHeight;
        if (layoutParams instanceof ViewGroup$MarginLayoutParams) {
            final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)layoutParams;
            n = leftDecorationWidth + viewGroup$MarginLayoutParams.leftMargin;
            n2 = rightDecorationWidth + viewGroup$MarginLayoutParams.rightMargin;
            n3 = topDecorationHeight + viewGroup$MarginLayoutParams.topMargin;
            n4 = bottomDecorationHeight + viewGroup$MarginLayoutParams.bottomMargin;
        }
        final int n5 = viewByPosition.getHeight() + n3 + n4;
        final int width = viewByPosition.getWidth();
        int n8;
        int n9;
        if (this.mLayoutManager.getOrientation() == 0) {
            int n6 = viewByPosition.getLeft() - n - this.mRecyclerView.getPaddingLeft();
            if (this.mViewPager.isRtl()) {
                n6 = -n6;
            }
            final int n7 = width + n + n2;
            n8 = n6;
            n9 = n7;
        }
        else {
            n8 = viewByPosition.getTop() - n3 - this.mRecyclerView.getPaddingTop();
            n9 = n5;
        }
        final int mOffsetPx = -n8;
        mScrollValues.mOffsetPx = mOffsetPx;
        if (mOffsetPx >= 0) {
            float mOffset;
            if (n9 == 0) {
                mOffset = 0.0f;
            }
            else {
                mOffset = mOffsetPx / (float)n9;
            }
            mScrollValues.mOffset = mOffset;
            return;
        }
        if (new AnimateLayoutChangeDetector(this.mLayoutManager).mayHaveInterferingAnimations()) {
            throw new IllegalStateException("Page(s) contain a ViewGroup with a LayoutTransition (or animateLayoutChanges=\"true\"), which interferes with the scrolling animation. Make sure to call getLayoutTransition().setAnimateParentHierarchy(false) on all ViewGroups with a LayoutTransition before an animation is started.");
        }
        throw new IllegalStateException(String.format(Locale.US, "Page can only be offset by a positive amount, not by %d", mScrollValues.mOffsetPx));
    }
    
    double getRelativeScrollPosition() {
        this.updateScrollEventValues();
        final ScrollEventValues mScrollValues = this.mScrollValues;
        return mScrollValues.mPosition + (double)mScrollValues.mOffset;
    }
    
    int getScrollState() {
        return this.mScrollState;
    }
    
    boolean isFakeDragging() {
        return this.mFakeDragging;
    }
    
    boolean isIdle() {
        return this.mScrollState == 0;
    }
    
    void notifyDataSetChangeHappened() {
        this.mDataSetChangeHappened = true;
    }
    
    void notifyProgrammaticScroll(final int mTarget, final boolean b) {
        int mAdapterState;
        if (b) {
            mAdapterState = 2;
        }
        else {
            mAdapterState = 3;
        }
        this.mAdapterState = mAdapterState;
        boolean b2 = false;
        this.mFakeDragging = false;
        if (this.mTarget != mTarget) {
            b2 = true;
        }
        this.mTarget = mTarget;
        this.dispatchStateChanged(2);
        if (b2) {
            this.dispatchSelected(mTarget);
        }
    }
    
    @Override
    public void onScrollStateChanged(final RecyclerView recyclerView, int mTarget) {
        final int mAdapterState = this.mAdapterState;
        final boolean b = true;
        if ((mAdapterState != 1 || this.mScrollState != 1) && mTarget == 1) {
            this.startDrag(false);
            return;
        }
        if (this.isInAnyDraggingState() && mTarget == 2) {
            if (this.mScrollHappened) {
                this.dispatchStateChanged(2);
                this.mDispatchSelected = true;
            }
            return;
        }
        if (this.isInAnyDraggingState() && mTarget == 0) {
            this.updateScrollEventValues();
            int n;
            if (!this.mScrollHappened) {
                final int mPosition = this.mScrollValues.mPosition;
                n = (b ? 1 : 0);
                if (mPosition != -1) {
                    this.dispatchScrolled(mPosition, 0.0f, 0);
                    n = (b ? 1 : 0);
                }
            }
            else {
                final ScrollEventValues mScrollValues = this.mScrollValues;
                if (mScrollValues.mOffsetPx == 0) {
                    final int mDragStartPosition = this.mDragStartPosition;
                    final int mPosition2 = mScrollValues.mPosition;
                    n = (b ? 1 : 0);
                    if (mDragStartPosition != mPosition2) {
                        this.dispatchSelected(mPosition2);
                        n = (b ? 1 : 0);
                    }
                }
                else {
                    n = 0;
                }
            }
            if (n != 0) {
                this.dispatchStateChanged(0);
                this.resetState();
            }
        }
        if (this.mAdapterState == 2 && mTarget == 0 && this.mDataSetChangeHappened) {
            this.updateScrollEventValues();
            final ScrollEventValues mScrollValues2 = this.mScrollValues;
            if (mScrollValues2.mOffsetPx == 0) {
                mTarget = this.mTarget;
                final int mPosition3 = mScrollValues2.mPosition;
                if (mTarget != mPosition3) {
                    if ((mTarget = mPosition3) == -1) {
                        mTarget = 0;
                    }
                    this.dispatchSelected(mTarget);
                }
                this.dispatchStateChanged(0);
                this.resetState();
            }
        }
    }
    
    @Override
    public void onScrolled(final RecyclerView recyclerView, int mTarget, int n) {
        this.mScrollHappened = true;
        this.updateScrollEventValues();
        if (this.mDispatchSelected) {
            this.mDispatchSelected = false;
            if (n <= 0 && (n != 0 || mTarget < 0 != this.mViewPager.isRtl())) {
                mTarget = 0;
            }
            else {
                mTarget = 1;
            }
            Label_0098: {
                if (mTarget != 0) {
                    final ScrollEventValues mScrollValues = this.mScrollValues;
                    if (mScrollValues.mOffsetPx != 0) {
                        mTarget = mScrollValues.mPosition + 1;
                        break Label_0098;
                    }
                }
                mTarget = this.mScrollValues.mPosition;
            }
            this.mTarget = mTarget;
            if (this.mDragStartPosition != mTarget) {
                this.dispatchSelected(mTarget);
            }
        }
        else if (this.mAdapterState == 0) {
            n = this.mScrollValues.mPosition;
            if ((mTarget = n) == -1) {
                mTarget = 0;
            }
            this.dispatchSelected(mTarget);
        }
        n = this.mScrollValues.mPosition;
        if ((mTarget = n) == -1) {
            mTarget = 0;
        }
        final ScrollEventValues mScrollValues2 = this.mScrollValues;
        this.dispatchScrolled(mTarget, mScrollValues2.mOffset, mScrollValues2.mOffsetPx);
        mTarget = this.mScrollValues.mPosition;
        n = this.mTarget;
        if ((mTarget == n || n == -1) && this.mScrollValues.mOffsetPx == 0 && this.mScrollState != 1) {
            this.dispatchStateChanged(0);
            this.resetState();
        }
    }
    
    void setOnPageChangeCallback(final ViewPager2.OnPageChangeCallback mCallback) {
        this.mCallback = mCallback;
    }
    
    private static final class ScrollEventValues
    {
        float mOffset;
        int mOffsetPx;
        int mPosition;
        
        ScrollEventValues() {
        }
        
        void reset() {
            this.mPosition = -1;
            this.mOffset = 0.0f;
            this.mOffsetPx = 0;
        }
    }
}
