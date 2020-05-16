// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.qs;

import android.view.View;
import android.content.Context;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import android.view.ViewGroup;

@ProvidesInterface(version = 1)
public abstract class QSIconView extends ViewGroup
{
    public static final int VERSION = 1;
    
    public QSIconView(final Context context) {
        super(context);
    }
    
    public abstract void disableAnimation();
    
    public abstract View getIconView();
    
    public abstract void setIcon(final QSTile.State p0, final boolean p1);
}
