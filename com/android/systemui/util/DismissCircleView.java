// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import android.content.res.Configuration;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.R$dimen;
import android.content.res.Resources;
import android.view.View;
import com.android.systemui.R$drawable;
import android.content.Context;
import android.widget.ImageView;
import android.widget.FrameLayout;

public class DismissCircleView extends FrameLayout
{
    private final ImageView mIconView;
    
    public DismissCircleView(final Context context) {
        super(context);
        this.mIconView = new ImageView(this.getContext());
        final Resources resources = this.getResources();
        this.setBackground(resources.getDrawable(R$drawable.dismiss_circle_background));
        this.mIconView.setImageDrawable(resources.getDrawable(R$drawable.dismiss_target_x));
        this.addView((View)this.mIconView);
        this.setViewSizes();
    }
    
    private void setViewSizes() {
        final int dimensionPixelSize = this.getResources().getDimensionPixelSize(R$dimen.dismiss_target_x_size);
        this.mIconView.setLayoutParams((ViewGroup$LayoutParams)new FrameLayout$LayoutParams(dimensionPixelSize, dimensionPixelSize, 17));
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.setViewSizes();
    }
}
