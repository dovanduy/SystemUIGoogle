// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.qs;

import android.view.View;
import android.content.Context;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.Dependencies;
import android.widget.LinearLayout;

@Dependencies({ @DependsOn(target = QSIconView.class), @DependsOn(target = QSTile.class) })
@ProvidesInterface(version = 2)
public abstract class QSTileView extends LinearLayout
{
    public static final int VERSION = 2;
    
    public QSTileView(final Context context) {
        super(context);
    }
    
    public abstract int getDetailY();
    
    public abstract QSIconView getIcon();
    
    public abstract View getIconWithBackground();
    
    public abstract void init(final QSTile p0);
    
    public abstract void onStateChanged(final QSTile.State p0);
    
    public abstract View updateAccessibilityOrder(final View p0);
}
