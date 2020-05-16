// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.qs;

import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.Dependencies;
import com.android.systemui.plugins.Plugin;

@Dependencies({ @DependsOn(target = QSTile.class), @DependsOn(target = QSTileView.class) })
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_QS_FACTORY", version = 1)
public interface QSFactory extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_QS_FACTORY";
    public static final int VERSION = 1;
    
    QSTile createTile(final String p0);
    
    QSTileView createTileView(final QSTile p0, final boolean p1);
}
