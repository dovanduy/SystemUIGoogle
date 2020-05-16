// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.widget;

import android.graphics.drawable.Drawable;
import android.view.View$OnClickListener;

public class BarViewInfo implements Comparable<BarViewInfo>
{
    abstract View$OnClickListener getClickListener();
    
    public abstract CharSequence getContentDescription();
    
    abstract int getHeight();
    
    abstract Drawable getIcon();
    
    abstract int getNormalizedHeight();
    
    abstract CharSequence getSummary();
    
    abstract CharSequence getTitle();
    
    abstract void setNormalizedHeight(final int p0);
}
