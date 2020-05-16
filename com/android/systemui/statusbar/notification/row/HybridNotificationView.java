// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.View;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.notification.TransformState;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.ViewTransformationHelper;
import android.widget.TextView;
import com.android.systemui.statusbar.TransformableView;
import com.android.keyguard.AlphaOptimizedLinearLayout;

public class HybridNotificationView extends AlphaOptimizedLinearLayout implements TransformableView
{
    protected TextView mTextView;
    protected TextView mTitleView;
    private ViewTransformationHelper mTransformationHelper;
    
    public HybridNotificationView(final Context context) {
        this(context, null);
    }
    
    public HybridNotificationView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public HybridNotificationView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public HybridNotificationView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public void bind(final CharSequence text, final CharSequence charSequence) {
        this.mTitleView.setText(text);
        final TextView mTitleView = this.mTitleView;
        int visibility;
        if (TextUtils.isEmpty(text)) {
            visibility = 8;
        }
        else {
            visibility = 0;
        }
        mTitleView.setVisibility(visibility);
        if (TextUtils.isEmpty(charSequence)) {
            this.mTextView.setVisibility(8);
            this.mTextView.setText((CharSequence)null);
        }
        else {
            this.mTextView.setVisibility(0);
            this.mTextView.setText((CharSequence)charSequence.toString());
        }
        this.requestLayout();
    }
    
    @Override
    public TransformState getCurrentState(final int n) {
        return this.mTransformationHelper.getCurrentState(n);
    }
    
    public TextView getTextView() {
        return this.mTextView;
    }
    
    public TextView getTitleView() {
        return this.mTitleView;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitleView = (TextView)this.findViewById(R$id.notification_title);
        this.mTextView = (TextView)this.findViewById(R$id.notification_text);
        (this.mTransformationHelper = new ViewTransformationHelper()).setCustomTransformation((ViewTransformationHelper.CustomTransformation)new ViewTransformationHelper.CustomTransformation() {
            @Override
            public boolean transformFrom(final TransformState transformState, final TransformableView transformableView, final float n) {
                final TransformState currentState = transformableView.getCurrentState(1);
                CrossFadeHelper.fadeIn((View)HybridNotificationView.this.mTextView, n);
                if (currentState != null) {
                    transformState.transformViewVerticalFrom(currentState, n);
                    currentState.recycle();
                }
                return true;
            }
            
            @Override
            public boolean transformTo(final TransformState transformState, final TransformableView transformableView, final float n) {
                final TransformState currentState = transformableView.getCurrentState(1);
                CrossFadeHelper.fadeOut((View)HybridNotificationView.this.mTextView, n);
                if (currentState != null) {
                    transformState.transformViewVerticalTo(currentState, n);
                    currentState.recycle();
                }
                return true;
            }
        }, 2);
        this.mTransformationHelper.addTransformedView(1, (View)this.mTitleView);
        this.mTransformationHelper.addTransformedView(2, (View)this.mTextView);
    }
    
    @Override
    public void setVisible(final boolean visible) {
        int visibility;
        if (visible) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        this.setVisibility(visibility);
        this.mTransformationHelper.setVisible(visible);
    }
    
    @Override
    public void transformFrom(final TransformableView transformableView) {
        this.mTransformationHelper.transformFrom(transformableView);
    }
    
    @Override
    public void transformFrom(final TransformableView transformableView, final float n) {
        this.mTransformationHelper.transformFrom(transformableView, n);
    }
    
    @Override
    public void transformTo(final TransformableView transformableView, final float n) {
        this.mTransformationHelper.transformTo(transformableView, n);
    }
    
    @Override
    public void transformTo(final TransformableView transformableView, final Runnable runnable) {
        this.mTransformationHelper.transformTo(transformableView, runnable);
    }
}
