// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Color;
import android.content.Context;
import android.view.ContextThemeWrapper;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import android.view.View;

public class RotationContextButton extends ContextualButton implements ModeChangedListener, RotationButton
{
    private RotationButtonController mRotationButtonController;
    
    public RotationContextButton(final int n, final int n2) {
        super(n, n2);
    }
    
    @Override
    public boolean acceptRotationProposal() {
        final View currentView = this.getCurrentView();
        return currentView != null && currentView.isAttachedToWindow();
    }
    
    @Override
    protected KeyButtonDrawable getNewDrawable() {
        return KeyButtonDrawable.create((Context)new ContextThemeWrapper(this.getContext().getApplicationContext(), this.mRotationButtonController.getStyleRes()), super.mIconResId, false, null);
    }
    
    @Override
    public void onNavigationModeChanged(final int n) {
    }
    
    @Override
    public void setRotationButtonController(final RotationButtonController mRotationButtonController) {
        this.mRotationButtonController = mRotationButtonController;
    }
    
    @Override
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        final KeyButtonDrawable imageDrawable = this.getImageDrawable();
        if (visibility == 0 && imageDrawable != null) {
            imageDrawable.resetAnimation();
            imageDrawable.startAnimation();
        }
    }
}
