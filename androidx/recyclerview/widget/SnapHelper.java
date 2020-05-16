// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public abstract class SnapHelper extends OnFlingListener
{
    RecyclerView mRecyclerView;
    private final OnScrollListener mScrollListener;
    
    public SnapHelper() {
        this.mScrollListener = new OnScrollListener() {
            boolean mScrolled = false;
            
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int n) {
                super.onScrollStateChanged(recyclerView, n);
                if (n == 0 && this.mScrolled) {
                    this.mScrolled = false;
                    SnapHelper.this.snapToTargetExistingView();
                }
            }
            
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int n, final int n2) {
                if (n != 0 || n2 != 0) {
                    this.mScrolled = true;
                }
            }
        };
    }
    
    private void destroyCallbacks() {
        this.mRecyclerView.removeOnScrollListener(this.mScrollListener);
        this.mRecyclerView.setOnFlingListener(null);
    }
    
    private void setupCallbacks() throws IllegalStateException {
        if (this.mRecyclerView.getOnFlingListener() == null) {
            this.mRecyclerView.addOnScrollListener(this.mScrollListener);
            this.mRecyclerView.setOnFlingListener((RecyclerView.OnFlingListener)this);
            return;
        }
        throw new IllegalStateException("An instance of OnFlingListener already set.");
    }
    
    private boolean snapFromFling(final LayoutManager layoutManager, int targetSnapPosition, final int n) {
        if (!(layoutManager instanceof ScrollVectorProvider)) {
            return false;
        }
        final SmoothScroller scroller = this.createScroller(layoutManager);
        if (scroller == null) {
            return false;
        }
        targetSnapPosition = this.findTargetSnapPosition(layoutManager, targetSnapPosition, n);
        if (targetSnapPosition == -1) {
            return false;
        }
        scroller.setTargetPosition(targetSnapPosition);
        layoutManager.startSmoothScroll(scroller);
        return true;
    }
    
    public void attachToRecyclerView(final RecyclerView mRecyclerView) throws IllegalStateException {
        final RecyclerView mRecyclerView2 = this.mRecyclerView;
        if (mRecyclerView2 == mRecyclerView) {
            return;
        }
        if (mRecyclerView2 != null) {
            this.destroyCallbacks();
        }
        if ((this.mRecyclerView = mRecyclerView) != null) {
            this.setupCallbacks();
            new Scroller(this.mRecyclerView.getContext(), (Interpolator)new DecelerateInterpolator());
            this.snapToTargetExistingView();
        }
    }
    
    public abstract int[] calculateDistanceToFinalSnap(final LayoutManager p0, final View p1);
    
    protected abstract SmoothScroller createScroller(final LayoutManager p0);
    
    public abstract View findSnapView(final LayoutManager p0);
    
    public abstract int findTargetSnapPosition(final LayoutManager p0, final int p1, final int p2);
    
    @Override
    public boolean onFling(final int a, final int a2) {
        final RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        final boolean b = false;
        if (layoutManager == null) {
            return false;
        }
        if (this.mRecyclerView.getAdapter() == null) {
            return false;
        }
        final int minFlingVelocity = this.mRecyclerView.getMinFlingVelocity();
        if (Math.abs(a2) <= minFlingVelocity) {
            final boolean b2 = b;
            if (Math.abs(a) <= minFlingVelocity) {
                return b2;
            }
        }
        boolean b2 = b;
        if (this.snapFromFling(layoutManager, a, a2)) {
            b2 = true;
        }
        return b2;
    }
    
    void snapToTargetExistingView() {
        final RecyclerView mRecyclerView = this.mRecyclerView;
        if (mRecyclerView == null) {
            return;
        }
        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        final View snapView = this.findSnapView(layoutManager);
        if (snapView == null) {
            return;
        }
        final int[] calculateDistanceToFinalSnap = this.calculateDistanceToFinalSnap(layoutManager, snapView);
        if (calculateDistanceToFinalSnap[0] != 0 || calculateDistanceToFinalSnap[1] != 0) {
            this.mRecyclerView.smoothScrollBy(calculateDistanceToFinalSnap[0], calculateDistanceToFinalSnap[1]);
        }
    }
}
