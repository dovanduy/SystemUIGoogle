// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View$MeasureSpec;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.TypedArray;
import com.android.systemui.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Handler;
import android.database.DataSetObserver;
import android.widget.ListAdapter;
import android.widget.LinearLayout;

public class AutoSizingList extends LinearLayout
{
    private ListAdapter mAdapter;
    private final Runnable mBindChildren;
    private int mCount;
    private final DataSetObserver mDataObserver;
    private boolean mEnableAutoSizing;
    private final Handler mHandler;
    private final int mItemSize;
    
    public AutoSizingList(final Context context, final AttributeSet set) {
        super(context, set);
        this.mBindChildren = new Runnable() {
            @Override
            public void run() {
                AutoSizingList.this.rebindChildren();
            }
        };
        this.mDataObserver = new DataSetObserver() {
            public void onChanged() {
                if (AutoSizingList.this.mCount > AutoSizingList.this.getDesiredCount()) {
                    final AutoSizingList this$0 = AutoSizingList.this;
                    this$0.mCount = this$0.getDesiredCount();
                }
                AutoSizingList.this.postRebindChildren();
            }
            
            public void onInvalidated() {
                AutoSizingList.this.postRebindChildren();
            }
        };
        this.mHandler = new Handler();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.AutoSizingList);
        this.mItemSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.AutoSizingList_itemHeight, 0);
        this.mEnableAutoSizing = obtainStyledAttributes.getBoolean(R$styleable.AutoSizingList_enableAutoSizing, true);
        obtainStyledAttributes.recycle();
    }
    
    private int getDesiredCount() {
        final ListAdapter mAdapter = this.mAdapter;
        int count;
        if (mAdapter != null) {
            count = mAdapter.getCount();
        }
        else {
            count = 0;
        }
        return count;
    }
    
    private int getItemCount(final int n) {
        int b = this.getDesiredCount();
        if (this.mEnableAutoSizing) {
            b = Math.min(n / this.mItemSize, b);
        }
        return b;
    }
    
    private void postRebindChildren() {
        this.mHandler.post(this.mBindChildren);
    }
    
    private void rebindChildren() {
        if (this.mAdapter == null) {
            return;
        }
        for (int i = 0; i < this.mCount; ++i) {
            View child;
            if (i < this.getChildCount()) {
                child = this.getChildAt(i);
            }
            else {
                child = null;
            }
            final View view = this.mAdapter.getView(i, child, (ViewGroup)this);
            if (view != child) {
                if (child != null) {
                    this.removeView(child);
                }
                this.addView(view, i);
            }
        }
        while (this.getChildCount() > this.mCount) {
            this.removeViewAt(this.getChildCount() - 1);
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int size = View$MeasureSpec.getSize(n2);
        if (size != 0) {
            final int itemCount = this.getItemCount(size);
            if (this.mCount != itemCount) {
                this.postRebindChildren();
                this.mCount = itemCount;
            }
        }
        super.onMeasure(n, n2);
    }
    
    public void setAdapter(final ListAdapter mAdapter) {
        final ListAdapter mAdapter2 = this.mAdapter;
        if (mAdapter2 != null) {
            mAdapter2.unregisterDataSetObserver(this.mDataObserver);
        }
        if ((this.mAdapter = mAdapter) != null) {
            mAdapter.registerDataSetObserver(this.mDataObserver);
        }
    }
}
