// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import android.content.Context;

public class ContextualButton extends ButtonDispatcher
{
    private ContextualButtonGroup mGroup;
    protected final int mIconResId;
    private ContextButtonListener mListener;
    
    public ContextualButton(final int n, final int mIconResId) {
        super(n);
        this.mIconResId = mIconResId;
    }
    
    void attachToGroup(final ContextualButtonGroup mGroup) {
        this.mGroup = mGroup;
    }
    
    protected Context getContext() {
        return this.getCurrentView().getContext();
    }
    
    protected KeyButtonDrawable getNewDrawable() {
        return KeyButtonDrawable.create(this.getContext().getApplicationContext(), this.mIconResId, false);
    }
    
    public boolean hide() {
        final ContextualButtonGroup mGroup = this.mGroup;
        boolean b = false;
        if (mGroup == null) {
            this.setVisibility(4);
            return false;
        }
        if (mGroup.setButtonVisibility(this.getId(), false) != 0) {
            b = true;
        }
        return b;
    }
    
    public void setListener(final ContextButtonListener mListener) {
        this.mListener = mListener;
    }
    
    @Override
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        final KeyButtonDrawable imageDrawable = this.getImageDrawable();
        if (visibility != 0 && imageDrawable != null && imageDrawable.canAnimate()) {
            imageDrawable.clearAnimationCallbacks();
            imageDrawable.resetAnimation();
        }
        final ContextButtonListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onVisibilityChanged(this, visibility == 0);
        }
    }
    
    public boolean show() {
        final ContextualButtonGroup mGroup = this.mGroup;
        boolean b = false;
        if (mGroup == null) {
            this.setVisibility(0);
            return true;
        }
        if (mGroup.setButtonVisibility(this.getId(), true) == 0) {
            b = true;
        }
        return b;
    }
    
    public void updateIcon() {
        if (this.getCurrentView() != null && this.getCurrentView().isAttachedToWindow()) {
            if (this.mIconResId != 0) {
                final KeyButtonDrawable imageDrawable = this.getImageDrawable();
                final KeyButtonDrawable newDrawable = this.getNewDrawable();
                if (imageDrawable != null) {
                    newDrawable.setDarkIntensity(imageDrawable.getDarkIntensity());
                }
                this.setImageDrawable(newDrawable);
            }
        }
    }
    
    public interface ContextButtonListener
    {
        void onVisibilityChanged(final ContextualButton p0, final boolean p1);
    }
}
