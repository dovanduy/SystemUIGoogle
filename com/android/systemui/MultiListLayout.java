// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.widget.BaseAdapter;
import android.view.View$OnClickListener;
import android.view.View$AccessibilityDelegate;
import android.content.res.Configuration;
import android.view.ViewGroup;
import com.android.systemui.util.leak.RotationUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public abstract class MultiListLayout extends LinearLayout
{
    protected MultiListAdapter mAdapter;
    protected int mRotation;
    protected RotationListener mRotationListener;
    
    public MultiListLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mRotation = RotationUtils.getRotation(context);
    }
    
    public abstract float getAnimationOffsetX();
    
    public abstract float getAnimationOffsetY();
    
    protected abstract ViewGroup getListView();
    
    protected abstract ViewGroup getSeparatedView();
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final int rotation = RotationUtils.getRotation(super.mContext);
        final int mRotation = this.mRotation;
        if (rotation != mRotation) {
            this.rotate(mRotation, rotation);
            this.mRotation = rotation;
        }
    }
    
    protected void onUpdateList() {
        this.removeAllItems();
        this.setSeparatedViewVisibility(this.mAdapter.hasSeparatedItems());
    }
    
    protected void removeAllItems() {
        this.removeAllListViews();
        this.removeAllSeparatedViews();
    }
    
    protected void removeAllListViews() {
        final ViewGroup listView = this.getListView();
        if (listView != null) {
            listView.removeAllViews();
        }
    }
    
    protected void removeAllSeparatedViews() {
        final ViewGroup separatedView = this.getSeparatedView();
        if (separatedView != null) {
            separatedView.removeAllViews();
        }
    }
    
    protected void rotate(final int n, final int n2) {
        final RotationListener mRotationListener = this.mRotationListener;
        if (mRotationListener != null) {
            mRotationListener.onRotate(n, n2);
        }
    }
    
    public void setAdapter(final MultiListAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }
    
    public void setListViewAccessibilityDelegate(final View$AccessibilityDelegate accessibilityDelegate) {
        this.getListView().setAccessibilityDelegate(accessibilityDelegate);
    }
    
    public void setOutsideTouchListener(final View$OnClickListener onClickListener) {
        this.requestLayout();
        this.setOnClickListener(onClickListener);
        this.setClickable(true);
        this.setFocusable(true);
    }
    
    public void setRotationListener(final RotationListener mRotationListener) {
        this.mRotationListener = mRotationListener;
    }
    
    protected void setSeparatedViewVisibility(final boolean b) {
        final ViewGroup separatedView = this.getSeparatedView();
        if (separatedView != null) {
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            separatedView.setVisibility(visibility);
        }
    }
    
    public void updateList() {
        if (this.mAdapter != null) {
            this.onUpdateList();
            return;
        }
        throw new IllegalStateException("mAdapter must be set before calling updateList");
    }
    
    public abstract static class MultiListAdapter extends BaseAdapter
    {
        public abstract int countListItems();
        
        public abstract int countSeparatedItems();
        
        public boolean hasSeparatedItems() {
            return this.countSeparatedItems() > 0;
        }
        
        public abstract boolean shouldBeSeparated(final int p0);
    }
    
    public interface RotationListener
    {
        void onRotate(final int p0, final int p1);
    }
}
