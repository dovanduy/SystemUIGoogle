// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import com.android.systemui.qs.tileimpl.SlashImageView;
import android.content.Context;

public class AlphaControlledSignalTileView extends SignalTileView
{
    public AlphaControlledSignalTileView(final Context context) {
        super(context);
    }
    
    @Override
    protected SlashImageView createSlashImageView(final Context context) {
        return new AlphaControlledSlashImageView(context);
    }
    
    public static class AlphaControlledSlashDrawable extends SlashDrawable
    {
        AlphaControlledSlashDrawable(final Drawable drawable) {
            super(drawable);
        }
        
        @Override
        protected void setDrawableTintList(final ColorStateList list) {
        }
        
        public void setFinalTintList(final ColorStateList drawableTintList) {
            super.setDrawableTintList(drawableTintList);
        }
    }
    
    public static class AlphaControlledSlashImageView extends SlashImageView
    {
        public AlphaControlledSlashImageView(final Context context) {
            super(context);
        }
        
        @Override
        protected void ensureSlashDrawable() {
            if (this.getSlash() == null) {
                final AlphaControlledSlashDrawable alphaControlledSlashDrawable = new AlphaControlledSlashDrawable(this.getDrawable());
                this.setSlash(alphaControlledSlashDrawable);
                alphaControlledSlashDrawable.setAnimationEnabled(this.getAnimationEnabled());
                this.setImageViewDrawable(alphaControlledSlashDrawable);
            }
        }
        
        public void setFinalImageTintList(final ColorStateList list) {
            super.setImageTintList(list);
            final SlashDrawable slash = this.getSlash();
            if (slash != null) {
                ((AlphaControlledSlashDrawable)slash).setFinalTintList(list);
            }
        }
    }
}
