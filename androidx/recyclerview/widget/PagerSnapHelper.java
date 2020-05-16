// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import android.view.animation.Interpolator;
import android.util.DisplayMetrics;
import android.content.Context;
import android.graphics.PointF;
import android.view.View;

public class PagerSnapHelper extends SnapHelper
{
    private OrientationHelper mHorizontalHelper;
    private OrientationHelper mVerticalHelper;
    
    private int distanceToCenter(final View view, final OrientationHelper orientationHelper) {
        return orientationHelper.getDecoratedStart(view) + orientationHelper.getDecoratedMeasurement(view) / 2 - (orientationHelper.getStartAfterPadding() + orientationHelper.getTotalSpace() / 2);
    }
    
    private View findCenterView(final LayoutManager layoutManager, final OrientationHelper orientationHelper) {
        final int childCount = layoutManager.getChildCount();
        View view = null;
        if (childCount == 0) {
            return null;
        }
        final int startAfterPadding = orientationHelper.getStartAfterPadding();
        final int n = orientationHelper.getTotalSpace() / 2;
        int n2 = Integer.MAX_VALUE;
        int n3;
        for (int i = 0; i < childCount; ++i, n2 = n3) {
            final View child = layoutManager.getChildAt(i);
            final int abs = Math.abs(orientationHelper.getDecoratedStart(child) + orientationHelper.getDecoratedMeasurement(child) / 2 - (startAfterPadding + n));
            if (abs < (n3 = n2)) {
                view = child;
                n3 = abs;
            }
        }
        return view;
    }
    
    private OrientationHelper getHorizontalHelper(final LayoutManager layoutManager) {
        final OrientationHelper mHorizontalHelper = this.mHorizontalHelper;
        if (mHorizontalHelper == null || mHorizontalHelper.mLayoutManager != layoutManager) {
            this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return this.mHorizontalHelper;
    }
    
    private OrientationHelper getOrientationHelper(final LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return this.getVerticalHelper(layoutManager);
        }
        if (layoutManager.canScrollHorizontally()) {
            return this.getHorizontalHelper(layoutManager);
        }
        return null;
    }
    
    private OrientationHelper getVerticalHelper(final LayoutManager layoutManager) {
        final OrientationHelper mVerticalHelper = this.mVerticalHelper;
        if (mVerticalHelper == null || mVerticalHelper.mLayoutManager != layoutManager) {
            this.mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return this.mVerticalHelper;
    }
    
    private boolean isForwardFling(final LayoutManager layoutManager, final int n, final int n2) {
        final boolean canScrollHorizontally = layoutManager.canScrollHorizontally();
        final boolean b = true;
        boolean b2 = true;
        if (canScrollHorizontally) {
            if (n <= 0) {
                b2 = false;
            }
            return b2;
        }
        return n2 > 0 && b;
    }
    
    private boolean isReverseLayout(final LayoutManager layoutManager) {
        final int itemCount = layoutManager.getItemCount();
        final boolean b = layoutManager instanceof ScrollVectorProvider;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final PointF computeScrollVectorForPosition = ((ScrollVectorProvider)layoutManager).computeScrollVectorForPosition(itemCount - 1);
            b3 = b2;
            if (computeScrollVectorForPosition != null) {
                if (computeScrollVectorForPosition.x >= 0.0f) {
                    b3 = b2;
                    if (computeScrollVectorForPosition.y >= 0.0f) {
                        return b3;
                    }
                }
                b3 = true;
            }
        }
        return b3;
    }
    
    @Override
    public int[] calculateDistanceToFinalSnap(final LayoutManager layoutManager, final View view) {
        final int[] array = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            array[0] = this.distanceToCenter(view, this.getHorizontalHelper(layoutManager));
        }
        else {
            array[0] = 0;
        }
        if (layoutManager.canScrollVertically()) {
            array[1] = this.distanceToCenter(view, this.getVerticalHelper(layoutManager));
        }
        else {
            array[1] = 0;
        }
        return array;
    }
    
    @Override
    protected SmoothScroller createScroller(final LayoutManager layoutManager) {
        if (!(layoutManager instanceof ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(super.mRecyclerView.getContext()) {
            @Override
            protected float calculateSpeedPerPixel(final DisplayMetrics displayMetrics) {
                return 100.0f / displayMetrics.densityDpi;
            }
            
            @Override
            protected int calculateTimeForScrolling(final int n) {
                return Math.min(100, super.calculateTimeForScrolling(n));
            }
            
            @Override
            protected void onTargetFound(final View view, final State state, final Action action) {
                final PagerSnapHelper this$0 = PagerSnapHelper.this;
                final int[] calculateDistanceToFinalSnap = this$0.calculateDistanceToFinalSnap(this$0.mRecyclerView.getLayoutManager(), view);
                final int a = calculateDistanceToFinalSnap[0];
                final int a2 = calculateDistanceToFinalSnap[1];
                final int calculateTimeForDeceleration = this.calculateTimeForDeceleration(Math.max(Math.abs(a), Math.abs(a2)));
                if (calculateTimeForDeceleration > 0) {
                    action.update(a, a2, calculateTimeForDeceleration, (Interpolator)super.mDecelerateInterpolator);
                }
            }
        };
    }
    
    @Override
    public View findSnapView(final LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return this.findCenterView(layoutManager, this.getVerticalHelper(layoutManager));
        }
        if (layoutManager.canScrollHorizontally()) {
            return this.findCenterView(layoutManager, this.getHorizontalHelper(layoutManager));
        }
        return null;
    }
    
    @Override
    public int findTargetSnapPosition(final LayoutManager layoutManager, int n, int position) {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return -1;
        }
        final OrientationHelper orientationHelper = this.getOrientationHelper(layoutManager);
        if (orientationHelper == null) {
            return -1;
        }
        int n2 = Integer.MIN_VALUE;
        int n3 = Integer.MAX_VALUE;
        final int childCount = layoutManager.getChildCount();
        int i = 0;
        View view = null;
        View view2 = null;
        while (i < childCount) {
            final View child = layoutManager.getChildAt(i);
            int n4;
            View view3;
            if (child == null) {
                n4 = n3;
                view3 = view;
            }
            else {
                final int distanceToCenter = this.distanceToCenter(child, orientationHelper);
                int n5 = n2;
                View view4 = view2;
                if (distanceToCenter <= 0) {
                    n5 = n2;
                    view4 = view2;
                    if (distanceToCenter > n2) {
                        view4 = child;
                        n5 = distanceToCenter;
                    }
                }
                n2 = n5;
                n4 = n3;
                view3 = view;
                view2 = view4;
                if (distanceToCenter >= 0) {
                    n2 = n5;
                    n4 = n3;
                    view3 = view;
                    view2 = view4;
                    if (distanceToCenter < n3) {
                        n4 = distanceToCenter;
                        view2 = view4;
                        view3 = child;
                        n2 = n5;
                    }
                }
            }
            ++i;
            n3 = n4;
            view = view3;
        }
        final boolean forwardFling = this.isForwardFling(layoutManager, n, position);
        if (forwardFling && view != null) {
            return layoutManager.getPosition(view);
        }
        if (!forwardFling && view2 != null) {
            return layoutManager.getPosition(view2);
        }
        if (forwardFling) {
            view = view2;
        }
        if (view == null) {
            return -1;
        }
        position = layoutManager.getPosition(view);
        if (this.isReverseLayout(layoutManager) == forwardFling) {
            n = -1;
        }
        else {
            n = 1;
        }
        n += position;
        if (n >= 0 && n < itemCount) {
            return n;
        }
        return -1;
    }
}
