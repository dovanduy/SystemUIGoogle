// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import java.lang.ref.WeakReference;
import android.widget.BaseAdapter;
import android.database.DataSetObserver;
import android.view.View$MeasureSpec;
import android.view.View;
import android.content.res.TypedArray;
import com.android.systemui.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewGroup;

public class PseudoGridView extends ViewGroup
{
    private int mHorizontalSpacing;
    private int mNumColumns;
    private int mVerticalSpacing;
    
    public PseudoGridView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mNumColumns = 3;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.PseudoGridView);
        for (int indexCount = obtainStyledAttributes.getIndexCount(), i = 0; i < indexCount; ++i) {
            final int index = obtainStyledAttributes.getIndex(i);
            if (index == R$styleable.PseudoGridView_numColumns) {
                this.mNumColumns = obtainStyledAttributes.getInt(index, 3);
            }
            else if (index == R$styleable.PseudoGridView_verticalSpacing) {
                this.mVerticalSpacing = obtainStyledAttributes.getDimensionPixelSize(index, 0);
            }
            else if (index == R$styleable.PseudoGridView_horizontalSpacing) {
                this.mHorizontalSpacing = obtainStyledAttributes.getDimensionPixelSize(index, 0);
            }
        }
        obtainStyledAttributes.recycle();
    }
    
    protected void onLayout(final boolean b, int n, int i, int n2, int j) {
        final boolean layoutRtl = this.isLayoutRtl();
        final int childCount = this.getChildCount();
        int n3;
        int mNumColumns;
        int min;
        int max;
        View child;
        int measuredWidth;
        int measuredHeight;
        int n4;
        for (n = this.mNumColumns, n3 = (childCount + n - 1) / n, i = (n2 = 0); i < n3; ++i, n2 = n) {
            if (layoutRtl) {
                n = this.getWidth();
            }
            else {
                n = 0;
            }
            mNumColumns = this.mNumColumns;
            j = i * mNumColumns;
            min = Math.min(mNumColumns + j, childCount);
            max = 0;
            while (j < min) {
                child = this.getChildAt(j);
                measuredWidth = child.getMeasuredWidth();
                measuredHeight = child.getMeasuredHeight();
                n4 = n;
                if (layoutRtl) {
                    n4 = n - measuredWidth;
                }
                child.layout(n4, n2, n4 + measuredWidth, n2 + measuredHeight);
                max = Math.max(max, measuredHeight);
                if (layoutRtl) {
                    n = n4 - this.mHorizontalSpacing;
                }
                else {
                    n = n4 + (measuredWidth + this.mHorizontalSpacing);
                }
                ++j;
            }
            n2 = (n = n2 + max);
            if (i > 0) {
                n = n2 + this.mVerticalSpacing;
            }
        }
    }
    
    protected void onMeasure(int n, final int n2) {
        if (View$MeasureSpec.getMode(n) != 0) {
            final int size = View$MeasureSpec.getSize(n);
            n = this.mNumColumns;
            final int measureSpec = View$MeasureSpec.makeMeasureSpec((size - (n - 1) * this.mHorizontalSpacing) / n, 1073741824);
            final int childCount = this.getChildCount();
            n = this.mNumColumns;
            for (int n3 = (childCount + n - 1) / n, i = n = 0; i < n3; ++i) {
                final int mNumColumns = this.mNumColumns;
                int j = i * mNumColumns;
                final int min = Math.min(mNumColumns + j, childCount);
                int max = 0;
                for (int k = j; k < min; ++k) {
                    final View child = this.getChildAt(k);
                    child.measure(measureSpec, 0);
                    max = Math.max(max, child.getMeasuredHeight());
                }
                final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(max, 1073741824);
                while (j < min) {
                    final View child2 = this.getChildAt(j);
                    if (child2.getMeasuredHeight() != max) {
                        child2.measure(measureSpec, measureSpec2);
                    }
                    ++j;
                }
                final int n4 = n += max;
                if (i > 0) {
                    n = n4 + this.mVerticalSpacing;
                }
            }
            this.setMeasuredDimension(size, ViewGroup.resolveSizeAndState(n, n2, 0));
            return;
        }
        throw new UnsupportedOperationException("Needs a maximum width");
    }
    
    public static class ViewGroupAdapterBridge extends DataSetObserver
    {
        private final BaseAdapter mAdapter;
        private boolean mReleased;
        private final WeakReference<ViewGroup> mViewGroup;
        
        private ViewGroupAdapterBridge(final ViewGroup referent, final BaseAdapter mAdapter) {
            this.mViewGroup = new WeakReference<ViewGroup>(referent);
            this.mAdapter = mAdapter;
            this.mReleased = false;
            mAdapter.registerDataSetObserver((DataSetObserver)this);
            this.refresh();
        }
        
        public static void link(final ViewGroup viewGroup, final BaseAdapter baseAdapter) {
            new ViewGroupAdapterBridge(viewGroup, baseAdapter);
        }
        
        private void refresh() {
            if (this.mReleased) {
                return;
            }
            final ViewGroup viewGroup = this.mViewGroup.get();
            if (viewGroup == null) {
                this.release();
                return;
            }
            final int childCount = viewGroup.getChildCount();
            final int count = this.mAdapter.getCount();
            for (int max = Math.max(childCount, count), i = 0; i < max; ++i) {
                if (i < count) {
                    View child = null;
                    if (i < childCount) {
                        child = viewGroup.getChildAt(i);
                    }
                    final View view = this.mAdapter.getView(i, child, viewGroup);
                    if (child == null) {
                        viewGroup.addView(view);
                    }
                    else if (child != view) {
                        viewGroup.removeViewAt(i);
                        viewGroup.addView(view, i);
                    }
                }
                else {
                    viewGroup.removeViewAt(viewGroup.getChildCount() - 1);
                }
            }
        }
        
        private void release() {
            if (!this.mReleased) {
                this.mReleased = true;
                this.mAdapter.unregisterDataSetObserver((DataSetObserver)this);
            }
        }
        
        public void onChanged() {
            this.refresh();
        }
        
        public void onInvalidated() {
            this.release();
        }
    }
}
