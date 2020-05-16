// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ImageView;

public class ExpandableIndicator extends ImageView
{
    private boolean mExpanded;
    private boolean mIsDefaultDirection;
    
    public ExpandableIndicator(final Context context, final AttributeSet set) {
        super(context, set);
        this.mIsDefaultDirection = true;
    }
    
    private String getContentDescription(final boolean b) {
        String s;
        if (b) {
            s = super.mContext.getString(R$string.accessibility_quick_settings_collapse);
        }
        else {
            s = super.mContext.getString(R$string.accessibility_quick_settings_expand);
        }
        return s;
    }
    
    private int getDrawableResourceId(final boolean b) {
        if (this.mIsDefaultDirection) {
            int n;
            if (b) {
                n = R$drawable.ic_volume_collapse_animation;
            }
            else {
                n = R$drawable.ic_volume_expand_animation;
            }
            return n;
        }
        int n2;
        if (b) {
            n2 = R$drawable.ic_volume_expand_animation;
        }
        else {
            n2 = R$drawable.ic_volume_collapse_animation;
        }
        return n2;
    }
    
    private void updateIndicatorDrawable() {
        this.setImageResource(this.getDrawableResourceId(this.mExpanded));
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.updateIndicatorDrawable();
        this.setContentDescription((CharSequence)this.getContentDescription(this.mExpanded));
    }
    
    public void setExpanded(final boolean mExpanded) {
        if (mExpanded == this.mExpanded) {
            return;
        }
        this.mExpanded = mExpanded;
        final AnimatedVectorDrawable imageDrawable = (AnimatedVectorDrawable)this.getContext().getDrawable(this.getDrawableResourceId(mExpanded ^ true)).getConstantState().newDrawable();
        this.setImageDrawable((Drawable)imageDrawable);
        imageDrawable.forceAnimationOnUI();
        imageDrawable.start();
        this.setContentDescription((CharSequence)this.getContentDescription(mExpanded));
    }
}
