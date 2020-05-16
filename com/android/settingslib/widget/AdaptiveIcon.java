// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.util.Log;
import android.graphics.PorterDuff$Mode;
import android.graphics.drawable.Drawable$ConstantState;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;

public class AdaptiveIcon extends LayerDrawable
{
    private AdaptiveConstantState mAdaptiveConstantState;
    int mBackgroundColor;
    
    public AdaptiveIcon(final Context context, final Drawable drawable) {
        super(new Drawable[] { (Drawable)new AdaptiveIconShapeDrawable(context.getResources()), drawable });
        this.mBackgroundColor = -1;
        final int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.dashboard_tile_foreground_image_inset);
        this.setLayerInset(1, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        this.mAdaptiveConstantState = new AdaptiveConstantState(context, drawable);
    }
    
    public Drawable$ConstantState getConstantState() {
        return this.mAdaptiveConstantState;
    }
    
    public void setBackgroundColor(final int n) {
        this.mBackgroundColor = n;
        this.getDrawable(0).setColorFilter(n, PorterDuff$Mode.SRC_ATOP);
        final StringBuilder sb = new StringBuilder();
        sb.append("Setting background color ");
        sb.append(this.mBackgroundColor);
        Log.d("AdaptiveHomepageIcon", sb.toString());
        this.mAdaptiveConstantState.mColor = n;
    }
    
    static class AdaptiveConstantState extends Drawable$ConstantState
    {
        int mColor;
        Context mContext;
        Drawable mDrawable;
        
        AdaptiveConstantState(final Context mContext, final Drawable mDrawable) {
            this.mContext = mContext;
            this.mDrawable = mDrawable;
        }
        
        public int getChangingConfigurations() {
            return 0;
        }
        
        public Drawable newDrawable() {
            final AdaptiveIcon adaptiveIcon = new AdaptiveIcon(this.mContext, this.mDrawable);
            adaptiveIcon.setBackgroundColor(this.mColor);
            return (Drawable)adaptiveIcon;
        }
    }
}
