// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import android.view.View;
import android.graphics.Rect;

public abstract class OrientationHelper
{
    private int mLastTotalSpace;
    protected final RecyclerView.LayoutManager mLayoutManager;
    final Rect mTmpRect;
    
    private OrientationHelper(final RecyclerView.LayoutManager mLayoutManager) {
        this.mLastTotalSpace = Integer.MIN_VALUE;
        this.mTmpRect = new Rect();
        this.mLayoutManager = mLayoutManager;
    }
    
    public static OrientationHelper createHorizontalHelper(final RecyclerView.LayoutManager layoutManager) {
        return new OrientationHelper(layoutManager) {
            @Override
            public int getDecoratedEnd(final View view) {
                return super.mLayoutManager.getDecoratedRight(view) + ((RecyclerView.LayoutParams)view.getLayoutParams()).rightMargin;
            }
            
            @Override
            public int getDecoratedMeasurement(final View view) {
                final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view.getLayoutParams();
                return super.mLayoutManager.getDecoratedMeasuredWidth(view) + layoutParams.leftMargin + layoutParams.rightMargin;
            }
            
            @Override
            public int getDecoratedMeasurementInOther(final View view) {
                final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view.getLayoutParams();
                return super.mLayoutManager.getDecoratedMeasuredHeight(view) + layoutParams.topMargin + layoutParams.bottomMargin;
            }
            
            @Override
            public int getDecoratedStart(final View view) {
                return super.mLayoutManager.getDecoratedLeft(view) - ((RecyclerView.LayoutParams)view.getLayoutParams()).leftMargin;
            }
            
            @Override
            public int getEnd() {
                return super.mLayoutManager.getWidth();
            }
            
            @Override
            public int getEndAfterPadding() {
                return super.mLayoutManager.getWidth() - super.mLayoutManager.getPaddingRight();
            }
            
            @Override
            public int getEndPadding() {
                return super.mLayoutManager.getPaddingRight();
            }
            
            @Override
            public int getMode() {
                return super.mLayoutManager.getWidthMode();
            }
            
            @Override
            public int getModeInOther() {
                return super.mLayoutManager.getHeightMode();
            }
            
            @Override
            public int getStartAfterPadding() {
                return super.mLayoutManager.getPaddingLeft();
            }
            
            @Override
            public int getTotalSpace() {
                return super.mLayoutManager.getWidth() - super.mLayoutManager.getPaddingLeft() - super.mLayoutManager.getPaddingRight();
            }
            
            @Override
            public int getTransformedEndWithDecoration(final View view) {
                super.mLayoutManager.getTransformedBoundingBox(view, true, super.mTmpRect);
                return super.mTmpRect.right;
            }
            
            @Override
            public int getTransformedStartWithDecoration(final View view) {
                super.mLayoutManager.getTransformedBoundingBox(view, true, super.mTmpRect);
                return super.mTmpRect.left;
            }
            
            @Override
            public void offsetChildren(final int n) {
                super.mLayoutManager.offsetChildrenHorizontal(n);
            }
        };
    }
    
    public static OrientationHelper createOrientationHelper(final RecyclerView.LayoutManager layoutManager, final int n) {
        if (n == 0) {
            return createHorizontalHelper(layoutManager);
        }
        if (n == 1) {
            return createVerticalHelper(layoutManager);
        }
        throw new IllegalArgumentException("invalid orientation");
    }
    
    public static OrientationHelper createVerticalHelper(final RecyclerView.LayoutManager layoutManager) {
        return new OrientationHelper(layoutManager) {
            @Override
            public int getDecoratedEnd(final View view) {
                return super.mLayoutManager.getDecoratedBottom(view) + ((RecyclerView.LayoutParams)view.getLayoutParams()).bottomMargin;
            }
            
            @Override
            public int getDecoratedMeasurement(final View view) {
                final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view.getLayoutParams();
                return super.mLayoutManager.getDecoratedMeasuredHeight(view) + layoutParams.topMargin + layoutParams.bottomMargin;
            }
            
            @Override
            public int getDecoratedMeasurementInOther(final View view) {
                final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view.getLayoutParams();
                return super.mLayoutManager.getDecoratedMeasuredWidth(view) + layoutParams.leftMargin + layoutParams.rightMargin;
            }
            
            @Override
            public int getDecoratedStart(final View view) {
                return super.mLayoutManager.getDecoratedTop(view) - ((RecyclerView.LayoutParams)view.getLayoutParams()).topMargin;
            }
            
            @Override
            public int getEnd() {
                return super.mLayoutManager.getHeight();
            }
            
            @Override
            public int getEndAfterPadding() {
                return super.mLayoutManager.getHeight() - super.mLayoutManager.getPaddingBottom();
            }
            
            @Override
            public int getEndPadding() {
                return super.mLayoutManager.getPaddingBottom();
            }
            
            @Override
            public int getMode() {
                return super.mLayoutManager.getHeightMode();
            }
            
            @Override
            public int getModeInOther() {
                return super.mLayoutManager.getWidthMode();
            }
            
            @Override
            public int getStartAfterPadding() {
                return super.mLayoutManager.getPaddingTop();
            }
            
            @Override
            public int getTotalSpace() {
                return super.mLayoutManager.getHeight() - super.mLayoutManager.getPaddingTop() - super.mLayoutManager.getPaddingBottom();
            }
            
            @Override
            public int getTransformedEndWithDecoration(final View view) {
                super.mLayoutManager.getTransformedBoundingBox(view, true, super.mTmpRect);
                return super.mTmpRect.bottom;
            }
            
            @Override
            public int getTransformedStartWithDecoration(final View view) {
                super.mLayoutManager.getTransformedBoundingBox(view, true, super.mTmpRect);
                return super.mTmpRect.top;
            }
            
            @Override
            public void offsetChildren(final int n) {
                super.mLayoutManager.offsetChildrenVertical(n);
            }
        };
    }
    
    public abstract int getDecoratedEnd(final View p0);
    
    public abstract int getDecoratedMeasurement(final View p0);
    
    public abstract int getDecoratedMeasurementInOther(final View p0);
    
    public abstract int getDecoratedStart(final View p0);
    
    public abstract int getEnd();
    
    public abstract int getEndAfterPadding();
    
    public abstract int getEndPadding();
    
    public abstract int getMode();
    
    public abstract int getModeInOther();
    
    public abstract int getStartAfterPadding();
    
    public abstract int getTotalSpace();
    
    public int getTotalSpaceChange() {
        int n;
        if (Integer.MIN_VALUE == this.mLastTotalSpace) {
            n = 0;
        }
        else {
            n = this.getTotalSpace() - this.mLastTotalSpace;
        }
        return n;
    }
    
    public abstract int getTransformedEndWithDecoration(final View p0);
    
    public abstract int getTransformedStartWithDecoration(final View p0);
    
    public abstract void offsetChildren(final int p0);
    
    public void onLayoutComplete() {
        this.mLastTotalSpace = this.getTotalSpace();
    }
}
