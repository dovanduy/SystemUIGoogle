// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.view.View;
import android.graphics.Path;
import android.graphics.Bitmap;

interface BubbleViewProvider
{
    Bitmap getBadgedImage();
    
    int getDisplayId();
    
    int getDotColor();
    
    Path getDotPath();
    
    BubbleExpandedView getExpandedView();
    
    View getIconView();
    
    String getKey();
    
    void logUIEvent(final int p0, final int p1, final float p2, final float p3, final int p4);
    
    void setContentVisibility(final boolean p0);
    
    boolean showDot();
}
