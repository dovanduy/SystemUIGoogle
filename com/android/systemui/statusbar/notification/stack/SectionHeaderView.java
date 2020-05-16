// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.content.res.ColorStateList;
import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import android.view.View$OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;

public class SectionHeaderView extends StackScrollerDecorView
{
    private ImageView mClearAllButton;
    private ViewGroup mContents;
    private View$OnClickListener mLabelClickListener;
    private Integer mLabelTextId;
    private TextView mLabelView;
    private View$OnClickListener mOnClearClickListener;
    
    public SectionHeaderView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mLabelClickListener = null;
        this.mOnClearClickListener = null;
    }
    
    private void bindContents() {
        this.mLabelView = (TextView)this.requireViewById(R$id.header_label);
        final ImageView mClearAllButton = (ImageView)this.requireViewById(R$id.btn_clear_all);
        this.mClearAllButton = mClearAllButton;
        final View$OnClickListener mOnClearClickListener = this.mOnClearClickListener;
        if (mOnClearClickListener != null) {
            mClearAllButton.setOnClickListener(mOnClearClickListener);
        }
        final View$OnClickListener mLabelClickListener = this.mLabelClickListener;
        if (mLabelClickListener != null) {
            this.mLabelView.setOnClickListener(mLabelClickListener);
        }
        final Integer mLabelTextId = this.mLabelTextId;
        if (mLabelTextId != null) {
            this.mLabelView.setText((int)mLabelTextId);
        }
    }
    
    @Override
    protected void applyContentTransformation(final float n, final float n2) {
        super.applyContentTransformation(n, n2);
        this.mLabelView.setAlpha(n);
        this.mLabelView.setTranslationY(n2);
        this.mClearAllButton.setAlpha(n);
        this.mClearAllButton.setTranslationY(n2);
    }
    
    @Override
    protected View findContentView() {
        return (View)this.mContents;
    }
    
    @Override
    protected View findSecondaryView() {
        return null;
    }
    
    @Override
    public boolean isTransparent() {
        return true;
    }
    
    @Override
    public boolean needsClippingToShelf() {
        return true;
    }
    
    @Override
    protected void onFinishInflate() {
        this.mContents = (ViewGroup)this.requireViewById(R$id.content);
        this.bindContents();
        super.onFinishInflate();
        this.setVisible(true, false);
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return super.onInterceptTouchEvent(motionEvent);
    }
    
    void setAreThereDismissableGentleNotifs(final boolean b) {
        final ImageView mClearAllButton = this.mClearAllButton;
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mClearAllButton.setVisibility(visibility);
    }
    
    void setForegroundColor(final int textColor) {
        this.mLabelView.setTextColor(textColor);
        this.mClearAllButton.setImageTintList(ColorStateList.valueOf(textColor));
    }
    
    void setHeaderText(final int n) {
        this.mLabelTextId = n;
        this.mLabelView.setText(n);
    }
    
    void setOnClearAllClickListener(final View$OnClickListener view$OnClickListener) {
        this.mOnClearClickListener = view$OnClickListener;
        this.mClearAllButton.setOnClickListener(view$OnClickListener);
    }
    
    void setOnHeaderClickListener(final View$OnClickListener view$OnClickListener) {
        this.mLabelClickListener = view$OnClickListener;
        this.mLabelView.setOnClickListener(view$OnClickListener);
    }
}
