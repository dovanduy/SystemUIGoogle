// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;

public abstract class CustomInterpolatorTransformation extends CustomTransformation
{
    private final int mViewType;
    
    public CustomInterpolatorTransformation(final int mViewType) {
        this.mViewType = mViewType;
    }
    
    protected abstract boolean hasCustomTransformation();
    
    @Override
    public boolean transformFrom(final TransformState transformState, final TransformableView transformableView, final float n) {
        if (!this.hasCustomTransformation()) {
            return false;
        }
        final TransformState currentState = transformableView.getCurrentState(this.mViewType);
        if (currentState == null) {
            return false;
        }
        CrossFadeHelper.fadeIn(transformState.getTransformedView(), n);
        transformState.transformViewFullyFrom(currentState, this, n);
        currentState.recycle();
        return true;
    }
    
    @Override
    public boolean transformTo(final TransformState transformState, final TransformableView transformableView, final float n) {
        if (!this.hasCustomTransformation()) {
            return false;
        }
        final TransformState currentState = transformableView.getCurrentState(this.mViewType);
        if (currentState == null) {
            return false;
        }
        CrossFadeHelper.fadeOut(transformState.getTransformedView(), n);
        transformState.transformViewFullyTo(currentState, this, n);
        currentState.recycle();
        return true;
    }
}
