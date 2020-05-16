// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tileimpl;

import android.graphics.drawable.Drawable;
import com.android.systemui.plugins.qs.QSTile;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.qs.SlashDrawable;
import android.widget.ImageView;

public class SlashImageView extends ImageView
{
    private boolean mAnimationEnabled;
    @VisibleForTesting
    protected SlashDrawable mSlash;
    
    public SlashImageView(final Context context) {
        super(context);
        this.mAnimationEnabled = true;
    }
    
    private void setSlashState(final QSTile.SlashState slashState) {
        this.ensureSlashDrawable();
        this.mSlash.setRotation(slashState.rotation);
        this.mSlash.setSlashed(slashState.isSlashed);
    }
    
    protected void ensureSlashDrawable() {
        if (this.mSlash == null) {
            (this.mSlash = new SlashDrawable(this.getDrawable())).setAnimationEnabled(this.mAnimationEnabled);
            super.setImageDrawable((Drawable)this.mSlash);
        }
    }
    
    public boolean getAnimationEnabled() {
        return this.mAnimationEnabled;
    }
    
    protected SlashDrawable getSlash() {
        return this.mSlash;
    }
    
    public void setAnimationEnabled(final boolean mAnimationEnabled) {
        this.mAnimationEnabled = mAnimationEnabled;
    }
    
    public void setImageDrawable(final Drawable drawable) {
        if (drawable == null) {
            this.mSlash = null;
            super.setImageDrawable((Drawable)null);
        }
        else {
            final SlashDrawable mSlash = this.mSlash;
            if (mSlash == null) {
                this.setImageLevel(drawable.getLevel());
                super.setImageDrawable(drawable);
            }
            else {
                mSlash.setAnimationEnabled(this.mAnimationEnabled);
                this.mSlash.setDrawable(drawable);
            }
        }
    }
    
    protected void setImageViewDrawable(final SlashDrawable imageDrawable) {
        super.setImageDrawable((Drawable)imageDrawable);
    }
    
    protected void setSlash(final SlashDrawable mSlash) {
        this.mSlash = mSlash;
    }
    
    public void setState(final QSTile.SlashState slashState, final Drawable drawable) {
        if (slashState != null) {
            this.setImageDrawable(drawable);
            this.setSlashState(slashState);
        }
        else {
            this.mSlash = null;
            this.setImageDrawable(drawable);
        }
    }
}
